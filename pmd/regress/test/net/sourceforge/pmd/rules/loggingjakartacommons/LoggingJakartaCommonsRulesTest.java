package test.net.sourceforge.pmd.rules.loggingjakartacommons;

import net.sourceforge.pmd.Rule;

import org.junit.Before;
import org.junit.Test;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

import java.util.List;
import java.util.ArrayList;

public class LoggingJakartaCommonsRulesTest extends SimpleAggregatorTst {
    private List<Rule> rules = new ArrayList<Rule>();

    @Before
    public void setUp() {
        rules.add(findRule("logging-jakarta-commons", "ProperLogger"));
        rules.add(findRule("logging-jakarta-commons", "UseCorrectExceptionLogging"));
    }

    @Test
    public void testAll() {
        for (Rule r : rules) {
            runTests(r);
        }
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(LoggingJakartaCommonsRulesTest.class);
    }
}
