/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import java.util.Collection;
import java.util.Map;

import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;

/**
 * Detect structures like "foo.size() == 0" and suggest replacing them with
 * foo.isEmpty(). Will also find != 0 (replaceable with !isEmpty()).
 *
 * @author Jason Bennett
 */
public class UseCollectionIsEmptyRule extends AbstractJavaRulechainRule {

    public UseCollectionIsEmptyRule() {
        super(ASTMethodCall.class);
    }

    @Override
    public Object visit(ASTMethodCall call, Object data) {
        if ((TypeTestUtil.isA(Collection.class, call.getQualifier())
            || TypeTestUtil.isA(Map.class, call.getQualifier()))
            && isSizeZeroCheck(call)) {
            addViolation(data, call);
        }
        return null;
    }

    private static boolean isSizeZeroCheck(ASTMethodCall call) {
        return call.getMethodName().equals("size")
            && call.getArguments().size() == 0
            && JavaRuleUtil.isZeroChecked(call);
    }
}
