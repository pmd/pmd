/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.opentest4j.MultipleFailuresError;

class RuleSetAssertionsTest {
    @Test
    void canDetectDeprecatedRuleReferences() {
        MultipleFailuresError error = assertThrows(MultipleFailuresError.class, () -> RuleSetAssertions.assertNoWarnings("net/sourceforge/pmd/test/ruleset-with-deprecated-rule-ref.xml"));
        assertEquals(1, error.getFailures().size());
        assertTrue(error.getFailures().get(0).getMessage().startsWith("warnings while loading"));
    }

    @Test
    void rulesetWithoutWarning() {
        RuleSetAssertions.assertNoWarnings("net/sourceforge/pmd/test/ruleset-without-warnings.xml");
    }
}
