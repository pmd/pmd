/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java;

import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.AbstractRuleSetFactoryTest;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetLoader;

/**
 * Test java's rulesets
 */
class RuleSetFactoryTest extends AbstractRuleSetFactoryTest {

    @Test
    void testExclusionOfUselessParantheses() {
        RuleSet ruleset = new RuleSetLoader().loadFromString("",
                                                             "<?xml version=\"1.0\"?>\n" + "<ruleset name=\"Custom ruleset for tests\"\n"
                        + "    xmlns=\"http://pmd.sourceforge.net/ruleset/2.0.0\"\n"
                        + "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                        + "    xsi:schemaLocation=\"http://pmd.sourceforge.net/ruleset/2.0.0 https://pmd.sourceforge.io/ruleset_2_0_0.xsd\">\n"
                        + "  <description>Custom ruleset for tests</description>\n"
                        + "  <rule ref=\"category/java/codestyle.xml\">\n"
                        + "    <exclude name=\"UselessParentheses\"/>\n" + "  </rule>\n" + "</ruleset>\n");
        Rule rule = ruleset.getRuleByName("UselessParentheses");
        assertNull(rule);
    }
}
