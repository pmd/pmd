/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.TestFrameworksUtil;
import net.sourceforge.pmd.lang.java.types.InvocationMatcher;
import net.sourceforge.pmd.lang.java.types.InvocationMatcher.CompoundInvocationMatcher;

public class JUnitAssertionsShouldIncludeMessageRule extends AbstractJavaRulechainRule {

    private final CompoundInvocationMatcher checks =
        InvocationMatcher.parseAll(
            "_#assertEquals(_,_)",
            "_#assertTrue(_)",
            "_#assertFalse(_)",
            "_#assertSame(_,_)",
            "_#assertNotSame(_,_)",
            "_#assertNull(_)",
            "_#assertNotNull(_)",
            "_#assertArrayEquals(_,_)",
            "_#assertThat(_,_)",
            "_#fail()",
            "_#assertEquals(float,float,float)",
            "_#assertEquals(double,double,double)"
        );

    public JUnitAssertionsShouldIncludeMessageRule() {
        super(ASTMethodCall.class);
    }

    @Override
    public Object visit(ASTMethodCall node, Object data) {
        if (TestFrameworksUtil.isCallOnAssertionContainer(node)) {
            if (checks.anyMatch(node)) {
                addViolation(data, node);
            }
        }
        return null;
    }
}
