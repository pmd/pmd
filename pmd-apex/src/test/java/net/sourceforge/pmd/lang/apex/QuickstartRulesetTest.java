/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.test.RuleSetAssertions;

class QuickstartRulesetTest {
    private static final String QUICKSTART_RULESET = "rulesets/apex/quickstart.xml";

    @Test
    void loadQuickstartRuleset() throws Exception {
        RuleSetAssertions.assertNoWarnings(QUICKSTART_RULESET);
    }
}
