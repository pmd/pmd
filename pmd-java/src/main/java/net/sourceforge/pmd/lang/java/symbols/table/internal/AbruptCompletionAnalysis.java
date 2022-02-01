/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal;

import static net.sourceforge.pmd.util.OptionalBool.NO;
import static net.sourceforge.pmd.util.OptionalBool.UNKNOWN;
import static net.sourceforge.pmd.util.OptionalBool.YES;
import static net.sourceforge.pmd.util.OptionalBool.join;
import static net.sourceforge.pmd.util.OptionalBool.max;
import static net.sourceforge.pmd.util.OptionalBool.min;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.internal.util.AssertionUtil;
import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTBreakStatement;
import net.sourceforge.pmd.lang.java.ast.ASTContinueStatement;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTLabeledStatement;
import net.sourceforge.pmd.lang.java.ast.ASTLoopStatement;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTStatement;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchArrowBranch;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchArrowRHS;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchBranch;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchFallthroughBranch;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTSynchronizedStatement;
import net.sourceforge.pmd.lang.java.ast.ASTThrowStatement;
import net.sourceforge.pmd.lang.java.ast.ASTWhileStatement;
import net.sourceforge.pmd.lang.java.ast.ASTYieldStatement;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil;
import net.sourceforge.pmd.util.OptionalBool;

/**
 * Implementation of {@link #completesNormally(ASTStatement)}, which
 * is used to implement scoping of pattern variables.
 *
 * @see <a href="https://docs.oracle.com/javase/specs/jls/se17/html/jls-14.html#jls-14.1">Java Language Specification</a>
 */
final class AbruptCompletionAnalysis {

    private AbruptCompletionAnalysis() {
        // util class
    }

    /**
     * Returns whether the statement can complete normally. For instance,
     * an expression statement may complete normally, while a return or throw
     * always complete abruptly.
     */
    static boolean canCompleteNormally(ASTStatement stmt) {
        return completesNormally(stmt) != NO;
    }

    static OptionalBool completesNormally(ASTStatement stmt) {
        return completesNormally(stmt, new State(null));
    }


    static OptionalBool completesNormally(ASTStatement stmt, State state) {
        if (stmt instanceof ASTThrowStatement || stmt instanceof ASTReturnStatement) {

            state.setReturnOrThrow(true);
            return NO;

        } else if (stmt instanceof ASTBreakStatement) {

            state.addBreak((ASTBreakStatement) stmt);
            return NO;

        } else if (stmt instanceof ASTYieldStatement) {

            // note that switch expressions MUST complete normally and
            // this is enforced by the compiler (which is why this routine
            // only applies to statements). However, the individual
            // cases may contain blocks, and within them, yield statements
            // are similar to a break statement.
            state.addYield((ASTYieldStatement) stmt);
            return NO;

        } else if (stmt instanceof ASTContinueStatement) {

            state.addContinue((ASTContinueStatement) stmt);
            return NO;

        } else if (stmt instanceof ASTBlock) {

            return handleBlockLike(((ASTBlock) stmt).toStream(), state);

        } else if (stmt instanceof ASTIfStatement) {
            ASTIfStatement ifStmt = (ASTIfStatement) stmt;

            ASTStatement thenBranch = ifStmt.getThenBranch();
            ASTStatement elseBranch = ifStmt.getElseBranch();

            if (elseBranch == null) {
                // yes -> yes
                // unk -> unk
                // no -> unk

                // this max accounts for the case when the branch
                // is never executed, which is a normal completion
                return max(UNKNOWN, completesNormally(thenBranch, state));
            } else {
                // yes, yes -> yes
                // yes, unk -> unk
                // no, unk -> unk
                // no, no -> no

                return join(completesNormally(thenBranch, state),
                            completesNormally(elseBranch, state));
            }

        } else if (stmt instanceof ASTLabeledStatement) {

            State subState = new State(state);
            OptionalBool completesNormally = completesNormally(((ASTLabeledStatement) stmt).getStatement(), subState);
            // note: here we pass the labeled statement while completesNormally was computed with the enclosed statement.
            return doesBreakTargetCompleteNormally(stmt, subState, completesNormally);

        } else if (stmt instanceof ASTSynchronizedStatement) {

            return completesNormally(((ASTSynchronizedStatement) stmt).getBody(), state);

        } else if (stmt instanceof ASTSwitchStatement) {

            return handleSwitch(state, (ASTSwitchStatement) stmt);

        } else if (stmt instanceof ASTWhileStatement) {

            return doesLoopCompleteNormally(state, (ASTWhileStatement) stmt);

        } else if (stmt instanceof ASTForStatement) {

            return doesLoopCompleteNormally(state, (ASTForStatement) stmt);

        } else {
            return YES;
        }
    }

