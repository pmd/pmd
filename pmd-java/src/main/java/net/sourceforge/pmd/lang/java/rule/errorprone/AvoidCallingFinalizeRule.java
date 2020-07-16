/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class AvoidCallingFinalizeRule extends AbstractJavaRule {

    private static final Pattern FINALIZE_METHOD_PATTERN = Pattern.compile("^(.+\\.)?finalize$");

    @Override
    public Object visit(ASTBlock block, Object data) {
        List<ASTPrimaryExpression> finalizeMethodCalls = getIncorrectFinalizeMethodCalls(block);
        for (ASTPrimaryExpression finalizeMethodCall : finalizeMethodCalls) {
            addViolation(data, finalizeMethodCall);
        }
        return data;
    }

    private List<ASTPrimaryExpression> getIncorrectFinalizeMethodCalls(ASTBlock block) {
        if (isFinalizeMethodBlock(block)) {
            return getNotSuperFinalizeMethodCalls(block);
        }
        return getFinalizeMethodCalls(block);
    }

    private boolean isFinalizeMethodBlock(ASTBlock block) {
        ASTMethodDeclaration methodDeclaration = block.getFirstParentOfType(ASTMethodDeclaration.class);
        return methodDeclaration != null && isFinalizeMethodDeclaration(methodDeclaration);
    }

    private boolean isFinalizeMethodDeclaration(ASTMethodDeclaration methodDeclaration) {
        return "finalize".equals(methodDeclaration.getName()) && methodDeclaration.getArity() == 0;
    }

    private List<ASTPrimaryExpression> getNotSuperFinalizeMethodCalls(ASTBlock block) {
        List<ASTPrimaryExpression> finalizeMethodCalls = getFinalizeMethodCalls(block);
        List<ASTPrimaryExpression> notSuperCalls = new ArrayList<>();
        for (ASTPrimaryExpression finalizeMethodCall : finalizeMethodCalls) {
            if (isNotSuperMethodCall(finalizeMethodCall)) {
                notSuperCalls.add(finalizeMethodCall);
            }
        }
        return notSuperCalls;
    }

    private boolean isNotSuperMethodCall(ASTPrimaryExpression primaryExpression) {
        ASTPrimaryPrefix primaryPrefix = primaryExpression.getFirstChildOfType(ASTPrimaryPrefix.class);
        return primaryPrefix == null || !primaryPrefix.usesSuperModifier();
    }

    private List<ASTPrimaryExpression> getFinalizeMethodCalls(ASTBlock block) {
        List<ASTPrimaryExpression> primaryExpressions = block.findDescendantsOfType(ASTPrimaryExpression.class);
        List<ASTPrimaryExpression> finalizeMethodCalls = new ArrayList<>();
        for (ASTPrimaryExpression primaryExpression : primaryExpressions) {
            if (isFinalizeMethodCall(primaryExpression)) {
                finalizeMethodCalls.add(primaryExpression);
            }
        }
        return finalizeMethodCalls;
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
}
