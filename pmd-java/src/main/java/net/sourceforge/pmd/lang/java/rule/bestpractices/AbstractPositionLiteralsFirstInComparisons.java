/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTArguments;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalAndExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalOrExpression;
import net.sourceforge.pmd.lang.java.ast.ASTEqualityExpression;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTNullLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

class AbstractPositionLiteralsFirstInComparisons extends AbstractJavaRule {

    private final String equalsImage;

    AbstractPositionLiteralsFirstInComparisons(String equalsImage) {
        addRuleChainVisit(ASTPrimaryExpression.class);
        this.equalsImage = equalsImage;
    }

    @Override
    public Object visit(ASTPrimaryExpression node, Object data) {
        ASTPrimaryPrefix primaryPrefix = node.getFirstChildOfType(ASTPrimaryPrefix.class);
        ASTPrimarySuffix primarySuffix = node.getFirstChildOfType(ASTPrimarySuffix.class);
        if (primaryPrefix != null && primarySuffix != null) {
            ASTName name = primaryPrefix.getFirstChildOfType(ASTName.class);
            if (name == null || !name.getImage().endsWith(equalsImage)) {
                return data;
            }
            if (!isSingleStringLiteralArgument(primarySuffix)) {
                return data;
            }
            if (isWithinNullComparison(node)) {
                return data;
            }
            addViolation(data, node);
        }
        return node;
    }

    private boolean isWithinNullComparison(ASTPrimaryExpression node) {
        for (ASTExpression parentExpr : node.getParentsOfType(ASTExpression.class)) {
            if (isComparisonWithNull(parentExpr, "==", ASTConditionalOrExpression.class)
                    || isComparisonWithNull(parentExpr, "!=", ASTConditionalAndExpression.class)) {
                return true;
            }
        }
        return false;
    }

    /*
     * Expression/ConditionalAndExpression//EqualityExpression(@Image='!=']//NullLiteral
     * Expression/ConditionalOrExpression//EqualityExpression(@Image='==']//NullLiteral
     */
    private boolean isComparisonWithNull(ASTExpression parentExpr, String equalOperator, Class<? extends JavaNode> condition) {
        Node condExpr = null;
        ASTEqualityExpression eqExpr = null;
        if (parentExpr != null) {
            condExpr = parentExpr.getFirstChildOfType(condition);
        }
        if (condExpr != null) {
            eqExpr = condExpr.getFirstDescendantOfType(ASTEqualityExpression.class);
        }
        if (eqExpr != null) {
            return eqExpr.hasImageEqualTo(equalOperator) && eqExpr.hasDescendantOfType(ASTNullLiteral.class);
        }
        return false;
    }

    /*
     * This corresponds to the following XPath expression:
     * (../PrimarySuffix/Arguments/ArgumentList/Expression/PrimaryExpression/PrimaryPrefix/Literal[@StringLiteral= true()])
     *       and
     * ( count(../PrimarySuffix/Arguments/ArgumentList/Expression) = 1 )
     */
    private boolean isSingleStringLiteralArgument(ASTPrimarySuffix primarySuffix) {
        if (!primarySuffix.isArguments() || primarySuffix.getArgumentCount() != 1) {
            return false;
        }
        Node node = primarySuffix;
        node = node.getFirstChildOfType(ASTArguments.class);
        if (node != null) {
            node = node.getFirstChildOfType(ASTArgumentList.class);
            if (node.getNumChildren() != 1) {
                return false;
            }
        }
        if (node != null) {
            node = node.getFirstChildOfType(ASTExpression.class);
        }
        if (node != null) {
            node = node.getFirstChildOfType(ASTPrimaryExpression.class);
        }
        if (node != null) {
            node = node.getFirstChildOfType(ASTPrimaryPrefix.class);
        }
        if (node != null) {
            node = node.getFirstChildOfType(ASTLiteral.class);
        }
        if (node != null) {
            ASTLiteral literal = (ASTLiteral) node;
            if (literal.isStringLiteral()) {
                return true;
            }
        }
        return false;
    }
}
