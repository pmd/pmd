package test.net.sourceforge.pmd.rules.junit;

import net.sourceforge.pmd.rules.junit.JUnitAssertionsShouldIncludeMessageRule;
import test.net.sourceforge.pmd.rules.RuleTst;

public class JUnitAssertionsShouldIncludeMessageRuleTest extends RuleTst {

    public void testAssertEqualsOK() throws Throwable {
        super.runTest("junit/JUnitAssertionsShouldIncludeMessageRule1.java", 0, new JUnitAssertionsShouldIncludeMessageRule());
    }

    public void testAssertEqualsBad() throws Throwable {
        super.runTest("junit/JUnitAssertionsShouldIncludeMessageRule2.java", 1, new JUnitAssertionsShouldIncludeMessageRule());
    }

    public void testAssertTrueOK() throws Throwable {
        super.runTest("junit/JUnitAssertionsShouldIncludeMessageRule3.java", 0, new JUnitAssertionsShouldIncludeMessageRule());
    }

    public void testAssertTrueBad() throws Throwable {
        super.runTest("junit/JUnitAssertionsShouldIncludeMessageRule4.java", 1, new JUnitAssertionsShouldIncludeMessageRule());
    }

    public void testAssertNullOK() throws Throwable {
        super.runTest("junit/JUnitAssertionsShouldIncludeMessageRule5.java", 0, new JUnitAssertionsShouldIncludeMessageRule());
    }

    public void testAssertNullBad() throws Throwable {
        super.runTest("junit/JUnitAssertionsShouldIncludeMessageRule6.java", 1, new JUnitAssertionsShouldIncludeMessageRule());
    }

    public void testAssertSameOK() throws Throwable {
        super.runTest("junit/JUnitAssertionsShouldIncludeMessageRule7.java", 0, new JUnitAssertionsShouldIncludeMessageRule());
    }

    public void testAssertSameBad() throws Throwable {
        super.runTest("junit/JUnitAssertionsShouldIncludeMessageRule8.java", 1, new JUnitAssertionsShouldIncludeMessageRule());
    }

    public void testAssertNotNullOK() throws Throwable {
        super.runTest("junit/JUnitAssertionsShouldIncludeMessageRule9.java", 0, new JUnitAssertionsShouldIncludeMessageRule());
    }

    public void testAssertNotNullBad() throws Throwable {
        super.runTest("junit/JUnitAssertionsShouldIncludeMessageRule10.java", 1, new JUnitAssertionsShouldIncludeMessageRule());
    }

    public void testFindBug() throws Throwable {
        super.runTest("junit/JUnitAssertionsShouldIncludeMessageRule11.java", 0, new JUnitAssertionsShouldIncludeMessageRule());
    }


}
