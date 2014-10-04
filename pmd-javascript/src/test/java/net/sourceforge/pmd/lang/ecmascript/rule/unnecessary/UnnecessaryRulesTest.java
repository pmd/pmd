/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ecmascript.rule.unnecessary;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class UnnecessaryRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "ecmascript-unnecessary";

    @Override
    public void setUp() {
        addRule(RULESET, "UnnecessaryBlock");
        addRule(RULESET, "UnnecessaryParentheses");
    }
}
