/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal;

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

final class PatternBindingsUtil {

    private PatternBindingsUtil() {
        // util class
    }

    static boolean canCompleteNormally(ASTStatement stmt) {
        if (stmt instanceof ASTLabeledStatement) {
            // we need to remove labels
            return canCompleteNormally(((ASTLabeledStatement) stmt).getStatement());
        }

        return canCompleteNormallyImpl(stmt, HashTreePSet.empty(), true, true);
    }

    /**
     * @param stmt          Statement
     * @param labelsInScope Labels to which breaking is ok, because they're
     *                      in a strict descendant of the toplevel node.
     *                      Unlabeled breaks/continues have special fake labels.
     */
    private static boolean canCompleteNormallyImpl(ASTStatement stmt,
                                                   PSet<String> labelsInScope,
                                                   boolean breakAllowed,
                                                   boolean continueAllowed) {
        if (stmt instanceof ASTThrowStatement || stmt instanceof ASTReturnStatement) {

            return false;

        } else if (stmt instanceof ASTBreakStatement) {

            String label = ((ASTBreakStatement) stmt).getLabel();
            return label == null && breakAllowed || label != null && labelsInScope.contains(label);

        } else if (stmt instanceof ASTContinueStatement) {

            String label = ((ASTContinueStatement) stmt).getLabel();
            return label == null && continueAllowed || label != null && labelsInScope.contains(label);

        } else if (stmt instanceof ASTBlock) {

            ASTBlock block = (ASTBlock) stmt;
            for (ASTStatement child : block) {
                if (!canCompleteNormallyImpl(child, labelsInScope, breakAllowed, continueAllowed)) {
                    return false;
                }
            }
            return true;
        } else if (stmt instanceof ASTIfStatement) {
            ASTIfStatement ifStmt = (ASTIfStatement) stmt;

            ASTStatement thenBranch = ifStmt.getThenBranch();
            ASTStatement elseBranch = ifStmt.getElseBranch();

            return elseBranch == null
                || canCompleteNormallyImpl(thenBranch, labelsInScope, breakAllowed, continueAllowed)
                || canCompleteNormallyImpl(elseBranch, labelsInScope, breakAllowed, continueAllowed);

        } else if (stmt instanceof ASTLabeledStatement) {
            ASTLabeledStatement labeledStmt = (ASTLabeledStatement) stmt;

            return canCompleteNormallyImpl(labeledStmt.getStatement(),
                                           labelsInScope.plus(labeledStmt.getLabel()),
                                           breakAllowed,
                                           continueAllowed);

        } else if (stmt instanceof ASTSynchronizedStatement) {

            return canCompleteNormallyImpl(((ASTSynchronizedStatement) stmt).getBody(), labelsInScope, breakAllowed, continueAllowed);

        } else if (stmt instanceof ASTWhileStatement) {
            ASTWhileStatement loop = (ASTWhileStatement) stmt;

            if (JavaRuleUtil.isBooleanLit(loop.getCondition(), true)) {
                // while (true)

                // can complete normally if the block *may* complete abruptly
                // todo
                return canCompleteNormallyImpl(loop.getBody(), labelsInScope, true, true);
            }
            return true;
        } else {
            return true;
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
