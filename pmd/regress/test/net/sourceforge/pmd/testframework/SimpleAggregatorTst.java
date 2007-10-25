/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.testframework;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.Rule;

import org.junit.Test;
import org.junit.internal.runners.TestClassMethodsRunner;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

/**
 * Standard methods for (simple) testcases.
 */
@RunWith(SimpleAggregatorTst.CustomXmlTestClassMethodsRunner.class)
public abstract class SimpleAggregatorTst extends RuleTst {
    /**
     * Run a set of tests defined in an XML test-data file for a rule. The file
     * should be ./xml/RuleName.xml relative to the test-class. The format is
     * defined in test-data.xsd.
     */
    public void runTests(Rule rule) {
        runTests(extractTestsFromXml(rule));
    }

    /**
     * Run a set of tests defined in a XML test-data file. The file should be
     * ./xml/[testsFileName].xml relative to the test-class. The format is
     * defined in test-data.xsd.
     */
    public void runTests(Rule rule, String testsFileName) {
        runTests(extractTestsFromXml(rule, testsFileName));
    }
    
    /**
     * Run a set of tests of a certain sourceType.
     */
    public void runTests(TestDescriptor[] tests) {
        for (int i = 0; i < tests.length; i++) {
            runTest(tests[i]);
        }
    }

    private List<Rule> rules = new ArrayList<Rule>();

    /**
     * Add new XML tests associated with the rule to the test suite. This should
     * be called from the setup method.
     */
    protected void addRule(String ruleSet, String ruleName) {
        rules.add(findRule(ruleSet, ruleName));
    }

    /**
     * Run a set of tests for all rules added in the setup method.
     */
    @Test
    public void testAll() {
        ArrayList<Failure> l = new ArrayList<Failure>();
        for (Rule r : rules) {
            TestDescriptor[] tests = extractTestsFromXml(r);
            for (int i = 0; i < tests.length; i++) {
                try {
                    runTest(tests[i]);
                } catch (Throwable t) {
                    Failure f = CustomXmlTestClassMethodsRunner.createFailure(r, t);
                    l.add(f);
                }
            }
        }
        for(Failure f: l) {
            CustomXmlTestClassMethodsRunner.addFailure(f);
        }
    }

    public static class CustomXmlTestClassMethodsRunner extends TestClassMethodsRunner {
        public CustomXmlTestClassMethodsRunner(Class<?> klass) {
            super(klass);
        }

        public static Failure createFailure(Rule rule, Throwable targetException) {
            return new Failure(Description.createTestDescription(
                    SimpleAggregatorTst.class, "xml." + rule.getRuleSetName() + '.' + rule.getName()),
                    targetException);
        }

        public static void addFailure(Failure failure) {
            NOTIFIER.fireTestFailure(failure);
        }

        @Override
        public synchronized void run(RunNotifier n) {
            // synchronized so that access to NOTIFIER is safe
            NOTIFIER = n;
            super.run(n);
        }

        private static RunNotifier NOTIFIER;
    }
}
