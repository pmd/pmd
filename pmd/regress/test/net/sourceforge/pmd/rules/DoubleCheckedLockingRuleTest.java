package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.rules.DoubleCheckedLockingRule;

public class DoubleCheckedLockingRuleTest extends RuleTst {

    public void testSimpleOK() throws Throwable {
        runTestFromFile("DoubleCheckedLockingRule1.java", 0, new DoubleCheckedLockingRule());
    }

    public void testSimpleBad() throws Throwable {
        runTestFromFile("DoubleCheckedLockingRule2.java", 1, new DoubleCheckedLockingRule());
    }

    public void testSkipInterfaces() throws Throwable {
        runTestFromFile("DoubleCheckedLockingRule3.java", 0, new DoubleCheckedLockingRule());
    }
}
