/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.test.lang.rule.AbstractRuleSetFactoryTest;

/**
 * Test java's rulesets
 */
class RuleSetFactoryTest extends AbstractRuleSetFactoryTest {
    RuleSetFactoryTest() {
        super(getExpectedMessages());
    }

    private static Map<String, Set<String>> getExpectedMessages() {
        Map<String, Set<String>> messages = new HashMap<>();
        Set<String> designMessages = new HashSet<>();
        // AvoidCatchingGenericException has been moved from Design to Error Prone with PMD 7.18.0
        designMessages.add("Warning at category/java/design.xml:50:5\n"
                + " 48|     </rule>\n"
                + " 49| \n"
                + " 50|     <rule name=\"AvoidCatchingGenericException\" ref=\"category/java/errorprone.xml/AvoidCatchingGenericException\" deprecated=\"true\" />\n"
                + "         ^^^^^ Use Rule name category/java/errorprone.xml/AvoidCatchingGenericException instead of the deprecated Rule name category/java/design.xml/AvoidCatchingGenericException. PMD 8.0.0 will remove support for this deprecated Rule name usage.\n"
                + "\n"
                + " 51| \n"
                + " 52|     <rule name=\"AvoidDeeplyNestedIfStmts\"\n");
        messages.put("category/java/design.xml", designMessages);
        return messages;
    }

}
