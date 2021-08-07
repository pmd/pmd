/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import net.sourceforge.pmd.lang.java.ast.ASTAllocationExpression;
import net.sourceforge.pmd.lang.java.ast.ASTArguments;
import net.sourceforge.pmd.lang.java.ast.ASTArrayDimsAndInits;
import net.sourceforge.pmd.lang.java.ast.ASTBooleanLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;

public class PrimitiveWrapperInstantiationRule extends AbstractJavaRule {

    public PrimitiveWrapperInstantiationRule() {
        addRuleChainVisit(ASTAllocationExpression.class);
        addRuleChainVisit(ASTPrimaryExpression.class);
    }

    @Override
    public Object visit(ASTAllocationExpression node, Object data) {
        if (node.getFirstChildOfType(ASTArrayDimsAndInits.class) != null) {
            return data;
        }
        ASTClassOrInterfaceType type = node.getFirstChildOfType(ASTClassOrInterfaceType.class);
        if (type == null) {
            return data;
        }

        if (TypeTestUtil.isA(Double.class, type)
                || TypeTestUtil.isA(Float.class, type)
                || TypeTestUtil.isA(Long.class, type)
                || TypeTestUtil.isA(Integer.class, type)
                || TypeTestUtil.isA(Short.class, type)
                || TypeTestUtil.isA(Byte.class, type)
                || TypeTestUtil.isA(Character.class, type)) {
            addViolation(data, node, type.getImage());
        } else if (TypeTestUtil.isA(Boolean.class, type)) {
            checkArguments(node.getFirstChildOfType(ASTArguments.class), node, data);
        }

        return data;
    }

    /**
     * Finds calls of "Boolean.valueOf".
     */
    @Override
    public Object visit(ASTPrimaryExpression node, Object data) {
        if (!TypeTestUtil.isA(Boolean.class, node)) {
            return data;
        }

        if (node.getNumChildren() >= 2 && node.getChild(0).getNumChildren() > 0
                && node.getChild(0).getChild(0) instanceof ASTName
                && node.getChild(0).getChild(0).hasImageEqualTo("Boolean.valueOf")) {
            ASTPrimarySuffix suffix = (ASTPrimarySuffix) node.getChild(1);
            checkArguments(suffix.getFirstChildOfType(ASTArguments.class), node, data);
        }

        return data;
    }

    private void checkArguments(ASTArguments arguments, JavaNode node, Object data) {
        if (arguments == null || arguments.size() != 1) {
            return;
        }
        String messagePart = node instanceof ASTAllocationExpression
                ? "Do not use `new Boolean"
                : "Do not use `Boolean.valueOf";
        ASTLiteral stringLiteral = getFirstArgStringLiteralOrNull(arguments);
        ASTBooleanLiteral boolLiteral = getFirstArgBooleanLiteralOrNull(arguments);
        if (stringLiteral != null) {
            if (stringLiteral.hasImageEqualTo("\"true\"")) {
                addViolationWithMessage(data, node, messagePart + "(\"true\")`, prefer `Boolean.TRUE`");
            } else if (stringLiteral.hasImageEqualTo("\"false\"")) {
                addViolationWithMessage(data, node, messagePart + "(\"false\")`, prefer `Boolean.FALSE`");
            }
        } else if (boolLiteral != null) {
            if (boolLiteral.isTrue()) {
                addViolationWithMessage(data, node, messagePart + "(true)`, prefer `Boolean.TRUE`");
            } else {
                addViolationWithMessage(data, node, messagePart + "(false)`, prefer `Boolean.FALSE`");
            }
        }
    }

    /**
     * <pre>
     * └─ Arguments
     *    └─ ArgumentList
     *       └─ Expression
     *          └─ PrimaryExpression
     *             └─ PrimaryPrefix
     *                └─ Literal
     * </pre>
     */
    private static ASTLiteral getFirstArgStringLiteralOrNull(ASTArguments arguments) {
        if (arguments.size() == 1) {
            ASTExpression expr = arguments.getFirstDescendantOfType(ASTExpression.class);
            ASTPrimaryExpression primaryExpr = getSingleChildOf(expr, ASTPrimaryExpression.class);
            ASTPrimaryPrefix prefix = getSingleChildOf(primaryExpr, ASTPrimaryPrefix.class);
            ASTLiteral literal = getSingleChildOf(prefix, ASTLiteral.class);
            if (literal != null && literal.isStringLiteral()) {
                return literal;
            }
        }
        return null;
    }

    /**
     * <pre>
     * └─ Arguments
     *    └─ ArgumentList
     *       └─ Expression
     *          └─ PrimaryExpression
     *             └─ PrimaryPrefix
     *                └─ Literal
     *                   └─ BooleanLiteral
     * </pre>
     */
    private static ASTBooleanLiteral getFirstArgBooleanLiteralOrNull(ASTArguments arguments) {
        if (arguments.size() == 1) {
            ASTExpression expr = arguments.getFirstDescendantOfType(ASTExpression.class);
            ASTPrimaryExpression primaryExpr = getSingleChildOf(expr, ASTPrimaryExpression.class);
            ASTPrimaryPrefix prefix = getSingleChildOf(primaryExpr, ASTPrimaryPrefix.class);
            ASTLiteral literal = getSingleChildOf(prefix, ASTLiteral.class);
            return getSingleChildOf(literal, ASTBooleanLiteral.class);
        }
        return null;
    }

    private static <N extends JavaNode> N getSingleChildOf(JavaNode node, Class<N> type) {
        if (node == null || node.getNumChildren() != 1
                || type != node.getChild(0).getClass()) {
            return null;
        }

        @SuppressWarnings("unchecked")
        N result = (N) node.getChild(0);
        return result;
    }
}