    private static OptionalBool doesLoopCompleteNormally(State state, ASTLoopStatement loop) {
        if (JavaRuleUtil.isBooleanLiteral(loop.getCondition(), false)) {
            return YES;
        }

        State loopState = new State(state);
        OptionalBool bodyCompletesNormally = completesNormally(loop.getBody(), loopState);

        if (JavaRuleUtil.isBooleanLiteral(loop.getCondition(), true)) {
            return loopState.containsBreak(loop)
                   // then the loop may complete normally via break
                   ? doesBreakTargetCompleteNormally(loop, loopState, bodyCompletesNormally)
                   // then the loop may only end through exception or return, ie abruptly
                   : NO;
        } else {
            // this max accounts for the case when the body
            // is never executed, which is a normal completion
            return max(UNKNOWN, doesBreakTargetCompleteNormally(loop, loopState, bodyCompletesNormally));
        }
    }

    private static OptionalBool handleSwitch(State state, ASTSwitchStatement switchStmt) {

        // note: exhaustive enum switches are NOT considered exhaustive
        // for the purposes of liveness analysis, only the presence of a
        // default case matters. Otherwise liveness analysis would depend
        // on type resolution and bad things would happen.
        boolean isExhaustive = switchStmt.hasDefaultCase();

        OptionalBool completesNormally = YES;
        boolean first = true;
        State switchState = new State(state);
        for (ASTSwitchBranch branch : switchStmt.getBranches()) {
            OptionalBool branchCompletesNormally;

            if (branch instanceof ASTSwitchArrowBranch) {
                ASTSwitchArrowRHS rhs = ((ASTSwitchArrowBranch) branch).getRightHandSide();
                branchCompletesNormally = switchArrowBranchCompletesNormally(state, switchStmt, rhs);

            } else if (branch instanceof ASTSwitchFallthroughBranch) {
                NodeStream<ASTStatement> statements = ((ASTSwitchFallthroughBranch) branch).getStatements();
                State branchState = new State(switchState);
                branchCompletesNormally = handleBlockLike(statements, branchState);
                branchCompletesNormally = doesBreakTargetCompleteNormally(switchStmt, branchState, branchCompletesNormally);

            } else {
                throw AssertionUtil.shouldNotReachHere("Not a branch type :" + branch);
            }

            if (isExhaustive && first) {
                completesNormally = branchCompletesNormally;
                first = false;
            } else {
                // if non-exhaustive, mix with YES on the first iteration,
                // which will produce at most UNKNOWN.

                // mix because it either/or, not a sequence (cf if/else treatment)
                completesNormally = join(completesNormally, branchCompletesNormally);
            }
        }

        return doesBreakTargetCompleteNormally(switchStmt, switchState, completesNormally);
    }

