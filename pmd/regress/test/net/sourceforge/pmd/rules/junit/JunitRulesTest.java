package test.net.sourceforge.pmd.rules.junit;

import net.sourceforge.pmd.Rule;

import org.junit.Before;
import org.junit.Test;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

import java.util.List;
import java.util.ArrayList;

public class JunitRulesTest extends SimpleAggregatorTst {
    private List<Rule> rules = new ArrayList<Rule>();

    @Before
    public void setUp() {
        rules.add(findRule("junit", "JUnitAssertionsShouldIncludeMessage"));
        rules.add(findRule("junit", "JUnitSpelling"));
        rules.add(findRule("junit", "JUnitStaticSuite"));
        rules.add(findRule("junit", "JUnitTestsShouldIncludeAssert"));
        rules.add(findRule("junit", "SimplifyBooleanAssertion"));
        rules.add(findRule("junit", "TestClassWithoutTestCases"));
        rules.add(findRule("junit", "UnnecessaryBooleanAssertion"));
        rules.add(findRule("junit", "UseAssertEqualsInsteadOfAssertTrue"));
        rules.add(findRule("junit", "UseAssertNullInsteadOfAssertTrue"));
        rules.add(findRule("junit", "UseAssertSameInsteadOfAssertTrue"));
    }

    @Test
    public void testAll() {
        for (Rule r : rules) {
            runTests(r);
        }
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(JunitRulesTest.class);
    }
}
