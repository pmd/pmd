/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

import org.junit.jupiter.api.Test;

import com.github.stefanbirkner.systemlambda.SystemLambda;

class RuleSetFactoryMessagesTest extends RulesetFactoryTestBase {

    @Test
    void testFullMessage() throws Exception {
        String log = SystemLambda.tapSystemErr(() -> assertCannotParse(
            rulesetXml(
                dummyRule(
                    priority("not a priority")
                )
            )
        ));

        assertThat(log, containsString(
            "Error at dummyRuleset.xml:9:1\n"
                + " 7| \n"
                + " 8| <rule name=\"MockRuleName\" language=\"dummy\" class=\"net.sourceforge.pmd.lang.rule.MockRule\" message=\"avoid the mock rule\">\n"
                + " 9| <priority>not a priority</priority></rule></ruleset>\n"
                + "    ^^^^^^^^^ Not a valid priority: 'not a priority', expected a number in [1,5]"
        ));
    }
}
