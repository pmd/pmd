/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.loggingjava;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class LoggingJavaRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "java-logging-java";

    @Override
    public void setUp() {
        addRule(RULESET, "AvoidPrintStackTrace");
        addRule(RULESET, "LoggerIsNotStaticFinal");
        addRule(RULESET, "MoreThanOneLogger");
        addRule(RULESET, "SystemPrintln");
        addRule(RULESET, "GuardLogStatementJavaUtil");
    }
}
