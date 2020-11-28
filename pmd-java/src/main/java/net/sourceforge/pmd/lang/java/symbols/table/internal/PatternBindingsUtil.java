/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal;

import org.pcollections.HashTreePSet;
import org.pcollections.PSet;

import net.sourceforge.pmd.internal.util.AssertionUtil;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTInfixExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPattern;
import net.sourceforge.pmd.lang.java.ast.ASTPatternExpression;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTStatement;
import net.sourceforge.pmd.lang.java.ast.ASTThrowStatement;
import net.sourceforge.pmd.lang.java.ast.ASTTypeTestPattern;
import net.sourceforge.pmd.lang.java.ast.ASTUnaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.BinaryOp;
import net.sourceforge.pmd.lang.java.ast.UnaryOp;
import net.sourceforge.pmd.util.OptionalBool;

final class PatternBindingsUtil {

    private PatternBindingsUtil() {
        // util class
    }

    /**
     * TODO test
     * TODO most statements are unsupported, including break statements
     *  (this will probs need to be rewritten at some point)
     */
    static OptionalBool completesAbruptly(ASTStatement s) {
        if (s instanceof ASTThrowStatement || s instanceof ASTReturnStatement) {

            return OptionalBool.YES;

        } else if (s instanceof ASTBlock) {

            ASTBlock block = (ASTBlock) s;
            OptionalBool result = OptionalBool.NO;
            for (ASTStatement stmt : block) {
                result = result.max(completesAbruptly(stmt));
                if (result == OptionalBool.YES) {
                    return result;
                }
            }
        } else if (s instanceof ASTIfStatement) {
            ASTIfStatement ifStmt = (ASTIfStatement) s;

            ASTStatement thenBranch = ifStmt.getThenBranch();
            ASTStatement elseBranch = ifStmt.getElseBranch();

            OptionalBool result = completesAbruptly(thenBranch);
            if (elseBranch != null) {
                result = result.max(completesAbruptly(elseBranch));
            } else {
                result = result.min(OptionalBool.UNKNOWN);
            }
            return result;
        }

        // todo
        return s.children(ASTStatement.class).reduce(OptionalBool.NO, (r, stmt) -> r.max(completesAbruptly(stmt)));
    }

    static boolean canCompleteNormally(ASTStatement s) {
        return completesAbruptly(s) != OptionalBool.YES;
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
