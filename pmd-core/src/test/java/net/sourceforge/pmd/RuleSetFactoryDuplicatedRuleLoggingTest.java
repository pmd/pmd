/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.github.stefanbirkner.systemlambda.SystemLambda;

class RuleSetFactoryDuplicatedRuleLoggingTest {

    @Test
    void duplicatedRuleReferenceShouldWarn() throws Exception {
        String log = SystemLambda.tapSystemErr(() -> {
            RuleSet ruleset = loadRuleSet("duplicatedRuleReference.xml");

            assertEquals(1, ruleset.getRules().size());
            Rule mockRule = ruleset.getRuleByName("DummyBasicMockRule");
            assertNotNull(mockRule);
            assertEquals(RulePriority.MEDIUM, mockRule.getPriority());
        });
        assertTrue(log.contains("The rule DummyBasicMockRule is referenced multiple times in \"Custom Rules\". "
                + "Only the last rule configuration is used."));
    }

    @Test
    void duplicatedRuleReferenceWithOverrideShouldNotWarn() throws Exception {
        String log = SystemLambda.tapSystemErr(() -> {
            RuleSet ruleset = loadRuleSet("duplicatedRuleReferenceWithOverride.xml");

            assertEquals(2, ruleset.getRules().size());
            Rule mockRule = ruleset.getRuleByName("DummyBasicMockRule");
            assertNotNull(mockRule);
            assertEquals(RulePriority.HIGH, mockRule.getPriority());
            assertNotNull(ruleset.getRuleByName("SampleXPathRule"));
        });
        assertTrue(log.isEmpty());
    }

    @Test
    void duplicatedRuleReferenceWithOverrideBeforeShouldNotWarn() throws Exception {
        String log = SystemLambda.tapSystemErr(() -> {
            RuleSet ruleset = loadRuleSet("duplicatedRuleReferenceWithOverrideBefore.xml");

            assertEquals(2, ruleset.getRules().size());
            Rule mockRule = ruleset.getRuleByName("DummyBasicMockRule");
            assertNotNull(mockRule);
            assertEquals(RulePriority.HIGH, mockRule.getPriority());
            assertNotNull(ruleset.getRuleByName("SampleXPathRule"));
        });
        assertTrue(log.isEmpty());
    }

    @Test
    void multipleDuplicates() throws Exception {
        String log = SystemLambda.tapSystemErr(() -> {
            RuleSet ruleset = loadRuleSet("multipleDuplicates.xml");

            assertEquals(2, ruleset.getRules().size());
            Rule mockRule = ruleset.getRuleByName("DummyBasicMockRule");
            assertNotNull(mockRule);
            assertEquals(RulePriority.MEDIUM_HIGH, mockRule.getPriority());
            assertNotNull(ruleset.getRuleByName("SampleXPathRule"));
        });
        assertTrue(log.contains("The rule DummyBasicMockRule is referenced multiple times in \"Custom Rules\". "
                + "Only the last rule configuration is used."));
        assertTrue(log.contains("The ruleset rulesets/dummy/basic.xml is referenced multiple times in \"Custom Rules\"."));
    }

    private RuleSet loadRuleSet(String ruleSetFilename) {
        return new RuleSetLoader().loadFromResource("net/sourceforge/pmd/rulesets/duplicatedRuleLoggingTest/" + ruleSetFilename);
    }
}
