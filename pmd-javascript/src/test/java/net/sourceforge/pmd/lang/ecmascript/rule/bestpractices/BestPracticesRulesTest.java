/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.rule.bestpractices;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class BestPracticesRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "category/ecmascript/bestpractices.xml";

    @Override
    public void setUp() {
        addRule(RULESET, "AvoidWithStatement");
        addRule(RULESET, "ConsistentReturn");
        addRule(RULESET, "GlobalVariable");
        addRule(RULESET, "ScopeForInVariable");
        addRule(RULESET, "UseBaseWithParseInt");
    }
}
