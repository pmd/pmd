/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.testframework;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.pmd.Rule;

import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.Filterable;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.TestClass;

/**
 * A test runner for rule tests. Unlike {@link SimpleAggregatorTst.CustomXmlTestClassMethodsRunner}
 * it also reports the successful executed tests and allows to selectively execute single test cases
 * (it is {@link Filterable}).
 * <p>
 * In order to use it, you'll need to subclass {@link SimpleAggregatorTst} and annotate your test
 * class with RunWith:
 * <pre>
 * {@code @}RunWith(PMDTestRunner.class)
 * public class MyRuleSetTest extends SimpleAggregatorTst {
 * ...
 * }
 * </pre>
 * </p>
 */
public class PMDTestRunner extends Runner implements Filterable {
    private final Description desc;
    private final Class<? extends SimpleAggregatorTst> klass;
    private final List<TestDescriptor> allTests = new ArrayList<TestDescriptor>();
    private BlockJUnit4ClassRunner chainedRunner;

    /**
     * Creates a new {@link PMDTestRunner} for the given test class.
     * @param klass the test class that is under test
     * @throws InitializationError any error
     */
    public PMDTestRunner(final Class<? extends SimpleAggregatorTst> klass) throws InitializationError {
        this.klass = klass;

        desc = Description.createSuiteDescription(klass);
        configureRuleTests();
        configureUnitTests();
    }

    private void configureRuleTests() throws InitializationError {
        Description root = Description.createSuiteDescription("Rule Tests");
        try {
            SimpleAggregatorTst test = createTestClass();
            test.setUp();

            List<Rule> rules = new ArrayList<Rule>(test.getRules());
            Collections.sort(rules, new Comparator<Rule>() {
                @Override
                public int compare(Rule o1, Rule o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });

            for (Rule r : rules) {
                Description ruleDescription = Description.createSuiteDescription(r.getName());
                root.addChild(ruleDescription);
                
                TestDescriptor[] ruleTests = test.extractTestsFromXml(r);
                for (TestDescriptor t : ruleTests) {
                    Description d = createTestDescription(t);
                    ruleDescription.addChild(d);
                    allTests.add(t);
                }
            }
            if (!root.getChildren().isEmpty()) {
                desc.addChild(root);
            }
        } catch (Exception e) {
            throw new InitializationError(e);
        }
    }

    private SimpleAggregatorTst createTestClass() {
        try {
            return klass.getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void configureUnitTests() throws InitializationError {
        TestClass tclass = new TestClass(klass);
        if (!tclass.getAnnotatedMethods(Test.class).isEmpty()) {
            Description unitTests = Description.createSuiteDescription("Unit tests");
            chainedRunner = new BlockJUnit4ClassRunner(klass);
            for (Description d : chainedRunner.getDescription().getChildren()) {
                unitTests.addChild(d);
            }
            desc.addChild(unitTests);
        }
    }

    @Override
    public Description getDescription() {
        return desc;
    }

    @Override
    public void run(RunNotifier notifier) {
        SimpleAggregatorTst test = createTestClass();
        boolean regressionTestMode = TestDescriptor.inRegressionTestMode();

        for (TestDescriptor t : allTests) {
            Description d = createTestDescription(t);
            notifier.fireTestStarted(d);
            try {
                if (!regressionTestMode || t.isRegressionTest()) {
                    test.runTest(t);
                } else {
                    notifier.fireTestIgnored(d);
                }
            } catch (Throwable e) {
                notifier.fireTestFailure(new Failure(d, e));
            } finally {
                notifier.fireTestFinished(d);
            }
        }
        if (chainedRunner != null) {
            chainedRunner.run(notifier);
        }
    }

    private Description createTestDescription(TestDescriptor t) {
        String d = t.getDescription().replaceAll("\n|\r", " ");
        return Description.createTestDescription(klass, t.getRule().getName() + "::" + t.getNumberInDocument() + " " + d);
    }

    @Override
    public void filter(Filter filter) throws NoTestsRemainException {
        Iterator<TestDescriptor> it = allTests.iterator();
        while (it.hasNext()) {
            TestDescriptor t = it.next();
            Description testDesc = createTestDescription(t);
            if (filter.shouldRun(testDesc)) {
                try {
                    filter.apply(t);
                } catch (NoTestsRemainException e) {
                    it.remove();
                }
            } else {
                it.remove();
            }
        }

        boolean chainIsEmpty = false;
        try {
            if (chainedRunner != null) {
                chainedRunner.filter(filter);
            } else {
                chainIsEmpty = true;
            }
        } catch (NoTestsRemainException e) {
            chainIsEmpty = true;
        }

        if (allTests.isEmpty() && chainIsEmpty) {
            throw new NoTestsRemainException();
        }
    }
}
