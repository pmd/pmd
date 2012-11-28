package net.sourceforge.pmd.lang.java.rule.strings;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

import org.junit.Before;


public class StringsRulesTest extends SimpleAggregatorTst {
    
    private static final String RULESET = "java-strings";

    @Before
    public void setUp() {
        addRule(RULESET, "AppendCharacterWithChar");
        addRule(RULESET, "AvoidStringBufferField");
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

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(StringsRulesTest.class);
    }
}
