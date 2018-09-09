/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.multithreading;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

/**
 * Rule tests for the multithreading category
 */
public abstract class MultithreadingRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "category/java/multithreading.xml";

    @Override
    public void setUp() {
        addRule(RULESET, getClass().getSimpleName().replaceFirst("Test$", ""));
    }
}
