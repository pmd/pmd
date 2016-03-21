/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.jsp.rule.basicjsf;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class BasicJsfRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "jsp-basic-jsf";

    @Override
    public void setUp() {
        addRule(RULESET, "DontNestJsfInJstlIteration");
    }
}
