/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.testframework;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.Rule;

import org.junit.runner.RunWith;

/**
 * Standard methods for (simple) testcases.
 */
@RunWith(PMDTestRunner.class)
public abstract class SimpleAggregatorTst extends RuleTst {

    /**
     * Configure the rule tests to be executed. Implement this method in
     * subclasses by calling adRule.
     * 
     * @see #addRule(String, String)
     */
    protected void setUp() {
        // empty, to be overridden
    }

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
     * Gets all configured rules.
     * 
     * @return all configured rules.
     */
    protected List<Rule> getRules() {
        return rules;
    }
}
