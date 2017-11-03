/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.jsp.rule.bestpractices;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class BestPracticesRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "category/jsp/bestpractices.xml";

    @Override
    public void setUp() {
        addRule(RULESET, "DontNestJsfInJstlIteration");
        addRule(RULESET, "NoClassAttribute");
        addRule(RULESET, "NoHtmlComments");
        addRule(RULESET, "NoJspForward");
    }
}
