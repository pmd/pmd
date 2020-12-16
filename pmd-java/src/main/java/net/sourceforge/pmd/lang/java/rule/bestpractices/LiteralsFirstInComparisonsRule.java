/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import java.util.List;

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
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBody;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class LiteralsFirstInComparisonsRule extends AbstractJavaRule {

    private static final String[] COMPARISON_OPS = {".equals", ".equalsIgnoreCase", ".compareTo", ".compareToIgnoreCase", ".contentEquals"};

    public LiteralsFirstInComparisonsRule() {
        addRuleChainVisit(ASTPrimaryExpression.class);
    }

    @Override
    public Object visit(ASTPrimaryExpression expression, Object data) {
        if (violatesLiteralsFirstInComparisonsRule(expression)) {
            addViolation(data, expression);
        }
        return data;
    }

    private boolean violatesLiteralsFirstInComparisonsRule(ASTPrimaryExpression expression) {
        return !hasStringLiteralFirst(expression) && isNullableComparisonWithStringLiteral(expression);
    }

    private boolean hasStringLiteralFirst(ASTPrimaryExpression expression) {
        ASTPrimaryPrefix primaryPrefix = expression.getFirstChildOfType(ASTPrimaryPrefix.class);
        ASTLiteral firstLiteral = primaryPrefix.getFirstDescendantOfType(ASTLiteral.class);
        return firstLiteral != null && firstLiteral.isStringLiteral();
    }

    private boolean isNullableComparisonWithStringLiteral(ASTPrimaryExpression expression) {
        String opName = getOperationName(expression);
        ASTPrimarySuffix argsSuffix = getSuffixOfArguments(expression);
        return opName != null && argsSuffix != null && isStringLiteralComparison(opName, argsSuffix)
                && isNotWithinNullComparison(expression);
    }

    private String getOperationName(ASTPrimaryExpression primaryExpression) {
        return isMethodsChain(primaryExpression) ? getOperationNameBySuffix(primaryExpression)
                : getOperationNameByPrefix(primaryExpression);
    }

    private boolean isMethodsChain(ASTPrimaryExpression primaryExpression) {
        return primaryExpression.getNumChildren() > 2;
    }

    private String getOperationNameBySuffix(ASTPrimaryExpression primaryExpression) {
        ASTPrimarySuffix opAsSuffix = getPrimarySuffixAtIndexFromEnd(primaryExpression, 1);
        if (opAsSuffix != null) {
            String opName = opAsSuffix.getImage(); // name of pattern "operation"
            return "." + opName;
        }
        return null;
    }

    private String getOperationNameByPrefix(ASTPrimaryExpression primaryExpression) {
        ASTPrimaryPrefix opAsPrefix = primaryExpression.getFirstChildOfType(ASTPrimaryPrefix.class);
        if (opAsPrefix != null) {
            ASTName opName = opAsPrefix.getFirstChildOfType(ASTName.class); // name of pattern "*.operation"
            return opName != null ? opName.getImage() : null;
        }
        return null;
    }

    private ASTPrimarySuffix getSuffixOfArguments(ASTPrimaryExpression primaryExpression) {
        return getPrimarySuffixAtIndexFromEnd(primaryExpression, 0);
    }

    private ASTPrimarySuffix getPrimarySuffixAtIndexFromEnd(ASTPrimaryExpression primaryExpression, int indexFromEnd) {
        List<ASTPrimarySuffix> primarySuffixes = primaryExpression.findChildrenOfType(ASTPrimarySuffix.class);
        if (!primarySuffixes.isEmpty()) {
            int suffixIndex = primarySuffixes.size() - 1 - indexFromEnd;
            return primarySuffixes.get(suffixIndex);
        }
        return null;
    }

    private boolean isStringLiteralComparison(String opName, ASTPrimarySuffix argsSuffix) {
        return isComparisonOperation(opName) && isSingleStringLiteralArgument(argsSuffix);
    }

    private boolean isComparisonOperation(String op) {
        for (String comparisonOp : COMPARISON_OPS) {
            if (op.endsWith(comparisonOp)) {
                return true;
            }
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
        return isSingleArgumentSuffix(primarySuffix) && isStringLiteralFirstArgumentOfSuffix(primarySuffix);
    }

    private boolean isSingleArgumentSuffix(ASTPrimarySuffix primarySuffix) {
        return primarySuffix.getArgumentCount() == 1;
    }

    private boolean isStringLiteralFirstArgumentOfSuffix(ASTPrimarySuffix primarySuffix) {
        try {
            JavaNode firstLiteralArg = getFirstLiteralArgument(primarySuffix);
            JavaNode firstNameArg = getFirstNameArgument(primarySuffix);
            return isStringLiteral(firstLiteralArg) || isConstantString(firstNameArg);
        } catch (NullPointerException e) {
            return false;
        }
    }

    private JavaNode getFirstLiteralArgument(ASTPrimarySuffix primarySuffix) {
        return getArgumentPrimaryPrefixFromSuffix(primarySuffix).getFirstChildOfType(ASTLiteral.class);
    }

    private JavaNode getFirstNameArgument(ASTPrimarySuffix primarySuffix) {
        return getArgumentPrimaryPrefixFromSuffix(primarySuffix).getFirstChildOfType(ASTName.class);
    }

    private JavaNode getArgumentPrimaryPrefixFromSuffix(ASTPrimarySuffix primarySuffix) {
        ASTArguments arguments = primarySuffix.getFirstChildOfType(ASTArguments.class);
        ASTArgumentList argumentList = arguments.getFirstChildOfType(ASTArgumentList.class);
        ASTExpression expression = argumentList.getFirstChildOfType(ASTExpression.class);
        ASTPrimaryExpression primaryExpression = expression.getFirstChildOfType(ASTPrimaryExpression.class);
        return primaryExpression.getFirstChildOfType(ASTPrimaryPrefix.class);
    }

    private boolean isStringLiteral(JavaNode node) {
        if (node instanceof ASTLiteral) {
            ASTLiteral literal = (ASTLiteral) node;
            return literal.isStringLiteral();
        }
        return false;
    }

    private boolean isConstantString(JavaNode node) {
        if (node instanceof ASTName) {
            ASTName name = (ASTName) node;
            ASTClassOrInterfaceBody classBody = name.getFirstParentOfType(ASTClassOrInterfaceBody.class);
            ASTClassOrInterfaceBodyDeclaration classOrInterfaceBodyDeclaration = classBody.getFirstChildOfType(ASTClassOrInterfaceBodyDeclaration.class);
            List<ASTFieldDeclaration> fieldDeclarations = classOrInterfaceBodyDeclaration.findChildrenOfType(ASTFieldDeclaration.class);
            for (ASTFieldDeclaration fieldDeclaration : fieldDeclarations) {
                ASTVariableDeclarator declaration = fieldDeclaration.getFirstChildOfType(ASTVariableDeclarator.class);
                if (declaration.getName().equals(name.getImage())
                        && "class java.lang.String".equals(declaration.getType().toString())
                        && fieldDeclaration.isFinal()
                        && fieldDeclaration.isStatic()) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isNotWithinNullComparison(ASTPrimaryExpression node) {
        return !isWithinNullComparison(node);
    }

    /*
     * Expression/ConditionalAndExpression//EqualityExpression(@Image='!=']//NullLiteral
     * Expression/ConditionalOrExpression//EqualityExpression(@Image='==']//NullLiteral
     */
    private boolean isWithinNullComparison(ASTPrimaryExpression node) {
        for (ASTExpression parentExpr : node.getParentsOfType(ASTExpression.class)) {
            if (isNullComparison(parentExpr)) {
                return true;
            }
        }
        return false;
    }

    private boolean isNullComparison(ASTExpression expression) {
        return isAndNotNullComparison(expression) || isOrNullComparison(expression);
    }

    private boolean isAndNotNullComparison(ASTExpression expression) {
        ASTConditionalAndExpression andExpression = expression
                .getFirstChildOfType(ASTConditionalAndExpression.class);
        return andExpression != null && hasEqualityExpressionWithNullLiteral(andExpression, "!=");
    }

    private boolean isOrNullComparison(ASTExpression expression) {
        ASTConditionalOrExpression orExpression = expression
                .getFirstChildOfType(ASTConditionalOrExpression.class);
        return orExpression != null && hasEqualityExpressionWithNullLiteral(orExpression, "==");
    }

    private boolean hasEqualityExpressionWithNullLiteral(JavaNode node, String equalityOp) {
        ASTEqualityExpression equalityExpression = node.getFirstDescendantOfType(ASTEqualityExpression.class);
        if (equalityExpression != null && equalityExpression.hasImageEqualTo(equalityOp)) {
            return equalityExpression.hasDescendantOfType(ASTNullLiteral.class);
        }
        return false;
    }
}
