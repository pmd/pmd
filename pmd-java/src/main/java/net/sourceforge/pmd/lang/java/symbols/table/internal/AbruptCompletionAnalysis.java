/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTBreakStatement;
import net.sourceforge.pmd.lang.java.ast.ASTCatchClause;
import net.sourceforge.pmd.lang.java.ast.ASTContinueStatement;
import net.sourceforge.pmd.lang.java.ast.ASTDoStatement;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTFinallyClause;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTForeachStatement;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTLabeledStatement;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTStatement;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchArrowBranch;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchArrowRHS;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchBranch;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchFallthroughBranch;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTSynchronizedStatement;
import net.sourceforge.pmd.lang.java.ast.ASTThrowStatement;
import net.sourceforge.pmd.lang.java.ast.ASTTryStatement;
import net.sourceforge.pmd.lang.java.ast.ASTWhileStatement;
import net.sourceforge.pmd.lang.java.ast.ASTYieldStatement;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.JavaVisitorBase;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;
import net.sourceforge.pmd.lang.java.symbols.table.internal.AbruptCompletionAnalysis.ReachabilityVisitor.VisitResult;
import net.sourceforge.pmd.util.AssertionUtil;

/**
 * Implementation of {@link #canCompleteNormally(ASTStatement)}, which
 * is used to implement scoping of pattern variables.
 *
 * @see <a href="https://docs.oracle.com/javase/specs/jls/se17/html/jls-14.html#jls-14.22">Java Language Specification</a>
 */
final class AbruptCompletionAnalysis {

    private AbruptCompletionAnalysis() {
        // util class
    }

    /**
     * Returns whether the statement can complete normally, assuming
     * it is reachable. For instance, an expression statement may
     * complete normally, while a return or throw always complete abruptly.
     */
    static boolean canCompleteNormally(ASTStatement stmt) {
        return stmt.acceptVisitor(new ReachabilityVisitor(), new SubtreeState(f -> VisitResult.Continue));
    }

    /**
     * This visitor implements a reachability analysis: it only visits
     * reachable statements in a given subtree (assuming the first
     * statement visited is reachable). Statements are not visited in
     * a particular order, the only guarantee is that reachable statements
     * are visited. Note that the right-hand side of switch arrow branches
     * is not visited unless it is a statement.
     *
     * The return value of the visitor is whether the visited statement
     * can complete normally.
     */
    static final class ReachabilityVisitor extends JavaVisitorBase<SubtreeState, Boolean> {

        enum VisitResult {
            Continue,
            Abort
        }

        static class VisitAbortedException extends RuntimeException {

            static final VisitAbortedException INSTANCE = new VisitAbortedException();
        }

        @Override
        public Boolean visit(ASTBlock node, SubtreeState data) {
            recordReachableNode(node, data);
            return blockCanCompleteNormally(node, data);
        }

        private boolean blockCanCompleteNormally(Iterable<ASTStatement> node, SubtreeState data) {
            for (ASTStatement statement : node) {
                boolean canCompleteNormally = statement.acceptVisitor(this, new SubtreeState(data));
                if (!canCompleteNormally) {
                    // and further statements are unreachable
                    return false;
                }
            }
            return true;
        }

        @Override
        public Boolean visitJavaNode(JavaNode node, SubtreeState data) {
            throw AssertionUtil.shouldNotReachHere("Cannot visit non-statements");
        }

        @Override
        public Boolean visitStatement(ASTStatement node, SubtreeState data) {
            // assert, empty stmt
            recordReachableNode(node, data);
            return true;
        }

        @Override
        public Boolean visit(ASTThrowStatement node, SubtreeState data) {
            recordReachableNode(node, data);
            return false;
        }

        @Override
        public Boolean visit(ASTReturnStatement node, SubtreeState data) {
            recordReachableNode(node, data);
            return false;
        }

        @Override
        public Boolean visit(ASTBreakStatement node, SubtreeState data) {
            recordReachableNode(node, data);
            data.addBreak(node);
            return false;
        }

        @Override
        public Boolean visit(ASTYieldStatement node, SubtreeState data) {
            recordReachableNode(node, data);
            data.addYield(node);
            return false;
        }

        @Override
        public Boolean visit(ASTContinueStatement node, SubtreeState data) {
            recordReachableNode(node, data);
            data.addContinue(node);
            return false;
        }

