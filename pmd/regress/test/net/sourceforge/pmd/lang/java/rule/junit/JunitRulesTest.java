package test.net.sourceforge.pmd.lang.java.rule.junit;

import org.junit.Before;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class JunitRulesTest extends SimpleAggregatorTst {
    
    private static final String RULESET = "java-junit";

    @Before
    public void setUp() {
        addRule(RULESET, "JUnitAssertionsShouldIncludeMessage");
        addRule(RULESET, "JUnitSpelling");
        addRule(RULESET, "JUnitStaticSuite");
        addRule(RULESET, "JUnitTestsShouldIncludeAssert");
        addRule(RULESET, "SimplifyBooleanAssertion");
        addRule(RULESET, "TestClassWithoutTestCases");
        addRule(RULESET, "UnnecessaryBooleanAssertion");
        addRule(RULESET, "UseAssertEqualsInsteadOfAssertTrue");
        addRule(RULESET, "UseAssertNullInsteadOfAssertTrue");
        addRule(RULESET, "UseAssertSameInsteadOfAssertTrue");
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(JunitRulesTest.class);
    }
}
