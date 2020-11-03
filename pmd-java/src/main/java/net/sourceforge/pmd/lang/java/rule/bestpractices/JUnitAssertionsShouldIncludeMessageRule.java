/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;

import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.JUnitRuleUtil;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil.InvocationMatcher;

public class JUnitAssertionsShouldIncludeMessageRule extends AbstractJavaRulechainRule {

    private final List<InvocationMatcher> checks =
        listOf(
            InvocationMatcher.parse("_", "assertEquals(_,_)"),

            InvocationMatcher.parse("_", "assertTrue(_)"),
            InvocationMatcher.parse("_", "assertFalse(_)"),
            InvocationMatcher.parse("_", "assertSame(_,_)"),
            InvocationMatcher.parse("_", "assertNotSame(_,_)"),
            InvocationMatcher.parse("_", "assertNull(_)"),
            InvocationMatcher.parse("_", "assertNotNull(_)"),

            InvocationMatcher.parse("_", "assertArrayEquals(_,_)"),
            InvocationMatcher.parse("_", "assertThat(_,_)"),
            InvocationMatcher.parse("_", "fail()"),
            InvocationMatcher.parse("_", "assertEquals(float,float,float)"),
            InvocationMatcher.parse("_", "assertEquals(double,double,double)")
        );

    public JUnitAssertionsShouldIncludeMessageRule() {
        super(ASTMethodCall.class);
    }

    @Override
    public Object visit(ASTMethodCall node, Object data) {
        if (JUnitRuleUtil.isCallOnAssertionContainer(node)) {
            for (InvocationMatcher check : checks) {
                if (check.matchesCall(node)) {
                    addViolation(data, node);
                    break;
                }
            }
        }
        return null;
    }
}
