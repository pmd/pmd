package test.net.sourceforge.pmd.rules.loggingjava;

import net.sourceforge.pmd.Rule;

import org.junit.Before;
import org.junit.Test;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

import java.util.List;
import java.util.ArrayList;

public class LoggingJavaRulesTest extends SimpleAggregatorTst {
    private List<Rule> rules = new ArrayList<Rule>();

    @Before
    public void setUp() {
        rules.add(findRule("logging-java", "AvoidPrintStackTrace"));
        rules.add(findRule("logging-java", "LoggerIsNotStaticFinal"));
        rules.add(findRule("logging-java", "MoreThanOneLogger"));
        rules.add(findRule("logging-java", "SystemPrintln"));
    }

    @Test
    public void testAll() {
        for (Rule r : rules) {
            runTests(r);
        }
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(LoggingJavaRulesTest.class);
    }
}
