package test.net.sourceforge.pmd.rules.strings;

import net.sourceforge.pmd.Rule;

import org.junit.Before;
import org.junit.Test;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

import java.util.List;
import java.util.ArrayList;

public class StringsRulesTest extends SimpleAggregatorTst {
    private List<Rule> rules = new ArrayList<Rule>();

    @Before
    public void setUp() {
        rules.add(findRule("strings", "AppendCharacterWithChar"));
        rules.add(findRule("strings", "ConsecutiveLiteralAppends"));
        rules.add(findRule("strings", "InefficientEmptyStringCheck"));
        rules.add(findRule("strings", "InefficientStringBuffering"));
        rules.add(findRule("strings", "InsufficientStringBufferDeclaration"));
        rules.add(findRule("strings", "StringBufferInstantiationWithChar"));
        rules.add(findRule("strings", "StringInstantiation"));
        rules.add(findRule("strings", "StringToString"));
        rules.add(findRule("strings", "UnnecessaryCaseChange"));
        rules.add(findRule("strings", "UseEqualsToCompareStrings"));
        rules.add(findRule("strings", "UseIndexOfChar"));
        rules.add(findRule("strings", "UselessStringValueOf"));
        rules.add(findRule("strings", "UseStringBufferLength"));
    }

    @Test
    public void testAll() {
        for (Rule r : rules) {
            runTests(r);
        }
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(StringsRulesTest.class);
    }
}
