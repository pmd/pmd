/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Assert;
import org.junit.Test;

public class RuleSetFactoryDuplicatedRuleLoggingTest extends RulesetFactoryTestBase {

    @Test
    public void duplicatedRuleReferenceShouldWarn() {
        RuleSet ruleset = loadRuleSet("duplicatedRuleReference.xml");

        assertEquals(1, ruleset.getRules().size());
        Rule mockRule = ruleset.getRuleByName("DummyBasicMockRule");
        assertNotNull(mockRule);
        Assert.assertEquals(RulePriority.MEDIUM, mockRule.getPriority());
        verifyFoundAWarningWithMessage(containing(
            "The rule DummyBasicMockRule is referenced multiple times in ruleset 'Custom Rules'. "
                + "Only the last rule configuration is used."
        ));
    }

    @Test
    public void duplicatedRuleReferenceWithOverrideShouldNotWarn() {
        RuleSet ruleset = loadRuleSet("duplicatedRuleReferenceWithOverride.xml");

        assertEquals(2, ruleset.getRules().size());
        Rule mockRule = ruleset.getRuleByName("DummyBasicMockRule");
        assertNotNull(mockRule);
        assertEquals(RulePriority.HIGH, mockRule.getPriority());
        assertNotNull(ruleset.getRuleByName("SampleXPathRule"));
        verifyNoWarnings();
    }

    @Test
    public void duplicatedRuleReferenceWithOverrideBeforeShouldNotWarn() {
        RuleSet ruleset = loadRuleSet("duplicatedRuleReferenceWithOverrideBefore.xml");

        assertEquals(2, ruleset.getRules().size());
        Rule mockRule = ruleset.getRuleByName("DummyBasicMockRule");
        assertNotNull(mockRule);
        assertEquals(RulePriority.HIGH, mockRule.getPriority());
        assertNotNull(ruleset.getRuleByName("SampleXPathRule"));
        verifyNoWarnings();
    }

    @Test
    public void multipleDuplicates() {
        RuleSet ruleset = loadRuleSet("multipleDuplicates.xml");

        assertEquals(2, ruleset.getRules().size());
        Rule mockRule = ruleset.getRuleByName("DummyBasicMockRule");
        assertNotNull(mockRule);
        assertEquals(RulePriority.MEDIUM_HIGH, mockRule.getPriority());
        assertNotNull(ruleset.getRuleByName("SampleXPathRule"));
        verifyFoundAWarningWithMessage(containing(
            "The rule DummyBasicMockRule is referenced multiple times in ruleset 'Custom Rules'. "
                + "Only the last rule configuration is used."));
        verifyFoundAWarningWithMessage(containing(
            "The ruleset rulesets/dummy/basic.xml is referenced multiple times in ruleset 'Custom Rules'"));
    }

    protected RuleSet loadRuleSet(String ruleSetFilename) {
        return loadRuleSetInDir("net/sourceforge/pmd/rulesets/duplicatedRuleLoggingTest", ruleSetFilename);
    }
}
