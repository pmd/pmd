/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 * Test java's rulesets
 */
public class RuleSetFactoryTest extends AbstractRuleSetFactoryTest {

    @Test
    public void testExclusionOfUselessParantheses() throws RuleSetNotFoundException {
        RuleSetReferenceId ref = createRuleSetReferenceId(
                "<?xml version=\"1.0\"?>\n" + "<ruleset name=\"Custom ruleset for tests\"\n"
                        + "    xmlns=\"http://pmd.sourceforge.net/ruleset/2.0.0\"\n"
                        + "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                        + "    xsi:schemaLocation=\"http://pmd.sourceforge.net/ruleset/2.0.0 http://pmd.sourceforge.net/ruleset_2_0_0.xsd\">\n"
                        + "  <description>Custom ruleset for tests</description>\n"
                        + "  <rule ref=\"rulesets/java/unnecessary.xml\">\n"
                        + "    <exclude name=\"UselessParentheses\"/>\n" + "  </rule>\n" + "</ruleset>\n");
        RuleSetFactory ruleSetFactory = new RuleSetFactory();
        RuleSet ruleset = ruleSetFactory.createRuleSet(ref);
        Rule rule = ruleset.getRuleByName("UselessParentheses");
        assertNull(rule);
    }

    /**
     * Makes sure that the internal dogfood.xml ruleset is valid and doesn't
     * reference any unknown rules.
     * 
     * @throws RuleSetNotFoundException
     *             if dogfood couldn't be found at all
     */
    @Test
    public void testDogfoodRuleset() throws RuleSetNotFoundException {
        RuleSetReferenceId ref = RuleSetReferenceId.parse("rulesets/internal/dogfood.xml").get(0);
        RuleSetFactory ruleSetFactory = new RuleSetFactory();
        RuleSet ruleset = ruleSetFactory.createRuleSet(ref);
        assertNotNull(ruleset);
    }
}
