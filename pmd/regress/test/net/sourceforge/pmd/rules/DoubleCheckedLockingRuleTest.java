package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.rules.DoubleCheckedLockingRule;

public class DoubleCheckedLockingRuleTest extends RuleTst {

    public void testSimpleOK() throws Throwable {
        runTest("DoubleCheckedLockingRule1.java", 0 , new DoubleCheckedLockingRule());
    }

    public void testSimpleBad() throws Throwable {
        runTest("DoubleCheckedLockingRule2.java", 1 , new DoubleCheckedLockingRule());
    }

    public void testSkipInterfaces() throws Throwable {
        runTest("DoubleCheckedLockingRule3.java", 0 , new DoubleCheckedLockingRule());
    }
}
