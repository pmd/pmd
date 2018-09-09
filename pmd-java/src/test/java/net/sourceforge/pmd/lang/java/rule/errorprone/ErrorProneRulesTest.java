/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

/**
 * Rule tests for the error prone category
 */
public abstract class ErrorProneRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "category/java/errorprone.xml";

    // missing rule: AvoidAccessibilityAlteration

    @Override
    public void setUp() {
        addRule(RULESET, getClass().getSimpleName().replaceFirst("Test$", ""));
    }
}
