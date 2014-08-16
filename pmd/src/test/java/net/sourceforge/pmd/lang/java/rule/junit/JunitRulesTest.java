/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.junit;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class JunitRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "java-junit";

    @Override
    public void setUp() {
        addRule(RULESET, "JUnitAssertionsShouldIncludeMessage");
        addRule(RULESET, "JUnitSpelling");
        addRule(RULESET, "JUnitStaticSuite");
        addRule(RULESET, "JUnitTestContainsTooManyAsserts");
        addRule(RULESET, "JUnitTestsShouldIncludeAssert");
        addRule(RULESET, "SimplifyBooleanAssertion");
        addRule(RULESET, "TestClassWithoutTestCases");
        addRule(RULESET, "UnnecessaryBooleanAssertion");
        addRule(RULESET, "UseAssertEqualsInsteadOfAssertTrue");
        addRule(RULESET, "UseAssertNullInsteadOfAssertTrue");
        addRule(RULESET, "UseAssertSameInsteadOfAssertTrue");
        addRule(RULESET, "UseAssertTrueInsteadOfAssertEquals");
    }
}
