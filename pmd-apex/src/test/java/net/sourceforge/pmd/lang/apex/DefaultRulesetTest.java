/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.AbstractRuleSetFactoryTest;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetLoader;

import com.github.stefanbirkner.systemlambda.SystemLambda;

class DefaultRulesetTest {
    private static final String QUICKSTART_RULESET = "rulesets/apex/quickstart.xml";

    @Test
    void loadDefaultRuleset() {
        RuleSet ruleset = rulesetLoader().loadFromResource("rulesets/apex/ruleset.xml");
        assertNotNull(ruleset);
    }

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
