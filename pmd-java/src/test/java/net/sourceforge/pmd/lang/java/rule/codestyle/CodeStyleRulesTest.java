/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

/**
 * Rule tests for the code style category
 */
public abstract class CodeStyleRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "category/java/codestyle.xml";

    @Override
    public void setUp() {
        addRule(RULESET, getClass().getSimpleName().replaceFirst("Test$", ""));
    }
}
