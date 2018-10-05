/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.testframework;

import java.util.ArrayList;
import java.util.List;

import org.junit.runner.RunWith;

import net.sourceforge.pmd.Rule;

/**
 * Simple setup for a rule unit test,
 * capable of testing multiple rules.
 *
 * <p>Override {@link #setUp()} to register the
 * rules, that should be tested via calls to
 * {@link #addRule(String, String)}.
 */
@RunWith(PMDTestRunner.class)
public abstract class SimpleAggregatorTst extends RuleTst {

    private List<Rule> rules = new ArrayList<>();

    /**
     * Configure the rule tests to be executed. Override this method in
     * subclasses by calling addRule, e.g.
     *
     * <pre>addRule("path/myruleset.xml", "CustomRule");</pre>
     *
     * @see #addRule(String, String)
     */
    @Override
    protected void setUp() {
        // empty, to be overridden.
    }

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
    @Override
    protected List<Rule> getRules() {
        return rules;
    }
}
