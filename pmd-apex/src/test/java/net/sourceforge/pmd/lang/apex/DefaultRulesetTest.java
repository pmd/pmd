/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemErrRule;

import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetLoader;

public class DefaultRulesetTest {
    @Rule
    public final SystemErrRule systemErrRule = new SystemErrRule().enableLog().muteForSuccessfulTests();

    @Test
    public void loadDefaultRuleset() {
        RuleSet ruleset = rulesetLoader().loadFromResource("rulesets/apex/ruleset.xml");
        Assert.assertNotNull(ruleset);
    }

    @Test
    public void loadQuickstartRuleset() {
        RuleSet ruleset = rulesetLoader().loadFromResource("rulesets/apex/quickstart.xml");
        Assert.assertNotNull(ruleset);
        Assert.assertTrue("No Logging expected", systemErrRule.getLog().isEmpty());
    }

    private RuleSetLoader rulesetLoader() {
        return new RuleSetLoader().enableCompatibility(false);
    }
}
