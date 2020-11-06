/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.util.ResourceLoader;

public class PMD5RulesetTest {

    @Test
    public void loadRuleset() throws Exception {
        RuleSetFactory ruleSetFactory = new RuleSetFactory(new ResourceLoader(), RulePriority.LOW, true, true);
        RuleSet ruleset = ruleSetFactory.createRuleSet("net/sourceforge/pmd/lang/java/pmd5ruleset.xml");
        Assert.assertNotNull(ruleset);
        Assert.assertNull(ruleset.getRuleByName("GuardLogStatementJavaUtil"));
        Assert.assertNull(ruleset.getRuleByName("GuardLogStatement"));
    }
}
