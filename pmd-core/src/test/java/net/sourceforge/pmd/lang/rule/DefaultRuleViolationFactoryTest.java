/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.ast.DummyRoot;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.impl.DummyTreeUtil;
import net.sourceforge.pmd.lang.rule.impl.DefaultRuleViolationFactory;

public class DefaultRuleViolationFactoryTest {

    private static class TestRule extends AbstractRule {
        @Override
        public void apply(Node target, RuleContext ctx) {
            throw new UnsupportedOperationException("not implemented");
        }
    }

    @Test
    public void testMessage() {
        RuleViolation violation = makeViolation("Some message");

        Assert.assertEquals("Some message", violation.getDescription());
    }

    @Test
    public void testMessageEscaping() {
        RuleViolation violation = makeViolation("message with \"'{'\"");

        Assert.assertEquals("message with \"{\"", violation.getDescription());
    }

    @Test
    public void testMessageEscaping2() {
        RuleViolation violation = makeViolation("message with ${ohio}");

        Assert.assertEquals("message with ${ohio}", violation.getDescription());
    }

    private RuleViolation makeViolation(String unescapedMessage, Object... args) {
        return DefaultRuleViolationFactory.defaultInstance().formatViolation(new TestRule(), DummyTreeUtil.tree(DummyRoot::new), "file", unescapedMessage, args);
    }

}
