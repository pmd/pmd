/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetLoader;

import com.github.stefanbirkner.systemlambda.SystemLambda;

class QuickstartRulesetTest {

    @Test
    void noDeprecations() throws Exception {
        RuleSetLoader ruleSetLoader = new RuleSetLoader().enableCompatibility(false);
        String errorOutput = SystemLambda.tapSystemErr(() -> {
            RuleSet quickstart = ruleSetLoader.loadFromResource("rulesets/java/quickstart.xml");
            assertFalse(quickstart.getRules().isEmpty());
        });
        assertTrue(errorOutput.isEmpty());
    }
}
