package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.rules.AtLeastOneConstructorRule;

public class AtLeastOneConstructorRuleTest extends RuleTst {

    public void testSimpleOK() throws Throwable {
        super.runTest("AtLeastOneConstructorRule1.java", 0, new AtLeastOneConstructorRule());
    }

    public void testSimpleBad() throws Throwable {
        super.runTest("AtLeastOneConstructorRule2.java", 1, new AtLeastOneConstructorRule());
    }

    public void testNestedClassWithout() throws Throwable {
        super.runTest("AtLeastOneConstructorRule3.java", 1, new AtLeastOneConstructorRule());
    }

    public void testNestedClassWithOuterWithout() throws Throwable {
        super.runTest("AtLeastOneConstructorRule4.java", 1, new AtLeastOneConstructorRule());
    }
}
