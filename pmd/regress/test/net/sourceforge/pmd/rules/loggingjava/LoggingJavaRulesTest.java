package test.net.sourceforge.pmd.rules.loggingjava;

import org.junit.Before;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class LoggingJavaRulesTest extends SimpleAggregatorTst {

    @Before
    public void setUp() {
        addRule("logging-java", "AvoidPrintStackTrace");
        addRule("logging-java", "LoggerIsNotStaticFinal");
        addRule("logging-java", "MoreThanOneLogger");
        addRule("logging-java", "SystemPrintln");
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(LoggingJavaRulesTest.class);
    }
}
