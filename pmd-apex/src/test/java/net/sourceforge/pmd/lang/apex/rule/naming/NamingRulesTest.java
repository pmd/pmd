/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.rule.naming;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class NamingRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "apex-naming";

    @Override
    public void setUp() {
        addRule(RULESET, "AbstractNaming");
        addRule(RULESET, "AvoidFieldNameMatchingMethodName");
        addRule(RULESET, "AvoidFieldNameMatchingTypeName");
        addRule(RULESET, "BooleanGetMethodName");
        addRule(RULESET, "ClassNamingConventions");
        addRule(RULESET, "LongVariable");
        addRule(RULESET, "MethodNamingConventions");
        addRule(RULESET, "MethodWithSameNameAsEnclosingClass");
        addRule(RULESET, "MisleadingVariableName");
        addRule(RULESET, "ShortMethodName");
        addRule(RULESET, "ShortClassName");
        addRule(RULESET, "ShortVariable");
        addRule(RULESET, "SuspiciousConstantFieldName");
        addRule(RULESET, "VariableNamingConventions");
    }
}
