/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemErrRule;

import net.sourceforge.pmd.junit.LocaleRule;

public class RuleSetFactoryDuplicatedRuleLoggingTest {
    @org.junit.Rule
    public LocaleRule localeRule = LocaleRule.en();

    @org.junit.Rule
    public final SystemErrRule systemErrRule = new SystemErrRule().mute().enableLog();

    @Test
    public void duplicatedRuleReferenceShouldWarn() {
        RuleSet ruleset = loadRuleSet("duplicatedRuleReference.xml");

        assertEquals(1, ruleset.getRules().size());
        Rule mockRule = ruleset.getRuleByName("DummyBasicMockRule");
        assertNotNull(mockRule);
        assertEquals(RulePriority.MEDIUM, mockRule.getPriority());
        assertTrue(systemErrRule.getLog().contains("The rule DummyBasicMockRule is referenced multiple times in \"Custom Rules\". "
                + "Only the last rule configuration is used."));
    }

    @Test
    public void duplicatedRuleReferenceWithOverrideShouldNotWarn() {
        RuleSet ruleset = loadRuleSet("duplicatedRuleReferenceWithOverride.xml");

        assertEquals(2, ruleset.getRules().size());
        Rule mockRule = ruleset.getRuleByName("DummyBasicMockRule");
        assertNotNull(mockRule);
        assertEquals(RulePriority.HIGH, mockRule.getPriority());
        assertNotNull(ruleset.getRuleByName("SampleXPathRule"));
        assertTrue(systemErrRule.getLog().isEmpty());
    }

    @Test
    public void duplicatedRuleReferenceWithOverrideBeforeShouldNotWarn() {
        RuleSet ruleset = loadRuleSet("duplicatedRuleReferenceWithOverrideBefore.xml");

        assertEquals(2, ruleset.getRules().size());
        Rule mockRule = ruleset.getRuleByName("DummyBasicMockRule");
        assertNotNull(mockRule);
        assertEquals(RulePriority.HIGH, mockRule.getPriority());
        assertNotNull(ruleset.getRuleByName("SampleXPathRule"));
        assertTrue(systemErrRule.getLog().isEmpty());
    }

    @Test
    public void multipleDuplicates() {
        RuleSet ruleset = loadRuleSet("multipleDuplicates.xml");

        assertEquals(2, ruleset.getRules().size());
        Rule mockRule = ruleset.getRuleByName("DummyBasicMockRule");
        assertNotNull(mockRule);
        assertEquals(RulePriority.MEDIUM_HIGH, mockRule.getPriority());
        assertNotNull(ruleset.getRuleByName("SampleXPathRule"));
        assertTrue(systemErrRule.getLog().contains("The rule DummyBasicMockRule is referenced multiple times in \"Custom Rules\". "
                + "Only the last rule configuration is used."));
        assertTrue(systemErrRule.getLog().contains("The ruleset rulesets/dummy/basic.xml is referenced multiple times in \"Custom Rules\"."));
    }

    private RuleSet loadRuleSet(String ruleSetFilename) {
        return new RuleSetLoader().loadFromResource("net/sourceforge/pmd/rulesets/duplicatedRuleLoggingTest/" + ruleSetFilename);
    }
}
