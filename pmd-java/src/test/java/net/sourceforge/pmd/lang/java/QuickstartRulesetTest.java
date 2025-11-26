/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.test.RuleSetAssertions;

class QuickstartRulesetTest {
    private static final String QUICKSTART_RULESET = "rulesets/java/quickstart.xml";

    @Test
    void noDeprecations() {
        RuleSetAssertions.assertNoWarnings(QUICKSTART_RULESET);
    }
}
