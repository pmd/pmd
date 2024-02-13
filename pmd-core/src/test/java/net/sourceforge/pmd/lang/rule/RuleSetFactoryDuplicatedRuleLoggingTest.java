/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.github.stefanbirkner.systemlambda.SystemLambda;

class RuleSetFactoryDuplicatedRuleLoggingTest extends RulesetFactoryTestBase {

    private static final String DIR = "net/sourceforge/pmd/rulesets/duplicatedRuleLoggingTest";

    @Test
    void duplicatedRuleReferenceShouldWarn() throws Exception {
        String log = SystemLambda.tapSystemErr(() -> {
            RuleSet ruleset = loadRuleSetInDir(DIR, "duplicatedRuleReference.xml");

            assertEquals(1, ruleset.getRules().size());
            Rule mockRule = ruleset.getRuleByName("DummyBasicMockRule");
            assertNotNull(mockRule);
            assertEquals(RulePriority.MEDIUM, mockRule.getPriority());
        });
        assertThat(log, containsString(
            "The rule DummyBasicMockRule is referenced multiple times in ruleset 'Custom Rules'. "
                + "Only the last rule configuration is used"));
    }

    @Test
    void duplicatedRuleReferenceWithOverrideShouldNotWarn() throws Exception {
        String log = SystemLambda.tapSystemErr(() -> {
            RuleSet ruleset = loadRuleSetInDir(DIR, "duplicatedRuleReferenceWithOverride.xml");

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
            RuleSet ruleset = loadRuleSetInDir(DIR, "duplicatedRuleReferenceWithOverrideBefore.xml");
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
            RuleSet ruleset = loadRuleSetInDir(DIR, "multipleDuplicates.xml");

            assertEquals(2, ruleset.getRules().size());
            Rule mockRule = ruleset.getRuleByName("DummyBasicMockRule");
            assertNotNull(mockRule);
            assertEquals(RulePriority.MEDIUM_HIGH, mockRule.getPriority());
            assertNotNull(ruleset.getRuleByName("SampleXPathRule"));
        });
        assertThat(log, containsString("The rule DummyBasicMockRule is referenced multiple times in ruleset 'Custom Rules'. Only the last rule configuration is used."));
        assertThat(log, containsString("The ruleset rulesets/dummy/basic.xml is referenced multiple times in ruleset 'Custom Rules'"));
    }

}
