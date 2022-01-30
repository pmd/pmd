/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import static java.util.Arrays.asList;

import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;

public class UnnecessaryCaseChangeRule extends AbstractJavaRulechainRule {

    private static final List<String> CASE_CHANGING_METHODS = asList("toLowerCase", "toUpperCase");
    private static final List<String> EQUALITY_METHODS = asList("equals", "equalsIgnoreCase");

    public UnnecessaryCaseChangeRule() {
        super(ASTMethodCall.class);
    }

    @Override
    public Object visit(ASTMethodCall node, Object data) {
        if (EQUALITY_METHODS.contains(node.getMethodName()) && node.getArguments().size() == 1) {
            if (isCaseChangingMethodCall(node.getQualifier())
                    || isCaseChangingMethodCall(node.getArguments().get(0))) {
                addViolation(data, node);
            }
        }
        return data;
    }

    /**
     * Checks for toLower/UpperCase method calls without arguments.
     * These method take an optional Locale as an argument - in that case,
     * these case conversions are considered deliberate.
     */
    private boolean isCaseChangingMethodCall(ASTExpression expr) {
        if (expr instanceof ASTMethodCall) {
            ASTMethodCall call = (ASTMethodCall) expr;
            return CASE_CHANGING_METHODS.contains(call.getMethodName()) && call.getArguments().size() == 0;
        }
        return false;
    }
}
