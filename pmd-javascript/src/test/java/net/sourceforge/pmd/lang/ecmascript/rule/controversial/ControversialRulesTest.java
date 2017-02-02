/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.rule.controversial;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class ControversialRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "ecmascript-controversial";

    @Override
    public void setUp() {
        addRule(RULESET, "AvoidWithStatement");
    }
}
