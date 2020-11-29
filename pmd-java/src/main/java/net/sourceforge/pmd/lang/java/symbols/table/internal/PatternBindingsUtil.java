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
import net.sourceforge.pmd.lang.java.ast.ASTTypeTestPattern;
import net.sourceforge.pmd.lang.java.ast.ASTUnaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.ASTWhileStatement;
import net.sourceforge.pmd.lang.java.ast.BinaryOp;
import net.sourceforge.pmd.lang.java.ast.UnaryOp;
import net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil;
import net.sourceforge.pmd.util.OptionalBool;

final class PatternBindingsUtil {

    private PatternBindingsUtil() {
        // util class
    }

    static boolean canCompleteNormally(ASTStatement stmt) {
        return completesNormally(stmt) != OptionalBool.NO;
    }

    static OptionalBool completesNormally(ASTStatement stmt) {
        State state = new State(stmt);
        OptionalBool completesNormally = completesNormallyImpl(stmt, state);
        if (state.mayJumpOutsideToplevel) {
            completesNormally = completesNormally.max(OptionalBool.UNKNOWN);
        }
        return completesNormally;
    }

    private static final class State {

        final ASTStatement toplevel;
        final Set<String> labelsBelowToplevel = new HashSet<>();
        boolean mayJumpOutsideToplevel;

        ASTStatement breakTarget;
        ASTStatement continueTarget;

        State(ASTStatement toplevel) {
            this.toplevel = toplevel;
        }
    }

    /**
     * @param stmt Statement
     */
    private static OptionalBool completesNormallyImpl(ASTStatement stmt, State state) {

        if (stmt instanceof ASTThrowStatement || stmt instanceof ASTReturnStatement) {

            state.mayJumpOutsideToplevel = true;
            return OptionalBool.NO;

        } else if (stmt instanceof ASTBreakStatement) {

            String label = ((ASTBreakStatement) stmt).getLabel();
            if (label != null && !state.labelsBelowToplevel.contains(label)
                || label == null && state.breakTarget != state.toplevel) {
                state.mayJumpOutsideToplevel = true;
            }
            return OptionalBool.NO;

        } else if (stmt instanceof ASTContinueStatement) {

            String label = ((ASTContinueStatement) stmt).getLabel();
            if (label != null && !state.labelsBelowToplevel.contains(label)
                || label == null && state.continueTarget != state.toplevel) {
                state.mayJumpOutsideToplevel = true;
            }
            return OptionalBool.NO;

        } else if (stmt instanceof ASTBlock) {

            ASTBlock block = (ASTBlock) stmt;
            OptionalBool result = OptionalBool.YES;
            for (ASTStatement child : block) {
                result = result.min(completesNormallyImpl(child, state));
            }
            return result;
        } else if (stmt instanceof ASTIfStatement) {
            ASTIfStatement ifStmt = (ASTIfStatement) stmt;

            ASTStatement thenBranch = ifStmt.getThenBranch();
            ASTStatement elseBranch = ifStmt.getElseBranch();

            OptionalBool then = completesNormallyImpl(thenBranch, state);

            return elseBranch != null ? then.min(completesNormallyImpl(elseBranch, state))
                                      : OptionalBool.UNKNOWN;

        } else if (stmt instanceof ASTLabeledStatement) {
            ASTLabeledStatement labeledStmt = (ASTLabeledStatement) stmt;

            state.labelsBelowToplevel.add(labeledStmt.getLabel());
            OptionalBool result = completesNormallyImpl(labeledStmt.getStatement(), state);
            state.labelsBelowToplevel.remove(labeledStmt.getLabel());

            return result;

        } else if (stmt instanceof ASTSynchronizedStatement) {

            return completesNormallyImpl(((ASTSynchronizedStatement) stmt).getBody(), state);

        } else if (stmt instanceof ASTWhileStatement) {
            ASTWhileStatement loop = (ASTWhileStatement) stmt;

            State state2 = new State(loop);
            state2.continueTarget = loop;
            state2.breakTarget = loop;
            OptionalBool bodyCompletesNormally = completesNormallyImpl(loop.getBody(), state2);

            state.mayJumpOutsideToplevel |= state2.mayJumpOutsideToplevel;

            if (JavaRuleUtil.isBooleanLit(loop.getCondition(), true)) {
                // while (true)

                // can complete normally if the block *may* complete abruptly
                if (bodyCompletesNormally == OptionalBool.YES) {
                    return OptionalBool.NO;// fixme    while(true) continue;
                } else {
                    return bodyCompletesNormally;
                }
            }
            return OptionalBool.UNKNOWN;
        } else {
            return OptionalBool.YES;
        }
    }

    static OptionalBool negate(OptionalBool o) {
        switch (o) {
        case NO:
            return OptionalBool.YES;
        case YES:
            return OptionalBool.NO;
        default:
            return OptionalBool.UNKNOWN;
        }
    }

    /**
     * Returns the binding set declared by the expression. Note that
     * only some expressions contribute bindings, and every other form
     * of expression does not contribute anything (meaning, all subexpressions
     * are not processed).
     */
    static BindSet bindersOfExpr(ASTExpression e) {
        if (e instanceof ASTUnaryExpression) {
            ASTUnaryExpression unary = (ASTUnaryExpression) e;
            return unary.getOperator() == UnaryOp.NEGATION ? bindersOfExpr(unary.getOperand()).negate()
                                                           : BindSet.EMPTY;
        } else if (e instanceof ASTInfixExpression) {
            BinaryOp op = ((ASTInfixExpression) e).getOperator();
            ASTExpression left = ((ASTInfixExpression) e).getLeftOperand();
            ASTExpression right = ((ASTInfixExpression) e).getRightOperand();

            if (op == BinaryOp.INSTANCEOF && right instanceof ASTPatternExpression) {
                return collectBindings(((ASTPatternExpression) right).getPattern());
            } else if (op == BinaryOp.CONDITIONAL_AND) { // &&
                return BindSet.union(bindersOfExpr(left), bindersOfExpr(right));
            } else if (op == BinaryOp.CONDITIONAL_OR) { // ||
                // actually compute !( !left && !right )
                return BindSet.union(bindersOfExpr(left).negate(),
                                     bindersOfExpr(right).negate()).negate();
            } else {
                return BindSet.EMPTY;
            }
        }
        return BindSet.EMPTY;
    }

    static BindSet collectBindings(ASTPattern pattern) {
        if (pattern instanceof ASTTypeTestPattern) {
            return BindSet.EMPTY.addBinding(((ASTTypeTestPattern) pattern).getVarId());
        } else {
            throw AssertionUtil.shouldNotReachHere("no other instances of pattern should exist");
        }
    }

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

}
