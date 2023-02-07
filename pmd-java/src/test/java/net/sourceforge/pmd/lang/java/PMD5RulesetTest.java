/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetLoader;

class PMD5RulesetTest {

    @Test
    void loadRuleset() {
        RuleSet ruleset = new RuleSetLoader().loadFromResource("net/sourceforge/pmd/lang/java/pmd5ruleset.xml");
        assertNotNull(ruleset);
        assertNull(ruleset.getRuleByName("GuardLogStatementJavaUtil"));
        assertNull(ruleset.getRuleByName("GuardLogStatement"));
    }
}
