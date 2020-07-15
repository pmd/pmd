/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import static java.lang.Math.max;

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
        if (hasFinalizeMethodCallViolation(block)) {
            addViolation(data, block);
        }
        return super.visit(block, data);
    }

    private boolean hasFinalizeMethodCallViolation(ASTBlock block) {
        if (isFinalizeMethodBlock(block)) {
            return callsNotSuperFinalizeMethod(block);
        }
        return callsFinalizeMethod(block);
    }

    private boolean isFinalizeMethodBlock(ASTBlock block) {
        ASTMethodDeclaration methodDeclaration = block.getFirstParentOfType(ASTMethodDeclaration.class);
        return methodDeclaration != null && isFinalizeMethodDeclaration(methodDeclaration);
    }

    private boolean isFinalizeMethodDeclaration(ASTMethodDeclaration methodDeclaration) {
        return "finalize".equals(methodDeclaration.getName()) && methodDeclaration.getArity() == 0;
    }

    private boolean callsNotSuperFinalizeMethod(ASTBlock block) {
        List<ASTPrimaryExpression> primaryExpressions = block.findDescendantsOfType(ASTPrimaryExpression.class);
        for (ASTPrimaryExpression primaryExpression : primaryExpressions) {
            if (isNotSuperFinalizeMethodCall(primaryExpression)) {
                return true;
            }
        }
        return false;
    }

    private boolean isNotSuperFinalizeMethodCall(ASTPrimaryExpression primaryExpression) {
        return isFinalizeMethodCall(primaryExpression) && isNotSuperMethodCall(primaryExpression);
    }

    private boolean isNotSuperMethodCall(ASTPrimaryExpression primaryExpression) {
        ASTPrimaryPrefix primaryPrefix = primaryExpression.getFirstChildOfType(ASTPrimaryPrefix.class);
        return primaryPrefix == null || !primaryPrefix.usesSuperModifier();
    }

    private boolean callsFinalizeMethod(ASTBlock block) {
        List<ASTPrimaryExpression> primaryExpressions = block.findDescendantsOfType(ASTPrimaryExpression.class);
        for (ASTPrimaryExpression primaryExpression : primaryExpressions) {
            if (isFinalizeMethodCall(primaryExpression)) {
                return true;
            }
        }
        return false;
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
        ASTPrimarySuffix primarySuffix = primaryExpression.getFirstChildOfType(ASTPrimarySuffix.class);
        if (primarySuffix != null) {
            int argsCount = primarySuffix.getArgumentCount();
            return max(argsCount, 0);
        }
        return -1;
    }
}
