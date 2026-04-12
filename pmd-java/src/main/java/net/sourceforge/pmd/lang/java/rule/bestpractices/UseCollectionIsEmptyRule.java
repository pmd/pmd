/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import java.util.Collection;
import java.util.Map;

import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.reporting.RuleContext;

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
    public RuleContext visit(ASTMethodCall call, RuleContext data) {
        if ((TypeTestUtil.isA(Collection.class, call.getQualifier())
            || TypeTestUtil.isA(Map.class, call.getQualifier()))
            && isSizeZeroCheck(call)) {
            data.addViolation(call);
        }
        return null;
    }

    private static boolean isSizeZeroCheck(ASTMethodCall call) {
        return "size".equals(call.getMethodName())
            && call.getArguments().size() == 0
            && JavaRuleUtil.isZeroChecked(call);
    }
}
