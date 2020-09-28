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
        RuleViolation violation = DefaultRuleViolationFactory.defaultInstance().createViolation(new TestRule(), DummyTreeUtil.tree(DummyRoot::new), "file", "Some message");

        Assert.assertEquals("Some message", violation.getDescription());
    }

}
