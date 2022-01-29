/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal;

import java.util.HashSet;
import java.util.Set;

import org.pcollections.HashTreePSet;
import org.pcollections.PSet;

import net.sourceforge.pmd.internal.util.AssertionUtil;
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
        return completesNormally(stmt) != OptionalBool.NO;
    }

    static OptionalBool completesNormally(ASTStatement stmt) {
        if (stmt instanceof ASTLabeledStatement) {
            State state = new State();
            OptionalBool completesNormally = completesNormally(((ASTLabeledStatement) stmt).getStatement(), state);
            if (state.containsBreak(stmt)) {
                return OptionalBool.max(completesNormally, OptionalBool.UNKNOWN);
            }
            // we need to remove labels
            return completesNormally;
        }

        State state = new State();
        OptionalBool completesNormally = completesNormally(stmt, state);
        if (state.returnOrThrow) {
            return OptionalBool.min(completesNormally, OptionalBool.UNKNOWN);
        } else if (state.containsBreak(stmt)) {
            return OptionalBool.max(completesNormally, OptionalBool.UNKNOWN);
        }
        return completesNormally;
    }

    static OptionalBool completesNormally(ASTStatement stmt, State state) {
        /*
            TODO:
                - switches
                - do {} while(true);
                - while(true) {}
         */
        if (stmt instanceof ASTThrowStatement || stmt instanceof ASTReturnStatement) {

            state.returnOrThrow = true;
            return OptionalBool.NO;

        } else if (stmt instanceof ASTBreakStatement) {

            state.addBreak((ASTBreakStatement) stmt);
            return OptionalBool.NO;

        } else if (stmt instanceof ASTContinueStatement) {

            state.addContinue((ASTContinueStatement) stmt);
            return OptionalBool.NO;

        } else if (stmt instanceof ASTBlock) {
            // A block can complete normally if all of its statements
            // in sequence can complete normally.
            // Since if a statement CANNOT complete normally, anything
            // that follows is dead code (and would be a compile-time
            // error if there is any), we could optimize this branch
            // by just checking that the last statement of the block
            // may complete normally, under the assumption that we're
            // handling only valid java source code. Let's do this later.

            OptionalBool total = OptionalBool.YES; // empty block completes normally
            for (ASTStatement child : (ASTBlock) stmt) {
                OptionalBool childCompletesNormally = completesNormally(child, state);
                total = OptionalBool.min(total, childCompletesNormally);
                if (total == OptionalBool.NO) {
                    // note: short circuit implement a liveness analysis
                    // following statements are unreachable
                    return OptionalBool.NO;
                }
            }
            return total;
        } else if (stmt instanceof ASTIfStatement) {
            ASTIfStatement ifStmt = (ASTIfStatement) stmt;

            ASTStatement thenBranch = ifStmt.getThenBranch();
            ASTStatement elseBranch = ifStmt.getElseBranch();

            if (elseBranch == null) {
                // yes -> yes
                // unk -> unk
                // no -> unk
                return OptionalBool.max(completesNormally(thenBranch, state), OptionalBool.UNKNOWN);
            } else {
                // yes, yes -> yes
                // yes, unk -> unk
                // no, unk -> unk
                // no, no -> no

                return OptionalBool.mix(completesNormally(thenBranch, state),
                                        completesNormally(elseBranch, state));
            }

        } else if (stmt instanceof ASTLabeledStatement) {

            return completesNormally(((ASTLabeledStatement) stmt).getStatement());

        } else if (stmt instanceof ASTSynchronizedStatement) {

            return completesNormally(((ASTSynchronizedStatement) stmt).getBody(), state);

        } else if (stmt instanceof ASTWhileStatement) {

            ASTWhileStatement loop = (ASTWhileStatement) stmt;

            // a while(true) statement completes normally
            // iff it contains a break which targets it.

            // a while(true) statement always completes abruptly if its
            // body always or never completes abruptly.

            // a while(not true) statement may always complete normally (false condition).
            // if the body always completes normally, then it always completes normally.

            State loopState = new State();
            OptionalBool bodyCompletesNormally = completesNormally(loop.getBody(), loopState);

            if (JavaRuleUtil.isBooleanLiteral(loop.getCondition(), true)) {
                if (loopState.containsBreak(loop)) {
                    return OptionalBool.UNKNOWN;
                }

                if (bodyCompletesNormally == OptionalBool.YES) {
                    // then this is an infinite loop.
                    // todo maybe it would be worth setting an attribute on the node.
                    return OptionalBool.NO;
                } else if (loopState.returnOrThrow) {
                    // then a return or throw is reachable: this ends
                    // the while(true) abruptly
                    return OptionalBool.NO;
                }
                // unknown or NO
                return bodyCompletesNormally;
            } else {
                return (bodyCompletesNormally == OptionalBool.YES) ? OptionalBool.YES : OptionalBool.UNKNOWN;
            }
        } else {
            return OptionalBool.YES;
        }
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

        static BindSet union(BindSet first, BindSet other) {
            if (first.isEmpty()) {
                return other;
            } else if (other.isEmpty()) {
                return first;
            }
            return new BindSet(first.trueBindings.plusAll(other.trueBindings),
                               first.falseBindings.plusAll(other.falseBindings));
        }
    }

    /**
     * Tracks exploration state of an expression.
     */
    private static class State {

        private boolean returnOrThrow;
        private Set<ASTStatement> breakTargets = null;
        private Set<ASTStatement> continueTargets = null;

        State() {
        }

        boolean containsBreak(ASTStatement stmt) {
            return breakTargets != null && breakTargets.contains(stmt);
        }

        void addBreak(ASTBreakStatement breakStatement) {
            if (breakTargets == null) {
                breakTargets = new HashSet<>();
            }
            breakTargets.add(breakStatement.getTarget());
        }

        void addContinue(ASTContinueStatement continueStatement) {
            if (continueTargets == null) {
                continueTargets = new HashSet<>();
            }
            continueTargets.add(continueStatement.getTarget());
        }

    }
}
