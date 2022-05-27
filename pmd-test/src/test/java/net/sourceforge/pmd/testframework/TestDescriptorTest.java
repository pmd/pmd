/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.testframework;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.rule.MockRule;

public class TestDescriptorTest {
    @Test
    public void testMethodName() {
        Assert.assertEquals("MockRule_1_Name", create("Name"));
        Assert.assertEquals("MockRule_1_Tests_xyz", create("Tests xyz"));
        Assert.assertEquals("MockRule_1_Tests_xyz__false_positive_", create("Tests xyz (false positive)"));
        Assert.assertEquals("MockRule_1_Tests_xyz__123", create("Tests xyz #123"));
    }

    private String create(String description) {
        TestDescriptor descriptor = new TestDescriptor("foo", description, 0,
                new MockRule("MockRule", "desc", "msg", "ruleset"));
        descriptor.setNumberInDocument(1);
        return descriptor.getTestMethodName();
    }
}
