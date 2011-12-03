package net.sourceforge.pmd.lang.java.rule.loggingjakartacommons;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

import org.junit.Before;


public class LoggingJakartaCommonsRulesTest extends SimpleAggregatorTst {
    
    private static final String RULESET = "java-logging-jakarta-commons";

    @Before
    public void setUp() {
        addRule(RULESET, "ProperLogger");
        addRule(RULESET, "UseCorrectExceptionLogging");
        addRule(RULESET, "GuardDebugLogging");
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(LoggingJakartaCommonsRulesTest.class);
    }
}
