/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemErrRule;

import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetLoader;

public class QuickstartRulesetTest {

    @Rule
    public final SystemErrRule systemErrRule = new SystemErrRule().enableLog().muteForSuccessfulTests();

    @Test
    public void noDeprecations() {
        RuleSetLoader ruleSetLoader = new RuleSetLoader().enableCompatibility(false);
        RuleSet quickstart = ruleSetLoader.loadFromResource("rulesets/java/quickstart.xml");
        Assert.assertFalse(quickstart.getRules().isEmpty());
        Assert.assertTrue(systemErrRule.getLog().isEmpty());
    }
}
