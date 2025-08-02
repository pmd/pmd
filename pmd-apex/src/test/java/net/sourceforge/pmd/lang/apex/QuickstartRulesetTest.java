/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.test.RuleSetAssertions;
import net.sourceforge.pmd.test.lang.rule.AbstractRuleSetFactoryTest;

class QuickstartRulesetTest {
    private static final String QUICKSTART_RULESET = "rulesets/apex/quickstart.xml";

    @Test
    void loadQuickstartRuleset() throws Exception {
        RuleSetAssertions.assertNoWarnings(QUICKSTART_RULESET);
    }

    @Test
    void correctEncoding() throws Exception {
        assertTrue(AbstractRuleSetFactoryTest.hasCorrectEncoding(QUICKSTART_RULESET));
    }
}
