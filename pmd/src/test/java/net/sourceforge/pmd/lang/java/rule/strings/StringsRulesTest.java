/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.strings;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class StringsRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "java-strings";

    @Override
    public void setUp() {
        addRule(RULESET, "AppendCharacterWithChar");
        addRule(RULESET, "AvoidStringBufferField");
        addRule(RULESET, "ConsecutiveAppendsShouldReuse");
        addRule(RULESET, "ConsecutiveLiteralAppends");
        addRule(RULESET, "InefficientEmptyStringCheck");
        addRule(RULESET, "InefficientStringBuffering");
        addRule(RULESET, "InsufficientStringBufferDeclaration");
        addRule(RULESET, "StringBufferInstantiationWithChar");
        addRule(RULESET, "StringInstantiation");
        addRule(RULESET, "StringToString");
        addRule(RULESET, "UnnecessaryCaseChange");
        addRule(RULESET, "UseEqualsToCompareStrings");
        addRule(RULESET, "UseIndexOfChar");
        addRule(RULESET, "UselessStringValueOf");
        addRule(RULESET, "UseStringBufferLength");
    }
}
