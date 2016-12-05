/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;

public class DefaultRulesetTest {

    @Test
    public void loadDefaultRuleset() throws Exception {
        RuleSetFactory factory = new RuleSetFactory();
        RuleSet ruleset = factory.createRuleSet("rulesets/apex/ruleset.xml");
        assertNotNull(ruleset);
    }
}
