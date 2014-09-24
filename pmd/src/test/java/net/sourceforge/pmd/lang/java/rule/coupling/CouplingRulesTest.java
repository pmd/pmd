/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.coupling;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class CouplingRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "java-coupling";

    @Override
    public void setUp() {
        addRule(RULESET, "CouplingBetweenObjects");
        addRule(RULESET, "ExcessiveImports");
        addRule(RULESET, "LooseCoupling");
        addRule(RULESET, "LoosePackageCoupling");
        addRule(RULESET, "LawOfDemeter");
    }
}
