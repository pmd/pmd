/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import java.util.List;
import java.util.regex.Pattern;

import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class AvoidCallingFinalizeRule extends AbstractJavaRule {

    private static final Pattern FINALIZE_METHOD_PATTERN = Pattern.compile("^(.+\\.)?finalize$");

    @Override
    public Object visit(ASTPrimaryExpression primaryExpression, Object data) {
        if (isIncorrectFinalizeMethodCall(primaryExpression)) {
            addViolation(data, primaryExpression);
        }
        return data;
    }

    private boolean isIncorrectFinalizeMethodCall(ASTPrimaryExpression primaryExpression) {
        return isFinalizeMethodCall(primaryExpression)
                && (isNotInFinalizeMethod(primaryExpression) || isNotSuperMethodCall(primaryExpression));
    }

    private boolean isNotInFinalizeMethod(ASTPrimaryExpression primaryExpression) {
        ASTMethodDeclaration methodDeclaration = primaryExpression.getFirstParentOfType(ASTMethodDeclaration.class);
        return methodDeclaration == null || isNotFinalizeMethodDeclaration(methodDeclaration);
    }

    private boolean isNotFinalizeMethodDeclaration(ASTMethodDeclaration methodDeclaration) {
        return !isFinalizeMethodDeclaration(methodDeclaration);
    }

    private boolean isFinalizeMethodDeclaration(ASTMethodDeclaration methodDeclaration) {
        return "finalize".equals(methodDeclaration.getName()) && methodDeclaration.getArity() == 0;
    }

    private boolean isFinalizeMethodCall(ASTPrimaryExpression primaryExpression) {
        return hasFinalizeName(primaryExpression) && getArgsCount(primaryExpression) == 0;
    }

    private boolean hasFinalizeName(ASTPrimaryExpression primaryExpression) {
        List<JavaNode> expressionNodes = primaryExpression.findDescendantsOfType(JavaNode.class);
        for (JavaNode expressionNode : expressionNodes) {
            if (isFinalizeName(expressionNode.getImage())) {
                return true;
            }
        }
        return false;
    }

    private boolean isFinalizeName(String name) {
        return name != null && FINALIZE_METHOD_PATTERN.matcher(name).find();
    }

    private int getArgsCount(ASTPrimaryExpression primaryExpression) {
        List<ASTPrimarySuffix> primarySuffixes = primaryExpression.findChildrenOfType(ASTPrimarySuffix.class);
        if (!primarySuffixes.isEmpty()) {
            int lastSuffixIndex = primarySuffixes.size() - 1;
            return primarySuffixes.get(lastSuffixIndex).getArgumentCount();
        }
        return -1;
    }

    private boolean isNotSuperMethodCall(ASTPrimaryExpression primaryExpression) {
        ASTPrimaryPrefix primaryPrefix = primaryExpression.getFirstChildOfType(ASTPrimaryPrefix.class);
        return primaryPrefix == null || !primaryPrefix.usesSuperModifier();
    }
}
