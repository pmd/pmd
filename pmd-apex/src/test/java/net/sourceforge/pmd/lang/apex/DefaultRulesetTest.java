/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetLoader;

import com.github.stefanbirkner.systemlambda.SystemLambda;

class DefaultRulesetTest {

    @Test
    void loadDefaultRuleset() {
        RuleSet ruleset = rulesetLoader().loadFromResource("rulesets/apex/ruleset.xml");
        assertNotNull(ruleset);
    }

    @Test
    void loadQuickstartRuleset() throws Exception {
        String log = SystemLambda.tapSystemErr(() -> {
            RuleSet ruleset = rulesetLoader().loadFromResource("rulesets/apex/quickstart.xml");
            assertNotNull(ruleset);
        });
        assertTrue(log.isEmpty(), "No Logging expected");
    }

    private RuleSetLoader rulesetLoader() {
        return new RuleSetLoader().enableCompatibility(false);
    }
}
