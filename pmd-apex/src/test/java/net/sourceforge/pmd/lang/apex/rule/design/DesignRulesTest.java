/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.design;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.lang.apex.metrics.ApexMetricsHook;
import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class DesignRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "category/apex/design.xml";

    @Override
    protected Rule reinitializeRule(Rule rule) {
        ApexMetricsHook.reset();
        return super.reinitializeRule(rule);
    }

    @Override
    public void setUp() {
        addRule(RULESET, "AvoidDeeplyNestedIfStmts");
        addRule(RULESET, "CyclomaticComplexity");
        addRule(RULESET, "ExcessiveClassLength");
        addRule(RULESET, "ExcessiveParameterList");
        addRule(RULESET, "ExcessivePublicCount");
        addRule(RULESET, "NcssConstructorCount");
        addRule(RULESET, "NcssMethodCount");
        addRule(RULESET, "NcssTypeCount");
        addRule(RULESET, "StdCyclomaticComplexity");
        addRule(RULESET, "TooManyFields");
    }
}