    private static OptionalBool switchArrowBranchCompletesNormally(State state, ASTSwitchStatement switchStmt, ASTSwitchArrowRHS rhs) {
        if (rhs instanceof ASTExpression) {
            return YES;
        } else if (rhs instanceof ASTThrowStatement) {
            state.setReturnOrThrow(true);
            return NO;
        } else if (rhs instanceof ASTBlock) {
            State subState = new State(state);
            OptionalBool branchCompletesNormally = completesNormally((ASTStatement) rhs, subState);
            return doesBreakTargetCompleteNormally(switchStmt, subState, branchCompletesNormally);
        } else {
            throw AssertionUtil.shouldNotReachHere("not a branch RHS: " + rhs);
        }
    }

    private static OptionalBool handleBlockLike(NodeStream<ASTStatement> stmts, State state) {
        OptionalBool total = YES; // empty block completes normally
        for (ASTStatement child : stmts) {
            OptionalBool childCompletesNormally = completesNormally(child, state);
            total = min(total, childCompletesNormally);
            if (total == NO) {
                // note: short circuit implement a liveness analysis
                // following statements are unreachable
                return NO;
            }
        }
        return total;
    }

    /**
     * We have a statement `breakTarget` which may be the target of a `break`
     * statement in its body. The `state` corresponds to the exploration state
     * of the body. `completesNormally` is whether the body completes normally
     * or not.
     *
     * This function computes whether the statement `breakTarget` also
     * completes normally or not, given that breaks within the body
     * that target a statement outside of the `breakTarget` cause abrupt
     * completion of the `breakTarget`.
     */
    private static OptionalBool doesBreakTargetCompleteNormally(ASTStatement breakTarget, State state, OptionalBool bodyCompletesNormally) {
        if (bodyCompletesNormally == YES
            || state.breakTargets.isEmpty()
            && state.continueTargets.isEmpty()
            || state.isReturnOrThrow()) {
            return bodyCompletesNormally;
        }

        return onlyBreaksWithinSubTree(breakTarget, state) ? YES : bodyCompletesNormally;
    }

    private static boolean onlyBreaksWithinSubTree(ASTStatement breakTarget, State state) {
        return state.breakTargets.stream().allMatch(it -> isAncestor(breakTarget, it))
            && state.continueTargets.stream().allMatch(it -> isAncestor(breakTarget, it));
    }

    private static boolean isAncestor(ASTStatement breakTarget, JavaNode it) {
        return it.ancestorsOrSelf().any(parent -> parent == breakTarget);
    }

    /**
     * Tracks exploration state of an expression.
     */
    private static class State {

        private final @Nullable State parent;
        private boolean returnOrThrow;
        private Set<JavaNode> breakTargets = Collections.emptySet();
        private Set<ASTStatement> continueTargets = Collections.emptySet();

        State(State parent) {
            this.parent = parent;
        }

        boolean containsBreak(ASTStatement stmt) {
            return breakTargets.contains(stmt);
        }

        void addBreak(ASTBreakStatement breakStatement) {
            addBreakImpl(breakStatement.getTarget());
        }

        void addYield(ASTYieldStatement yieldStmt) {
            addBreakImpl(yieldStmt.getYieldTarget());
        }

        private void addBreakImpl(JavaNode breakTargetStatement) {
            if (breakTargets.isEmpty()) {
                breakTargets = new HashSet<>();
            }
            breakTargets.add(breakTargetStatement);
            if (parent != null) {
                parent.addBreakImpl(breakTargetStatement);
            }
        }

        void addContinue(ASTContinueStatement continueStatement) {
            if (continueTargets.isEmpty()) {
                continueTargets = new HashSet<>();
            }
            continueTargets.add(continueStatement.getTarget());
            if (parent != null) {
                parent.addContinue(continueStatement);
            }
        }

        public boolean isReturnOrThrow() {
            return returnOrThrow;
        }

        public void setReturnOrThrow(boolean returnOrThrow) {
            this.returnOrThrow = this.isReturnOrThrow() | returnOrThrow;
            if (parent != null) {
                parent.setReturnOrThrow(returnOrThrow);
            }
        }
    }
}
