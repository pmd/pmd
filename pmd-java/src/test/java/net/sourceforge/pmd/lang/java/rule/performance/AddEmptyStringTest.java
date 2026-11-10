/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.performance;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.rule.Rule;
import net.sourceforge.pmd.lang.rule.RuleSetLoader;
import net.sourceforge.pmd.test.PmdRuleTst;

class AddEmptyStringTest extends PmdRuleTst {

    @Test
    void explicitReferenceStillLoadsDeprecatedRule() {
        Rule rule = new RuleSetLoader().warnDeprecated(false)
            .loadFromResource("category/java/performance.xml/AddEmptyString")
            .getRuleByName("AddEmptyString");

        assertNotNull(rule);
        assertTrue(rule.isDeprecated());
    }
}
