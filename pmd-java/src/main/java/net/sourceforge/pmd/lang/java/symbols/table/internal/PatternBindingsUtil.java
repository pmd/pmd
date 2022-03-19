/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal;

import org.pcollections.HashTreePSet;
import org.pcollections.PSet;

import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTGuardedPattern;
import net.sourceforge.pmd.lang.java.ast.ASTInfixExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPattern;
import net.sourceforge.pmd.lang.java.ast.ASTPatternExpression;
import net.sourceforge.pmd.lang.java.ast.ASTRecordPattern;
import net.sourceforge.pmd.lang.java.ast.ASTTypePattern;
import net.sourceforge.pmd.lang.java.ast.ASTUnaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.BinaryOp;
import net.sourceforge.pmd.lang.java.ast.UnaryOp;
import net.sourceforge.pmd.util.AssertionUtil;

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

                return bindersOfPattern(((ASTPatternExpression) right).getPattern());

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
        } else if (e instanceof ASTPatternExpression) {
            return bindersOfPattern(((ASTPatternExpression) e).getPattern());
        }
        return BindSet.EMPTY;
    }

    static BindSet bindersOfPattern(ASTPattern pattern) {
        if (pattern instanceof ASTTypePattern) {
            return BindSet.whenTrue(HashTreePSet.singleton(((ASTTypePattern) pattern).getVarId()));
        } else if (pattern instanceof ASTRecordPattern) {
            // record pattern might not bind a variable for the whole record...
            ASTVariableDeclaratorId varId = ((ASTRecordPattern) pattern).getVarId();
            if (varId == null) {
                return BindSet.whenTrue(BindSet.noBindings());
            }
            return BindSet.whenTrue(HashTreePSet.singleton(varId));
        } else if (pattern instanceof ASTGuardedPattern) {
            BindSet patternBindings = bindersOfPattern(((ASTGuardedPattern) pattern).getPattern());
            BindSet guardBindings = bindersOfExpr(((ASTGuardedPattern) pattern).getGuard());
            return patternBindings.union(guardBindings);
        } else {
            throw AssertionUtil.shouldNotReachHere("no other instances of pattern should exist: " + pattern);
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

        public BindSet union(BindSet bindSet) {
            if (this.isEmpty()) {
                return bindSet;
            } else if (bindSet.isEmpty()) {
                return this;
            }
            return new BindSet(
                trueBindings.plusAll(bindSet.trueBindings),
                falseBindings.plusAll(bindSet.falseBindings)
            );
        }

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

}
