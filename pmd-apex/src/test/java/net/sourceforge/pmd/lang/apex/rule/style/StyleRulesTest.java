/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.style;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class StyleRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "apex-style";

    @Override
    public void setUp() {
        addRule(RULESET, "AvoidDirectAccessTriggerMap");
        addRule(RULESET, "AvoidGlobalModifier");
        addRule(RULESET, "AvoidLogicInTrigger");
        addRule(RULESET, "ClassNamingConventions");
        addRule(RULESET, "MethodNamingConventions");
        addRule(RULESET, "VariableNamingConventions");
        addRule(RULESET, "MethodWithSameNameAsEnclosingClass");
        addRule(RULESET, "AvoidHardcodingId");
    }
}
