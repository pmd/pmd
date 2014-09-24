/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.loggingjakartacommons;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class LoggingJakartaCommonsRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "java-logging-jakarta-commons";

    @Override
    public void setUp() {
        addRule(RULESET, "ProperLogger");
        addRule(RULESET, "UseCorrectExceptionLogging");
        addRule(RULESET, "GuardDebugLogging");
        addRule(RULESET, "GuardLogStatement");
    }
}
