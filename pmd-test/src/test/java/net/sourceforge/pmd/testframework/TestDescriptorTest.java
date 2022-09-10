/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.testframework;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.PlainTextLanguage;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.AbstractRule;

public class TestDescriptorTest {
    @Test
    public void testMethodName() {
        Assertions.assertEquals("MockRule_1_Name", create("Name"));
        Assertions.assertEquals("MockRule_1_Tests_xyz", create("Tests xyz"));
        Assertions.assertEquals("MockRule_1_Tests_xyz__false_positive_", create("Tests xyz (false positive)"));
        Assertions.assertEquals("MockRule_1_Tests_xyz__123", create("Tests xyz #123"));
    }

    private String create(String description) {
        TestDescriptor descriptor = new TestDescriptor("foo", description, 0,
                new MockRule());
        descriptor.setNumberInDocument(1);
        return descriptor.getTestMethodName();
    }

    private static final class MockRule extends AbstractRule {
        @Override
        public Language getLanguage() {
            return PlainTextLanguage.getInstance();
        }

        @Override
        public String getName() {
            return "MockRule";
        }

        @Override
        public void apply(Node target, RuleContext ctx) {
        }
    }
}
