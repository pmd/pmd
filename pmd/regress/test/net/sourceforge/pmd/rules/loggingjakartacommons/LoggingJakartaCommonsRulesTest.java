package test.net.sourceforge.pmd.rules.loggingjakartacommons;

import org.junit.Before;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class LoggingJakartaCommonsRulesTest extends SimpleAggregatorTst {

    @Before
    public void setUp() {
        addRule("logging-jakarta-commons", "ProperLogger");
        addRule("logging-jakarta-commons", "UseCorrectExceptionLogging");
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(LoggingJakartaCommonsRulesTest.class);
    }
}
