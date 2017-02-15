/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.rule.basic;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class BasicRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "vf-basic";

    @Override
    public void setUp() {
        addRule(RULESET, "VfUnescapeEl");
    }
}
