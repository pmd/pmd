package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.rules.AtLeastOneConstructorRule;

public class AtLeastOneConstructorRuleTest extends RuleTst {

    public void testSimpleOK() throws Throwable {
        super.runTestFromFile("AtLeastOneConstructorRule1.java", 0, new AtLeastOneConstructorRule());
    }

    public void testSimpleBad() throws Throwable {
        super.runTestFromFile("AtLeastOneConstructorRule2.java", 1, new AtLeastOneConstructorRule());
    }

    public void testNestedClassWithout() throws Throwable {
        super.runTestFromFile("AtLeastOneConstructorRule3.java", 1, new AtLeastOneConstructorRule());
    }

    public void testNestedClassWithOuterWithout() throws Throwable {
        super.runTestFromFile("AtLeastOneConstructorRule4.java", 1, new AtLeastOneConstructorRule());
    }
}
