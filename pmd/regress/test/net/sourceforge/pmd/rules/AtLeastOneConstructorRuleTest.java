package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.rules.AtLeastOneConstructorRule;

public class AtLeastOneConstructorRuleTest extends RuleTst {

    private static final String TEST1 =
    "public class AtLeastOneConstructorRule1 {" + PMD.EOL +
    " public AtLeastOneConstructorRule1() {}" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class AtLeastOneConstructorRule2 {" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class AtLeastOneConstructorRule3 {" + PMD.EOL +
    " public class Foo {}" + PMD.EOL +
    " public AtLeastOneConstructorRule3() {}" + PMD.EOL +
    "}";

    private static final String TEST4 =
    "public class AtLeastOneConstructorRule4 {" + PMD.EOL +
    " public class Foo { " + PMD.EOL +
    "  public Foo() {}" + PMD.EOL +
    " }" + PMD.EOL +
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
