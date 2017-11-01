/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

/**
 * Rule tests for the best practices category
 */
public class BestPracticesRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "category/java/bestpractices.xml";

    @Override
    public void setUp() {
        addRule(RULESET, "AvoidUsingHardCodedIP");
        // addRule(RULESET, "AvoidUsingHardCodedURL");
        addRule(RULESET, "CheckResultSet");
        addRule(RULESET, "LooseCoupling");
        addRule(RULESET, "OneDeclarationPerLine");
    }

}
