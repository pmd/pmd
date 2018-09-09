/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vm.rule.design;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public abstract class DesignRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "category/vm/design.xml";

    @Override
    public void setUp() {
        addRule(RULESET, getClass().getSimpleName().replaceFirst("Test$", ""));
    }
}
