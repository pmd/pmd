/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static net.sourceforge.pmd.util.CollectionUtil.setOf;

import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTArguments;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBody;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalAndExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalOrExpression;
import net.sourceforge.pmd.lang.java.ast.ASTEqualityExpression;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTNullLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.ASTStringLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;

public class LiteralsFirstInComparisonsRule extends AbstractJavaRulechainRule {

    private static final Set<String> STRING_COMPARISONS =
        setOf("equalsIgnoreCase",
              "compareTo",
              "compareToIgnoreCase",
              "contentEquals");

    public LiteralsFirstInComparisonsRule() {
        super(ASTMethodCall.class);
    }

    @Override
    public Object visit(ASTMethodCall call, Object data) {
        if ("equals".equals(call.getMethodName())
            && call.getArguments().size() == 1
            && isEqualsObjectAndNotAnOverload(call)) {
            checkArgs((RuleContext) data, call);
        } else if (STRING_COMPARISONS.contains(call.getMethodName())
            && call.getArguments().size() == 1
            && TypeTestUtil.isDeclaredInClass(String.class, call.getMethodType())) {
            checkArgs((RuleContext) data, call);
        }
        return data;
    }

    private boolean isEqualsObjectAndNotAnOverload(ASTMethodCall call) {
        if (call.getOverloadSelectionInfo().isFailed()) {
            return true; // failed selection is considered probably equals(Object)
        }
        return call.getMethodType().getFormalParameters().equals(listOf(call.getTypeSystem().OBJECT));
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
        return getArgumentPrimaryPrefix(primarySuffix).getFirstChildOfType(ASTLiteral.class);
    }

    private JavaNode getFirstNameArgument(ASTPrimarySuffix primarySuffix) {
        return getArgumentPrimaryPrefix(primarySuffix).getFirstChildOfType(ASTName.class);
    }

    private JavaNode getArgumentPrimaryPrefix(ASTPrimarySuffix primarySuffix) {
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
                        && String.class.equals(declaration.getType())
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

    private void checkArgs(RuleContext ctx, ASTMethodCall call) {
        ASTExpression arg = call.getArguments().get(0);
        ASTExpression qualifier = call.getQualifier();
        if (!(qualifier instanceof ASTStringLiteral) && arg instanceof ASTStringLiteral) {
            addViolation(ctx, call);
        }
    }

    private boolean hasEqualityExpressionWithNullLiteral(JavaNode node, String equalityOp) {
        ASTEqualityExpression equalityExpression = node.getFirstDescendantOfType(ASTEqualityExpression.class);
        if (equalityExpression != null && equalityExpression.hasImageEqualTo(equalityOp)) {
            return equalityExpression.hasDescendantOfType(ASTNullLiteral.class);
        }
        return false;
    }
}
