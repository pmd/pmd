/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.sourceforge.pmd.junit.JavaUtilLoggingRule;
import net.sourceforge.pmd.junit.LocaleRule;

public class RuleSetFactoryDuplicatedRuleLoggingTest {
    @org.junit.Rule
    public ExpectedException ex = ExpectedException.none();

    @org.junit.Rule
    public LocaleRule localeRule = LocaleRule.en();

    @org.junit.Rule
    public JavaUtilLoggingRule logging = new JavaUtilLoggingRule(RuleSetFactory.class.getName());

    @Test
    public void duplicatedRuleReferenceShouldWarn() throws RuleSetNotFoundException {
        RuleSet ruleset = loadRuleSet("duplicatedRuleReference.xml");

        assertEquals(1, ruleset.getRules().size());
        Rule mockRule = ruleset.getRuleByName("DummyBasicMockRule");
        assertNotNull(mockRule);
        assertEquals(RulePriority.MEDIUM, mockRule.getPriority());
        assertTrue(logging.getLog().contains("The rule DummyBasicMockRule is referenced multiple times in \"Custom Rules\". "
                + "Only the last rule configuration is used."));
    }

    @Test
    public void duplicatedRuleReferenceWithOverrideShouldNotWarn() throws RuleSetNotFoundException {
        RuleSet ruleset = loadRuleSet("duplicatedRuleReferenceWithOverride.xml");

        assertEquals(2, ruleset.getRules().size());
        Rule mockRule = ruleset.getRuleByName("DummyBasicMockRule");
        assertNotNull(mockRule);
        assertEquals(RulePriority.HIGH, mockRule.getPriority());
        assertNotNull(ruleset.getRuleByName("SampleXPathRule"));
        assertTrue(logging.getLog().isEmpty());
    }

    @Test
    public void duplicatedRuleReferenceWithOverrideBeforeShouldNotWarn() throws RuleSetNotFoundException {
        RuleSet ruleset = loadRuleSet("duplicatedRuleReferenceWithOverrideBefore.xml");

        assertEquals(2, ruleset.getRules().size());
        Rule mockRule = ruleset.getRuleByName("DummyBasicMockRule");
        assertNotNull(mockRule);
        assertEquals(RulePriority.HIGH, mockRule.getPriority());
        assertNotNull(ruleset.getRuleByName("SampleXPathRule"));
        assertTrue(logging.getLog().isEmpty());
    }

    @Test
    public void multipleDuplicates() throws RuleSetNotFoundException {
        RuleSet ruleset = loadRuleSet("multipleDuplicates.xml");

        assertEquals(2, ruleset.getRules().size());
        Rule mockRule = ruleset.getRuleByName("DummyBasicMockRule");
        assertNotNull(mockRule);
        assertEquals(RulePriority.MEDIUM_HIGH, mockRule.getPriority());
        assertNotNull(ruleset.getRuleByName("SampleXPathRule"));
        assertTrue(logging.getLog().contains("The rule DummyBasicMockRule is referenced multiple times in \"Custom Rules\". "
                + "Only the last rule configuration is used."));
        assertTrue(logging.getLog().contains("The ruleset rulesets/dummy/basic.xml is referenced multiple times in \"Custom Rules\"."));
    }

    private RuleSet loadRuleSet(String ruleSetFilename) throws RuleSetNotFoundException {
        RuleSetFactory rsf = RulesetsFactoryUtils.defaultFactory();
        return rsf.createRuleSet("net/sourceforge/pmd/rulesets/duplicatedRuleLoggingTest/" + ruleSetFilename);
    }
}
