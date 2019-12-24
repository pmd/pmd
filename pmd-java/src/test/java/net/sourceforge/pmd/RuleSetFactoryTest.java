/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

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
                        + "    xsi:schemaLocation=\"http://pmd.sourceforge.net/ruleset/2.0.0 https://pmd.sourceforge.io/ruleset_2_0_0.xsd\">\n"
                        + "  <description>Custom ruleset for tests</description>\n"
                        + "  <rule ref=\"category/java/codestyle.xml\">\n"
                        + "    <exclude name=\"UselessParentheses\"/>\n" + "  </rule>\n" + "</ruleset>\n");
        RuleSetFactory ruleSetFactory = RulesetsFactoryUtils.defaultFactory();
        RuleSet ruleset = ruleSetFactory.createRuleSet(ref);
        Rule rule = ruleset.getRuleByName("UselessParentheses");
        assertNull(rule);
    }
}
