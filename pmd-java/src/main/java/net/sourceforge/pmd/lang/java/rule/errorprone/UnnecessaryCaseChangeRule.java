/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import static java.util.Arrays.asList;

import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTArguments;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class UnnecessaryCaseChangeRule extends AbstractJavaRule {

    private static final List<String> CASE_CHANGING_METHODS = asList("toLowerCase", "toUpperCase");
    private static final List<String> EQUALITY_METHODS = asList("equals", "equalsIgnoreCase");

    @Override
    public Object visit(ASTPrimaryExpression expr, Object data) {
        if (hasUnnecessaryCaseChange(expr)) {
            addViolation(data, expr);
        }
        return super.visit(expr, data);
    }

    private boolean hasUnnecessaryCaseChange(ASTPrimaryExpression expr) {
        int equalsMethodCallIndex = getEqualsMethodCallIndex(expr);
        if (equalsMethodCallIndex != -1) {
            int equalsMethodCallArgsIndex = equalsMethodCallIndex + 1;
            ASTPrimaryExpression equalsCallArgs = getMethodCallArgsAtPosition(expr, equalsMethodCallArgsIndex);
            return anyHasCaseChangingMethodCall(expr, equalsCallArgs);
        }
        return false;
    }

    private int getEqualsMethodCallIndex(ASTPrimaryExpression expr) {
        for (int callIndex = 0; callIndex < expr.getNumChildren(); callIndex++) {
            JavaNode methodCall = expr.getChild(callIndex);
            if (isEqualsMethodCall(methodCall)) {
                return callIndex;
            }
        }
        return -1;
    }

    private boolean isEqualsMethodCall(JavaNode methodCall) {
        return calledMethodHasNameFromList(methodCall, EQUALITY_METHODS);
    }

    private ASTPrimaryExpression getMethodCallArgsAtPosition(ASTPrimaryExpression expr, int argsPos) {
        if (hasChildAtPosition(expr, argsPos)) {
            JavaNode methodCallArgs = expr.getChild(argsPos);
            return methodCallArgs.getFirstDescendantOfType(ASTPrimaryExpression.class);
        }
        return null;
    }

    private boolean hasChildAtPosition(ASTPrimaryExpression expr, int pos) {
        return expr.getNumChildren() > pos;
    }

    private boolean anyHasCaseChangingMethodCall(ASTPrimaryExpression ... exprs) {
        for (ASTPrimaryExpression expr : exprs) {
            if (expr != null && hasCaseChangingMethodCall(expr)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasCaseChangingMethodCall(ASTPrimaryExpression expr) {
        for (int callArgsIndex = 1; callArgsIndex < expr.getNumChildren(); callArgsIndex++) {
            JavaNode methodCall = expr.getChild(callArgsIndex - 1);
            JavaNode methodCallArgs = expr.getChild(callArgsIndex);
            if (isCaseChangingMethodCall(methodCall, methodCallArgs)) {
                return true;
            }
        }
        return false;
    }

    private boolean isCaseChangingMethodCall(JavaNode methodCall, JavaNode methodCallArgs) {
        if (calledMethodHasNameFromList(methodCall, CASE_CHANGING_METHODS)) {
            ASTArguments args = methodCallArgs.getFirstDescendantOfType(ASTArguments.class);
            return args != null && args.size() == 0;
        }
        return false;
    }

    private boolean calledMethodHasNameFromList(JavaNode methodCall, List<String> nameList) {
        String methodName = getCalledMethodName(methodCall);
        if (methodName != null) {
            for (String nameFromList : nameList) {
                if (methodName.endsWith(nameFromList)) {
                    return true;
                }
            }
        }
        return false;
    }

    private String getCalledMethodName(JavaNode methodCall) {
        String methodName = methodCall.getImage();
        if (methodName == null) {
            ASTName name = methodCall.getFirstDescendantOfType(ASTName.class);
            return name != null ? methodNameFromCallImage(name.getImage()) : null;
        }
        return methodName;
    }

    private String methodNameFromCallImage(String methodCallImage) {
        if (methodCallImage.contains(".")) {
            String[] callParts = methodCallImage.split("\\.");
            return callParts[1];
        }
        return methodCallImage;
    }
}
