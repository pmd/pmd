/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal;

import static net.sourceforge.pmd.util.OptionalBool.NO;
import static net.sourceforge.pmd.util.OptionalBool.UNKNOWN;
import static net.sourceforge.pmd.util.OptionalBool.YES;
import static net.sourceforge.pmd.util.OptionalBool.max;
import static net.sourceforge.pmd.util.OptionalBool.min;
import static net.sourceforge.pmd.util.OptionalBool.join;

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
            return handleBreaks(stmt, subState, completesNormally);

        } else if (stmt instanceof ASTSynchronizedStatement) {

            return completesNormally(((ASTSynchronizedStatement) stmt).getBody(), state);

        } else if (stmt instanceof ASTSwitchStatement) {

            return handleSwitch(state, (ASTSwitchStatement) stmt);

        } else if (stmt instanceof ASTWhileStatement) {

            ASTWhileStatement loop = (ASTWhileStatement) stmt;

            if (JavaRuleUtil.isBooleanLiteral(loop.getCondition(), false)) {
                return YES; // body is unreachable
            }

            State loopState = new State(state);
            OptionalBool bodyCompletesNormally = completesNormally(loop.getBody(), loopState);

            if (JavaRuleUtil.isBooleanLiteral(loop.getCondition(), true)) {
                if (loopState.containsBreak(loop)) {
                    if (!loopState.isReturnOrThrow()) {
                        return handleBreaks(stmt, loopState, bodyCompletesNormally);
                    }

                    // normal completion of the while
                    return max(UNKNOWN, bodyCompletesNormally);
                }

                if (bodyCompletesNormally == YES) {
                    // then this is an infinite loop.
                    // todo maybe it would be worth setting an attribute on the node.
                    return NO;
                } else if (loopState.isReturnOrThrow()) {
                    // then a return or throw is reachable: this ends
                    // the while(true) abruptly
                    return NO;
                }
                // unknown or NO
                return bodyCompletesNormally;
            } else {
                // no -> unk
                // unk -> unk
                // yes -> yes

                // this max accounts for the case when the body
                // is never executed, which is a normal completion
                return max(UNKNOWN, handleBreaks(loop, loopState, bodyCompletesNormally));
            }
        } else {
            return YES;
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
                branchCompletesNormally = handleBreaks(switchStmt, branchState, branchCompletesNormally);

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

        return handleBreaks(switchStmt, switchState, completesNormally);
    }

    private static OptionalBool switchArrowBranchCompletesNormally(State state, ASTSwitchStatement switchStmt, ASTSwitchArrowRHS rhs) {
        if (rhs instanceof ASTExpression) {
            return YES;
        }
        if (rhs instanceof ASTThrowStatement) {
            state.setReturnOrThrow(true);
            return NO;
        } else if (rhs instanceof ASTBlock) {
            State subState = new State(state);
            OptionalBool branchCompletesNormally = completesNormally((ASTStatement) rhs, subState);
            return handleBreaks(switchStmt, subState, branchCompletesNormally);
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

    private static OptionalBool handleBreaks(ASTStatement breakTarget, State state, OptionalBool completesNormally) {
        if (state.isReturnOrThrow()) {
            return min(completesNormally, UNKNOWN);
        } else if (!state.breakTargets.isEmpty()
            || !state.continueTargets.isEmpty()) {

            boolean onlyBreaksWithinSubTree =
                state.breakTargets.stream().allMatch(it -> isAncestor(breakTarget, it))
                    && state.continueTargets.stream().allMatch(it -> isAncestor(breakTarget, it));

            if (onlyBreaksWithinSubTree) {
                return YES;
            }
        }
        return completesNormally;
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
