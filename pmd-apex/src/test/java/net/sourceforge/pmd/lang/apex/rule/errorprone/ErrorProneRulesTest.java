/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.errorprone;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public abstract class ErrorProneRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "category/apex/errorprone.xml";

    @Override
    public void setUp() {
        addRule(RULESET, getClass().getSimpleName().replaceFirst("Test$", ""));
    }
}
