/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.complexity;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class ComplexityRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "apex-complexity";

    @Override
    public void setUp() {
        addRule(RULESET, "AvoidDeeplyNestedIfStmts");
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
