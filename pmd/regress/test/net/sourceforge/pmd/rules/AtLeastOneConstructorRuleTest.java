package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.rules.AtLeastOneConstructorRule;
import net.sourceforge.pmd.cpd.CPD;

public class AtLeastOneConstructorRuleTest extends RuleTst {

    private static final String TEST1 =
    "public class AtLeastOneConstructorRule1 {" + CPD.EOL +
    " public AtLeastOneConstructorRule1() {}" + CPD.EOL +
    "}";

    private static final String TEST2 =
    "public class AtLeastOneConstructorRule2 {" + CPD.EOL +
    "}";

    private static final String TEST3 =
    "public class AtLeastOneConstructorRule3 {" + CPD.EOL +
    " public class Foo {}" + CPD.EOL +
    " public AtLeastOneConstructorRule3() {}" + CPD.EOL +
    "}";

    private static final String TEST4 =
    "public class AtLeastOneConstructorRule4 {" + CPD.EOL +
    " public class Foo { " + CPD.EOL +
    "  public Foo() {}" + CPD.EOL +
    " }" + CPD.EOL +
    "}";


    public void testSimpleOK() throws Throwable {
        runTestFromString(TEST1, 0, new AtLeastOneConstructorRule());
    }

    public void testSimpleBad() throws Throwable {
        runTestFromString(TEST2, 1, new AtLeastOneConstructorRule());
    }

    public void testNestedClassWithout() throws Throwable {
        runTestFromString(TEST3, 1, new AtLeastOneConstructorRule());
    }

    public void testNestedClassWithOuterWithout() throws Throwable {
        runTestFromString(TEST4, 1, new AtLeastOneConstructorRule());
    }
}