        @Override
        public Boolean visit(ASTIfStatement node, SubtreeState data) {
            recordReachableNode(node, data);

            boolean thenCanCompleteNormally = node.getThenBranch().acceptVisitor(this, data);
            boolean elseCanCompleteNormally = node.getElseBranch() == null
                || node.getElseBranch().acceptVisitor(this, data);

            return thenCanCompleteNormally || elseCanCompleteNormally;
        }

        @Override
        public Boolean visit(ASTSynchronizedStatement node, SubtreeState data) {
            recordReachableNode(node, data);
            return node.getBody().acceptVisitor(this, data);
        }

        @Override
        public Boolean visit(ASTLabeledStatement node, SubtreeState data) {
            recordReachableNode(node, data);
            boolean stmtCanCompleteNormally = node.getStatement().acceptVisitor(this, data);
            return stmtCanCompleteNormally || data.containsBreak(node);
        }

        @Override
        public Boolean visit(ASTForeachStatement node, SubtreeState data) {
            recordReachableNode(node, data);
            node.getBody().acceptVisitor(this, data);
            return true;
        }

        @Override
        public Boolean visit(ASTDoStatement node, SubtreeState data) {
            recordReachableNode(node, data);
            boolean bodyCompletesNormally = node.getBody().acceptVisitor(this, data);

            boolean isNotDoWhileTrue = !JavaAstUtils.isBooleanLiteral(node.getCondition(), true);

            return isNotDoWhileTrue && (bodyCompletesNormally || data.containsContinue(node))
                || data.containsBreak(node);
        }

        @Override
        public Boolean visit(ASTSwitchStatement node, SubtreeState data) {

            // note: exhaustive enum switches are NOT considered exhaustive
            // for the purposes of liveness analysis, only the presence of a
            // default case matters. Otherwise liveness analysis would depend
            // on type resolution and bad things would happen.
            boolean isExhaustive = node.hasDefaultCase();

            boolean completesNormally = true;
            boolean first = true;
            for (ASTSwitchBranch branch : node.getBranches()) {
                boolean branchCompletesNormally;

                if (branch instanceof ASTSwitchArrowBranch) {
                    ASTSwitchArrowRHS rhs = ((ASTSwitchArrowBranch) branch).getRightHandSide();
                    branchCompletesNormally = switchArrowBranchCompletesNormally(new SubtreeState(data), node, rhs);

                } else if (branch instanceof ASTSwitchFallthroughBranch) {
                    NodeStream<ASTStatement> statements = ((ASTSwitchFallthroughBranch) branch).getStatements();
                    SubtreeState branchState = new SubtreeState(data);
                    branchCompletesNormally = blockCanCompleteNormally(statements, branchState)
                        || branchState.containsBreak(node);
                } else {
                    throw AssertionUtil.shouldNotReachHere("Not a branch type :" + branch);
                }

                if (isExhaustive && first) {
                    completesNormally = branchCompletesNormally;
                    first = false;
                } else {
                    completesNormally = completesNormally || branchCompletesNormally;
                }
            }

            return completesNormally;

        }

        private boolean switchArrowBranchCompletesNormally(SubtreeState state, ASTSwitchStatement switchStmt, ASTSwitchArrowRHS rhs) {
            if (rhs instanceof ASTExpression) {
                return true;
            } else if (rhs instanceof ASTThrowStatement) {
                return false;
            } else if (rhs instanceof ASTBlock) {
                return ((ASTBlock) rhs).acceptVisitor(this, state) || state.containsBreak(switchStmt);
            } else {
                throw AssertionUtil.shouldNotReachHere("not a branch RHS: " + rhs);
            }
        }

        @Override
        public Boolean visit(ASTWhileStatement node, SubtreeState data) {
            recordReachableNode(node, data);

            node.getBody().acceptVisitor(this, data);
            boolean isNotWhileTrue = !JavaAstUtils.isBooleanLiteral(node.getCondition(), true);

            return isNotWhileTrue || data.containsBreak(node);
        }


        @Override
        public Boolean visit(ASTForStatement node, SubtreeState data) {
            recordReachableNode(node, data);

            node.getBody().acceptVisitor(this, data);
            boolean isNotForTrue = node.getCondition() != null
                && !JavaAstUtils.isBooleanLiteral(node.getCondition(), true);

            return isNotForTrue || data.containsBreak(node);
        }


