package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.rules.JUnitAssertionsShouldIncludeMessageRule;

public class JUnitAssertionsShouldIncludeMessageRuleTest extends RuleTst {

    public void testAssertEqualsOK() throws Throwable {
        super.runTest("JUnitAssertionsShouldIncludeMessageRule1.java", 0, new JUnitAssertionsShouldIncludeMessageRule());
    }

    public void testAssertEqualsBad() throws Throwable {
        super.runTest("JUnitAssertionsShouldIncludeMessageRule2.java", 1, new JUnitAssertionsShouldIncludeMessageRule());
    }

    public void testAssertTrueOK() throws Throwable {
        super.runTest("JUnitAssertionsShouldIncludeMessageRule3.java", 0, new JUnitAssertionsShouldIncludeMessageRule());
    }

    public void testAssertTrueBad() throws Throwable {
        super.runTest("JUnitAssertionsShouldIncludeMessageRule4.java", 1, new JUnitAssertionsShouldIncludeMessageRule());
    }

    public void testAssertNullOK() throws Throwable {
        super.runTest("JUnitAssertionsShouldIncludeMessageRule5.java", 0, new JUnitAssertionsShouldIncludeMessageRule());
    }

    public void testAssertNullBad() throws Throwable {
        super.runTest("JUnitAssertionsShouldIncludeMessageRule6.java", 1, new JUnitAssertionsShouldIncludeMessageRule());
    }

    public void testAssertSameOK() throws Throwable {
        super.runTest("JUnitAssertionsShouldIncludeMessageRule7.java", 0, new JUnitAssertionsShouldIncludeMessageRule());
    }

    public void testAssertSameBad() throws Throwable {
        super.runTest("JUnitAssertionsShouldIncludeMessageRule8.java", 1, new JUnitAssertionsShouldIncludeMessageRule());
    }

    public void testAssertNotNullOK() throws Throwable {
        super.runTest("JUnitAssertionsShouldIncludeMessageRule9.java", 0, new JUnitAssertionsShouldIncludeMessageRule());
    }

    public void testAssertNotNullBad() throws Throwable {
        super.runTest("JUnitAssertionsShouldIncludeMessageRule10.java", 1, new JUnitAssertionsShouldIncludeMessageRule());
    }


}
