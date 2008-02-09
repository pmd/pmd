package test.net.sourceforge.pmd.rules.strings;

import org.junit.Before;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class StringsRulesTest extends SimpleAggregatorTst {

    @Before
    public void setUp() {
        addRule("strings", "AppendCharacterWithChar");
        addRule("strings","AvoidStringBufferField");
        addRule("strings", "ConsecutiveLiteralAppends");
        addRule("strings", "InefficientEmptyStringCheck");
        addRule("strings", "InefficientStringBuffering");
        addRule("strings", "InsufficientStringBufferDeclaration");
        addRule("strings", "StringBufferInstantiationWithChar");
        addRule("strings", "StringInstantiation");
        addRule("strings", "StringToString");
        addRule("strings", "UnnecessaryCaseChange");
        addRule("strings", "UseEqualsToCompareStrings");
        addRule("strings", "UseIndexOfChar");
        addRule("strings", "UselessStringValueOf");
        addRule("strings", "UseStringBufferLength");
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(StringsRulesTest.class);
    }
}
