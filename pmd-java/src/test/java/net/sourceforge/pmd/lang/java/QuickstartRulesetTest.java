/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemErrRule;

import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.util.ResourceLoader;

public class QuickstartRulesetTest {

    @Rule
    public final SystemErrRule systemErrRule = new SystemErrRule().enableLog();

    @Test
    public void noDeprecations() throws RuleSetNotFoundException {
        RuleSetFactory ruleSetFactory = new RuleSetFactory(new ResourceLoader(), RulePriority.LOW, true, false);
        RuleSet quickstart = ruleSetFactory.createRuleSet("rulesets/java/quickstart.xml");
        Assert.assertFalse(quickstart.getRules().isEmpty());

        Assert.assertTrue(systemErrRule.getLog().isEmpty());
    }
}