        @Override
        public Boolean visit(ASTTryStatement node, SubtreeState data) {
            recordReachableNode(node, data);

            ASTFinallyClause finallyClause = node.getFinallyClause();
            boolean finallyCompletesNormally = true;
            if (finallyClause != null) {
                finallyCompletesNormally = finallyClause.getBody().acceptVisitor(this, new SubtreeState(data));
            }

            SubtreeState bodyState = tryClauseState(data, finallyCompletesNormally);
            boolean bodyCompletesNormally = node.getBody().acceptVisitor(this, bodyState);

            /*
            Todo catch clauses are not automatically reachable.
            A catch block C is reachable iff both of the following are true:
               - Either the type of C's parameter is an unchecked exception type
                 or Exception or a superclass of Exception, or some expression
                 or throw statement in the try block is reachable and can throw
                 a checked exception whose type is assignment compatible (§5.2)
                 with the type of C's parameter. (An expression is reachable iff
                 the innermost statement containing it is reachable.)
               - There is no earlier catch block A in the try statement such that
                 the type of C's parameter is the same as, or a subclass of,
                 the type of A's parameter.
             */
            boolean anyCatchClauseCompletesNormally = false;
            for (ASTCatchClause catchClause : node.getCatchClauses()) {
                SubtreeState subtree = tryClauseState(data, finallyCompletesNormally);
                anyCatchClauseCompletesNormally |= catchClause.getBody().acceptVisitor(this, subtree);
            }

            return finallyCompletesNormally
                && (bodyCompletesNormally || anyCatchClauseCompletesNormally);
        }

        private SubtreeState tryClauseState(SubtreeState data, boolean finallyCompletesNormally) {
            SubtreeState bodyState = new SubtreeState(data);
            if (!finallyCompletesNormally) {
                bodyState.ignoreBreaksAndContinues = true;
            }
            return bodyState;
        }

        private void recordReachableNode(ASTStatement node, SubtreeState data) {
            if (data.shouldContinue.apply(node) == VisitResult.Abort) {
                throw abortVisit();
            }
        }

        private static @NonNull VisitAbortedException abortVisit() {
            return VisitAbortedException.INSTANCE;
        }
    }

    /**
     * Tracks exploration state of a subtree. A new one is created for
     * independent subtrees (eg in the handling for blocks). Children
     * state instances forward events to their parent (as these events
     * are part of the subtree of their parents).
     */
    private static class SubtreeState {

        private final @Nullable SubtreeState parent;
        public boolean ignoreBreaksAndContinues;
        private Set<JavaNode> breakTargets = Collections.emptySet();
        private Set<ASTStatement> continueTargets = Collections.emptySet();

        private final Function<? super ASTStatement, VisitResult> shouldContinue;

        SubtreeState(Function<? super ASTStatement, VisitResult> shouldContinue) {
            this.parent = null;
            this.shouldContinue = shouldContinue;
        }

        SubtreeState(SubtreeState parent) {
            this.parent = parent;
            this.shouldContinue = parent.shouldContinue;
        }

        public boolean isIgnoreBreaksAndContinues() {
            return ignoreBreaksAndContinues || parent != null && parent.isIgnoreBreaksAndContinues();
        }

        boolean containsBreak(ASTStatement stmt) {
            return breakTargets.contains(stmt);
        }

        boolean containsContinue(ASTStatement stmt) {
            return continueTargets.contains(stmt);
        }

        void addBreak(ASTBreakStatement breakStatement) {
            addBreakImpl(breakStatement.getTarget());
        }

        void addYield(ASTYieldStatement yieldStmt) {
            addBreakImpl(yieldStmt.getYieldTarget());
        }

        private void addBreakImpl(JavaNode breakTargetStatement) {
            if (isIgnoreBreaksAndContinues()) {
                return;
            }

            if (breakTargets.isEmpty()) {
                breakTargets = new HashSet<>();
            }
            breakTargets.add(breakTargetStatement);
            if (parent != null) {
                parent.addBreakImpl(breakTargetStatement);
            }
        }

        void addContinue(ASTContinueStatement continueStatement) {
            if (isIgnoreBreaksAndContinues()) {
                return;
            }

            if (continueTargets.isEmpty()) {
                continueTargets = new HashSet<>();
            }
            continueTargets.add(continueStatement.getTarget());
            if (parent != null) {
                parent.addContinue(continueStatement);
            }
        }

    }
}
