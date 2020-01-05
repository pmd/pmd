/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.testframework;

import java.util.Collections;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.Filterable;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.manipulation.Sortable;
import org.junit.runner.manipulation.Sorter;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.JUnit4;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;

/**
 * A JUnit Runner, that combines the default {@link JUnit4}
 * and our custom {@link RuleTestRunner}.
 * It allows to selectively execute single test cases (it is {@link Filterable}).
 *
 * <p>Note: Since we actually run two runners one after another, the static {@code BeforeClass}
 * and {@Code AfterClass} methods will be executed twice and the test class will be instantiated twice, too.</p>
 *
 * <p>In order to use it, you'll need to subclass {@link SimpleAggregatorTst} and
 * annotate your test class with RunWith:</p>
 *
 * <pre>
 * &#64;RunWith(PMDTestRunner.class)
 * public class MyRuleSetTest extends SimpleAggregatorTst {
 * ...
 * }
 * </pre>
 */
public class PMDTestRunner extends Runner implements Filterable, Sortable {
    private final Class<? extends SimpleAggregatorTst> klass;
    private final RuleTestRunner ruleTests;
    private final ParentRunner<?> unitTests;

    public PMDTestRunner(final Class<? extends SimpleAggregatorTst> klass) throws InitializationError {
        this.klass = klass;
        ruleTests = new RuleTestRunner(klass);

        if (ruleTests.hasUnitTests()) {
            unitTests = new JUnit4(klass);
        } else {
            unitTests = new EmptyRunner(klass);
        }
    }

    @Override
    public void filter(Filter filter) throws NoTestsRemainException {
        boolean noRuleTests = false;
        try {
            ruleTests.filter(filter);
        } catch (NoTestsRemainException e) {
            noRuleTests = true;
        }

        boolean noUnitTests = false;
        try {
            unitTests.filter(filter);
        } catch (NoTestsRemainException e) {
            noUnitTests = true;
        }

        if (noRuleTests && noUnitTests) {
            throw new NoTestsRemainException();
        }
    }

    @Override
    public Description getDescription() {
        Description description = Description.createSuiteDescription(klass);
        description.addChild(createChildrenDescriptions(ruleTests, "Rule Tests"));
        if (ruleTests.hasUnitTests()) {
            description.addChild(createChildrenDescriptions(unitTests, "Unit Tests"));
        }
        return description;
    }

    private Description createChildrenDescriptions(Runner runner, String suiteName) {
        Description suite = Description.createSuiteDescription(suiteName);
        for (Description child : runner.getDescription().getChildren()) {
            suite.addChild(child);
        }
        return suite;
    }

    @Override
    public void run(RunNotifier notifier) {
        ruleTests.run(notifier);
        unitTests.run(notifier);
    }

    @Override
    public void sort(Sorter sorter) {
        ruleTests.sort(sorter);
        unitTests.sort(sorter);
    }

    private static class EmptyRunner extends ParentRunner<Object> {
        protected EmptyRunner(Class<?> testClass) throws InitializationError {
            super(testClass);
        }

        @Override
        public Description getDescription() {
            return Description.EMPTY;
        }

        @Override
        protected List<Object> getChildren() {
            return Collections.emptyList();
        }

        @Override
        protected Description describeChild(Object child) {
            return Description.EMPTY;
        }

        @Override
        protected void runChild(Object child, RunNotifier notifier) {
            // there are no tests - nothing to execute
        }
    }
}
