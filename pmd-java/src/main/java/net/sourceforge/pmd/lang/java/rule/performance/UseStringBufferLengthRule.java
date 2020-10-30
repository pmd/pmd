/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.performance;

import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;

/**
 * This rule finds places where StringBuffer.toString() is called just to see if
 * the string is 0 length by either using .equals("") or toString().length().
 *
 * <pre>
 * StringBuffer sb = new StringBuffer(&quot;some string&quot;);
 * if (sb.toString().equals(&quot;&quot;)) {
 *     // this is wrong
 * }
 * if (sb.length() == 0) {
 *     // this is right
 * }
 * </pre>
 *
 * @author acaplan
 * @author Philip Graf
 */
public class UseStringBufferLengthRule extends AbstractJavaRulechainRule {

    public UseStringBufferLengthRule() {
        super(ASTMethodCall.class);
    }

    @Override
    public Object visit(ASTMethodCall call, Object data) {
        if (call.getMethodName().equals("toString")
            && call.getArguments().size() == 0
            && TypeTestUtil.isA(CharSequence.class, call.getQualifier())
            && !TypeTestUtil.isA(String.class, call.getQualifier())
            && isLengthCall(call.getParent())) {
            addViolation(data, call.getParent());
        }
        return data;
    }

    private boolean isLengthCall(JavaNode node) {
        if (node instanceof ASTMethodCall) {
            ASTMethodCall call = (ASTMethodCall) node;
            return call.getMethodName().equals("length")
                && call.getArguments().size() == 0;
        }
        return false;
    }
}
