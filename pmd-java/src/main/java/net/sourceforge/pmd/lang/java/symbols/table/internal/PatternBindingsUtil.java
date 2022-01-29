/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal;

import static net.sourceforge.pmd.util.OptionalBool.NO;
import static net.sourceforge.pmd.util.OptionalBool.UNKNOWN;
import static net.sourceforge.pmd.util.OptionalBool.YES;
import static net.sourceforge.pmd.util.OptionalBool.max;
import static net.sourceforge.pmd.util.OptionalBool.min;
import static net.sourceforge.pmd.util.OptionalBool.mix;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.pcollections.HashTreePSet;
import org.pcollections.PSet;

import net.sourceforge.pmd.internal.util.AssertionUtil;
import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTBreakStatement;
import net.sourceforge.pmd.lang.java.ast.ASTContinueStatement;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTInfixExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLabeledStatement;
import net.sourceforge.pmd.lang.java.ast.ASTPattern;
import net.sourceforge.pmd.lang.java.ast.ASTPatternExpression;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTStatement;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchArrowBranch;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchArrowRHS;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchBranch;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchFallthroughBranch;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTSynchronizedStatement;
import net.sourceforge.pmd.lang.java.ast.ASTThrowStatement;
import net.sourceforge.pmd.lang.java.ast.ASTTypePattern;
import net.sourceforge.pmd.lang.java.ast.ASTUnaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.ASTWhileStatement;
import net.sourceforge.pmd.lang.java.ast.BinaryOp;
import net.sourceforge.pmd.lang.java.ast.UnaryOp;
import net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil;
import net.sourceforge.pmd.util.OptionalBool;

/**
 * Utilities to resolve scope of pattern binding variables.
 *
 * @see <a href="https://docs.oracle.com/javase/specs/jls/se17/html/jls-6.html#jls-6.3.1">Java Language Specification</a>
 */
final class PatternBindingsUtil {

    private PatternBindingsUtil() {
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
        /*
            TODO:
                - switches
                - do {} while(true);
                - while(true) {}
         */
        if (stmt instanceof ASTThrowStatement || stmt instanceof ASTReturnStatement) {

            state.setReturnOrThrow(true);
            return NO;

        } else if (stmt instanceof ASTBreakStatement) {

            state.addBreak((ASTBreakStatement) stmt);
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

                return mix(completesNormally(thenBranch, state),
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

        boolean isExhaustive = switchStmt.isExhaustiveEnumSwitch() || switchStmt.hasDefaultCase();

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
                completesNormally = mix(completesNormally, branchCompletesNormally);
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

    private static boolean isAncestor(ASTStatement breakTarget, ASTStatement it) {
        return it.ancestorsOrSelf().any(parent -> parent == breakTarget);
    }

    /**
     * Returns the binding set declared by the expression. Note that
     * only some expressions contribute bindings, and every other form
     * of expression does not contribute anything (meaning, all subexpressions
     * are not processed).
     */
    static BindSet bindersOfExpr(ASTExpression e) {
        /*
           JLS 17§6.3.1
           If an expression is not a conditional-and expression, conditional-or
           expression, logical complement expression, conditional expression,
           instanceof expression, switch expression, or parenthesized
           expression, then no scope rules apply.
         */

        if (e instanceof ASTUnaryExpression) {
            ASTUnaryExpression unary = (ASTUnaryExpression) e;
            return unary.getOperator() == UnaryOp.NEGATION
                   ? bindersOfExpr(unary.getOperand()).negate()
                   : BindSet.EMPTY;

        } else if (e instanceof ASTInfixExpression) {
            BinaryOp op = ((ASTInfixExpression) e).getOperator();
            ASTExpression left = ((ASTInfixExpression) e).getLeftOperand();
            ASTExpression right = ((ASTInfixExpression) e).getRightOperand();

            if (op == BinaryOp.INSTANCEOF && right instanceof ASTPatternExpression) {

                return collectBindings(((ASTPatternExpression) right).getPattern());

            } else if (op == BinaryOp.CONDITIONAL_AND) { // &&
                // A pattern variable is introduced by a && b when true iff either
                // (i) it is introduced by a when true or
                // (ii) it is introduced by b when true.

                return BindSet.whenTrue(
                    bindersOfExpr(left).trueBindings.plusAll(bindersOfExpr(right).trueBindings)
                );

            } else if (op == BinaryOp.CONDITIONAL_OR) { // ||
                // A pattern variable is introduced by a || b when false iff either
                // (i) it is introduced by a when false or
                // (ii) it is introduced by b when false.

                return BindSet.whenFalse(
                    bindersOfExpr(left).falseBindings.plusAll(bindersOfExpr(right).falseBindings)
                );

            } else {
                return BindSet.EMPTY;
            }
        }
        return BindSet.EMPTY;
    }

    static BindSet collectBindings(ASTPattern pattern) {
        if (pattern instanceof ASTTypePattern) {
            return BindSet.EMPTY.addBinding(((ASTTypePattern) pattern).getVarId());
        } else {
            throw AssertionUtil.shouldNotReachHere("no other instances of pattern should exist");
        }
    }

    /**
     * A set of bindings contributed by a (boolean) expression. Different
     * bindings are introduced if the expr evaluates to false or true, which
     * is relevant for the scope of bindings introduced in if stmt conditions.
     */
    static final class BindSet {

        static final BindSet EMPTY = new BindSet(HashTreePSet.empty(),
                                                 HashTreePSet.empty());

        private final PSet<ASTVariableDeclaratorId> trueBindings;
        private final PSet<ASTVariableDeclaratorId> falseBindings;

        static PSet<ASTVariableDeclaratorId> noBindings() {
            return HashTreePSet.empty();
        }

        BindSet(PSet<ASTVariableDeclaratorId> trueBindings,
                PSet<ASTVariableDeclaratorId> falseBindings) {
            this.trueBindings = trueBindings;
            this.falseBindings = falseBindings;
        }

        public PSet<ASTVariableDeclaratorId> getTrueBindings() {
            return trueBindings;
        }

        public PSet<ASTVariableDeclaratorId> getFalseBindings() {
            return falseBindings;
        }

        BindSet negate() {
            return isEmpty() ? this : new BindSet(falseBindings, trueBindings);
        }

        boolean isEmpty() {
            return this == EMPTY;
        }

        BindSet addBinding(ASTVariableDeclaratorId e) {
            return new BindSet(trueBindings.plus(e), falseBindings);
        }

        static BindSet whenTrue(PSet<ASTVariableDeclaratorId> bindings) {
            return new BindSet(bindings, HashTreePSet.empty());
        }

        static BindSet whenFalse(PSet<ASTVariableDeclaratorId> bindings) {
            return new BindSet(HashTreePSet.empty(), bindings);
        }

    }

    /**
     * Tracks exploration state of an expression.
     */
    private static class State {

        private final @Nullable State parent;
        private boolean returnOrThrow;
        private Set<ASTStatement> breakTargets = Collections.emptySet();
        private Set<ASTStatement> continueTargets = Collections.emptySet();

        public State(State parent) {
            this.parent = parent;
        }

        boolean containsBreak(ASTStatement stmt) {
            return breakTargets.contains(stmt);
        }

        void addBreak(ASTBreakStatement breakStatement) {
            if (breakTargets.isEmpty()) {
                breakTargets = new HashSet<>();
            }
            breakTargets.add(breakStatement.getTarget());
            if (parent != null) {
                parent.addBreak(breakStatement);
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
