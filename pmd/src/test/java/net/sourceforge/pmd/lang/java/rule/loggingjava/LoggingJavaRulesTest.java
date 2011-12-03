package net.sourceforge.pmd.lang.java.rule.loggingjava;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

import org.junit.Before;


public class LoggingJavaRulesTest extends SimpleAggregatorTst {
    
    private static final String RULESET = "java-logging-java";

    @Before
    public void setUp() {
        addRule(RULESET, "AvoidPrintStackTrace");
        addRule(RULESET, "LoggerIsNotStaticFinal");
        addRule(RULESET, "MoreThanOneLogger");
        addRule(RULESET, "SystemPrintln");
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(LoggingJavaRulesTest.class);
    }
}
