package test.net.sourceforge.pmd.rules.junit;

import org.junit.Before;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class JunitRulesTest extends SimpleAggregatorTst {

    @Before
    public void setUp() {
        addRule("junit", "JUnitAssertionsShouldIncludeMessage");
        addRule("junit", "JUnitSpelling");
        addRule("junit", "JUnitStaticSuite");
        addRule("junit", "JUnitTestsShouldIncludeAssert");
        addRule("junit", "SimplifyBooleanAssertion");
        addRule("junit", "TestClassWithoutTestCases");
        addRule("junit", "UnnecessaryBooleanAssertion");
        addRule("junit", "UseAssertEqualsInsteadOfAssertTrue");
        addRule("junit", "UseAssertNullInsteadOfAssertTrue");
        addRule("junit", "UseAssertSameInsteadOfAssertTrue");
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(JunitRulesTest.class);
    }
}
