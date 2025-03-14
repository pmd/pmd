/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.rule.RuleSet;
import net.sourceforge.pmd.lang.rule.RuleSetLoader;
import net.sourceforge.pmd.test.lang.rule.AbstractRuleSetFactoryTest;

import com.github.stefanbirkner.systemlambda.SystemLambda;

class QuickstartRulesetTest {
    private static final String QUICKSTART_RULESET = "rulesets/apex/quickstart.xml";

    @Test
    void loadQuickstartRuleset() throws Exception {
        String log = SystemLambda.tapSystemErr(() -> {
            RuleSet ruleset = rulesetLoader().loadFromResource(QUICKSTART_RULESET);
            assertNotNull(ruleset);
        });
        assertTrue(log.isEmpty(), "No Logging expected");
    }

    @Test
    void correctEncoding() throws Exception {
        assertTrue(AbstractRuleSetFactoryTest.hasCorrectEncoding(QUICKSTART_RULESET));
    }

    private RuleSetLoader rulesetLoader() {
        return new RuleSetLoader();
    }
}
