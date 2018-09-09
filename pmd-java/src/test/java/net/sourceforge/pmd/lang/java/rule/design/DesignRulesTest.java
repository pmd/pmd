/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.lang.java.metrics.MetricsHook;
import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

/**
 * Tests all the rules, that are in the design category.
 */
public abstract class DesignRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "category/java/design.xml";

    @Override
    protected Rule reinitializeRule(Rule rule) {
        MetricsHook.reset();
        return rule;
    }

    // missing rule: PositionalIteratorRule
    // missing rule: TooManyHttpFilter

    @Override
    public void setUp() {
        addRule(RULESET, getClass().getSimpleName().replaceFirst("Test$", ""));
    }
}
