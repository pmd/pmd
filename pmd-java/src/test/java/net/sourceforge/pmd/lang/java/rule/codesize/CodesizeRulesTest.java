/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codesize;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.lang.java.metrics.MetricsHook;
import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class CodesizeRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "java-codesize";


    @Override
    protected Rule reinitializeRule(Rule rule) {
        MetricsHook.reset();
        return rule;
    }


    @Override
    public void setUp() {
        addRule(RULESET, "CyclomaticComplexity");
        addRule(RULESET, "ExcessivePublicCount");
        addRule(RULESET, "ExcessiveClassLength");
        addRule(RULESET, "ExcessiveParameterList");
        addRule(RULESET, "ExcessiveMethodLength");
        addRule(RULESET, "ModifiedCyclomaticComplexity");
        addRule(RULESET, "NcssConstructorCount");
        addRule(RULESET, "NcssMethodCount");
        addRule(RULESET, "NcssTypeCount");
        addRule(RULESET, "NcssCount");
        addRule(RULESET, "NPathComplexity");
        addRule(RULESET, "StdCyclomaticComplexity");
        addRule(RULESET, "TooManyFields");
        addRule(RULESET, "TooManyMethods");
    }
}
