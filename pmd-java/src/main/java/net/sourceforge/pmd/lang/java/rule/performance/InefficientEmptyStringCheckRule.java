/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.performance;

import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;

/**
 * This rule finds code which inefficiently determines empty strings.
 *
 * <p>
 * <pre>
 * str.trim().length()==0
 * </pre>
 * or
 * <pre>
 * str.trim().isEmpty()
 * </pre>
 * (for the same reason) is quite inefficient as trim() causes a new String to
 * be created. A Smarter code to check for an empty string would be:
 *
 * <pre>
 * private boolean checkTrimEmpty(String str) {
 *     for(int i = 0; i &lt; str.length(); i++) {
 *         if(!Character.isWhitespace(str.charAt(i))) {
 *             return false;
 *         }
 *     }
 *     return true;
 * }
 * </pre>
 * or you can refer to Apache's <code>StringUtils#isBlank</code>
 * (in commons-lang), Spring's <code>StringUtils#hasText</code> (in the Spring
 * framework) or Google's <code>CharMatcher#whitespace</code> (in Guava) for
 * existing implementations (some might include the check for != null).
 * </p>
 *
 * @author acaplan
 */
public class InefficientEmptyStringCheckRule extends AbstractJavaRulechainRule {

    public InefficientEmptyStringCheckRule() {
        super(ASTMethodCall.class);
    }

    @Override
    public Object visit(ASTMethodCall call, Object data) {
        if (isTrimCall(call.getQualifier())
            && (isLengthZeroCheck(call) || isIsEmptyCall(call))) {
            addViolation(data, call);
        }
        return null;
    }

    private static boolean isLengthZeroCheck(ASTMethodCall call) {
        return "length".equals(call.getMethodName())
            && call.getArguments().size() == 0
            && JavaRuleUtil.isZeroChecked(call);
    }

    private static boolean isTrimCall(ASTExpression expr) {
        if (expr instanceof ASTMethodCall) {
            ASTMethodCall call = (ASTMethodCall) expr;
            return "trim".equals(call.getMethodName())
                && call.getArguments().size() == 0
                && TypeTestUtil.isA(String.class, call.getQualifier());
        }
        return false;
    }


    private static boolean isIsEmptyCall(ASTExpression expr) {
        if (expr instanceof ASTMethodCall) {
            ASTMethodCall call = (ASTMethodCall) expr;
            return "isEmpty".equals(call.getMethodName())
                && call.getArguments().size() == 0;
        }
        return false;
    }

}

