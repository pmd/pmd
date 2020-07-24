/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import static java.util.Arrays.asList;

import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTArguments;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class UnnecessaryCaseChangeRule extends AbstractJavaRule {

    private static final List<String> CASE_CHANGING_METHODS = asList("toLowerCase", "toUpperCase");
    private static final List<String> EQUALITY_METHODS = asList("equals", "equalsIgnoreCase");

    @Override
    public Object visit(ASTPrimaryExpression expr, Object data) {
        int caseChangingCallIndex = getCaseChangingMethodCallIndex(expr);
        if (caseChangingCallIndex != -1) {
            int chainedMethodCallIndex = caseChangingCallIndex + 2;
            if (hasEqualsMethodCallChainedAtPosition(expr, chainedMethodCallIndex)
                    || isArgumentOfEqualsMethodCall(expr)) {
                addViolation(data, expr);
            }
        }
        return super.visit(expr, data);
    }

    private int getCaseChangingMethodCallIndex(ASTPrimaryExpression expr) {
        List<JavaNode> exprNodes = expr.findChildrenOfType(JavaNode.class);
        for (int callArgsIndex = 1; callArgsIndex < exprNodes.size(); callArgsIndex++) {
            int callIndex = callArgsIndex - 1;
            if (isCaseChangingMethodCall(exprNodes.get(callIndex), exprNodes.get(callArgsIndex))) {
                return callIndex;
            }
        }
        return -1;
    }

    private boolean isCaseChangingMethodCall(JavaNode methodCall, JavaNode methodCallArgs) {
        String methodName = getCalledMethodName(methodCall);
        int methodArgsCount = getCalledMethodArgsCount(methodCallArgs);
        return methodName != null
                && isNameOfCaseChangingMethod(methodName) && methodArgsCount == 0;
    }

    private String getCalledMethodName(JavaNode methodCall) {
        if (methodCall instanceof ASTPrimaryPrefix) {
            ASTName methodName = methodCall.getFirstDescendantOfType(ASTName.class);
            return methodName != null ? methodName.getImage() : null;
        }
        return methodCall.getImage();
    }

    private boolean isNameOfCaseChangingMethod(String methodName) {
        for (String caseChangingMethod : CASE_CHANGING_METHODS) {
            if (methodName.endsWith(caseChangingMethod)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasEqualsMethodCallChainedAtPosition(ASTPrimaryExpression expr, int pos) {
        int argsPos = pos + 1;
        if (hasNodeAtPosition(expr, argsPos)) {
            return isEqualsMethodCall(expr.getChild(pos), expr.getChild(argsPos));
        }
        return false;
    }

    private boolean hasNodeAtPosition(ASTPrimaryExpression expr, int pos) {
        return pos < expr.getNumChildren();
    }

    private boolean isArgumentOfEqualsMethodCall(ASTPrimaryExpression expr) {
        ASTPrimarySuffix parentMethodCallArgs = getParentMethodCallArgsSuffix(expr);
        if (parentMethodCallArgs != null) {
            List<JavaNode> parentNodes = parentMethodCallArgs.getParent().findChildrenOfType(JavaNode.class);
            int parentMethodCallIndex = parentNodes.indexOf(parentMethodCallArgs) - 1;
            JavaNode parentMethodCall = parentNodes.get(parentMethodCallIndex);
            return isEqualsMethodCall(parentMethodCall, parentMethodCallArgs);
        }
        return false;
    }

    private ASTPrimarySuffix getParentMethodCallArgsSuffix(ASTPrimaryExpression expr) {
        JavaNode parent = expr.getParent().getParent(); // ASTArgumentList/ASTExpression/ASTPrimaryExpression
        return parent instanceof ASTArgumentList
                ? parent.getFirstParentOfType(ASTPrimarySuffix.class)
                : null;
    }

    private boolean isEqualsMethodCall(JavaNode methodCall, JavaNode methodCallArgs) {
        String methodName = methodCall.getImage();
        int methodArgsCount = getCalledMethodArgsCount(methodCallArgs);
        return methodName != null
                && EQUALITY_METHODS.contains(methodName) && methodArgsCount == 1;
    }

    private int getCalledMethodArgsCount(JavaNode methodCall) {
        ASTArguments args = methodCall.getFirstDescendantOfType(ASTArguments.class);
        return args != null ? args.size() : -1;
    }
}
