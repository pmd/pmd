/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.slf4j.event.Level;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.lang.rule.RuleSet;
import net.sourceforge.pmd.lang.rule.RuleSetLoader;
import net.sourceforge.pmd.util.log.internal.MessageReporterBase;

public final class RuleSetAssertions {
    private RuleSetAssertions() {
    }

    public static void assertNoWarnings(String rulesetFilename) {
        class Reporter extends MessageReporterBase {
            private int warnings = 0;

            Reporter() {
                setLevel(Level.WARN);
            }

            @Override
            protected void logImpl(Level level, String message) {
                if (level == Level.WARN) {
                    warnings++;
                }
                System.out.println(message);
            }

            public int numWarnings() {
                return warnings;
            }
        }

        Reporter reporter = new Reporter();
        PMDConfiguration pmdConfig = new PMDConfiguration();
        pmdConfig.setReporter(reporter);
        RuleSetLoader ruleSetLoader = RuleSetLoader.fromPmdConfig(pmdConfig).warnDeprecated(true);

        RuleSet ruleSet = ruleSetLoader.loadFromResource(rulesetFilename);
        assertAll(
                () -> assertEquals(0, reporter.numErrors(), "errors while loading ruleset " + rulesetFilename),
                () -> assertEquals(0, reporter.numWarnings(), "warnings while loading ruleset " + rulesetFilename),
                () -> assertFalse(ruleSet.getRules().isEmpty(), "ruleset " + rulesetFilename + " was empty")
        );
    }
}
