/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.testframework;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.runners.statements.RunAfters;
import org.junit.internal.runners.statements.RunBefores;
import org.junit.rules.RunRules;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

import net.sourceforge.pmd.Rule;

/**
 * A JUnit Runner, that executes all declared rule tests in the class.
 * It supports Before and After methods as well as TestRules.
 *
 * @author Andreas Dangel
 */
public class RuleTestRunner extends ParentRunner<TestDescriptor> {
    private ConcurrentHashMap<TestDescriptor, Description> testDescriptions = new ConcurrentHashMap<>();
    private final RuleTst instance;

    public RuleTestRunner(Class<? extends RuleTst> testClass) throws InitializationError {
        super(testClass);
        instance = createTestClass();
        instance.setUp();
    }

    @Override
    protected Description describeChild(TestDescriptor testCase) {
        Description description = testDescriptions.get(testCase);
        if (description == null) {
            description = Description.createTestDescription(getTestClass().getJavaClass(),
                testCase.getRule().getName() + "::"
                        + testCase.getNumberInDocument() + " "
                        + testCase.getDescription().replaceAll("\n|\r", " "));
            testDescriptions.putIfAbsent(testCase, description);
        }
        return description;
    }

    /**
     * Checks whether this test class has additionally unit test methods.
     * @return true if there is at least one unit test method.
     */
    public boolean hasUnitTests() {
        return !getTestClass().getAnnotatedMethods(Test.class).isEmpty();
    }

    @Override
    protected List<TestDescriptor> getChildren() {
        List<Rule> rules = new ArrayList<>(instance.getRules());
        Collections.sort(rules, new Comparator<Rule>() {
            @Override
            public int compare(Rule o1, Rule o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        List<TestDescriptor> tests = new LinkedList<>();
        for (Rule r : rules) {
            TestDescriptor[] ruleTests = instance.extractTestsFromXml(r);
            for (TestDescriptor t : ruleTests) {
                tests.add(t);
            }
        }

        return tests;
    }

    private RuleTst createTestClass() throws InitializationError {
        try {
            return (RuleTst) getTestClass().getOnlyConstructor().newInstance();
        } catch (Exception e) {
            throw new InitializationError(e);
        }
    }

    @Override
    protected void runChild(TestDescriptor testCase, RunNotifier notifier) {
        Description description = describeChild(testCase);
        if (isIgnored(testCase)) {
            notifier.fireTestIgnored(description);
        } else {
            runLeaf(ruleTestBlock(testCase), description, notifier);
        }
    }

    /**
     * Executes the actual test case. If there are Before, After, or TestRules present,
     * they are executed accordingly.
     *
     * @param testCase the PMD rule test case to be executed
     * @return a single statement which includes any rules, if present.
     */
    private Statement ruleTestBlock(final TestDescriptor testCase) {
        Statement statement = new Statement() {
            @Override
            public void evaluate() throws Throwable {
                instance.runTest(testCase);
            }
        };
        statement = withBefores(statement);
        statement = withAfters(statement);
        statement = withRules(testCase, statement);
        return statement;
    }

    private Statement withBefores(Statement statement) {
        List<FrameworkMethod> befores = getTestClass().getAnnotatedMethods(Before.class);
        return befores.isEmpty() ? statement : new RunBefores(statement, befores, instance);
    }

    private Statement withAfters(Statement statement) {
        List<FrameworkMethod> afters = getTestClass().getAnnotatedMethods(After.class);
        return afters.isEmpty() ? statement : new RunAfters(statement, afters, instance);
    }

    private Statement withRules(final TestDescriptor testCase, Statement statement) {
        List<TestRule> testRules = getTestClass().getAnnotatedFieldValues(instance, org.junit.Rule.class, TestRule.class);
        return testRules.isEmpty() ? statement : new RunRules(statement, testRules, describeChild(testCase));
    }

    @Override
    protected boolean isIgnored(TestDescriptor child) {
        return TestDescriptor.inRegressionTestMode() && !child.isRegressionTest();
    }
}
