package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.rules.ConstructorCallsOverridableMethodRule;
import net.sourceforge.pmd.cpd.CPD;

public class ConstructorCallsOverridableMethodRuleTest extends RuleTst {

    private static final String TEST1 =
    "public class ConstructorCallsOverridableMethodRule1 {" + CPD.EOL +
    " public ConstructorCallsOverridableMethodRule1() {" + CPD.EOL +
    "  foo();" + CPD.EOL +
    " }" + CPD.EOL +
    " public void foo() {}" + CPD.EOL +
    "}";

    private static final String TEST2 =
    "public class ConstructorCallsOverridableMethodRule2 {" + CPD.EOL +
    " public ConstructorCallsOverridableMethodRule2() {" + CPD.EOL +
    "  foo();" + CPD.EOL +
    " }" + CPD.EOL +
    " protected void foo() {}" + CPD.EOL +
    "}";

    private static final String TEST3 =
    "public class ConstructorCallsOverridableMethodRule3 {" + CPD.EOL +
    " public ConstructorCallsOverridableMethodRule3() {" + CPD.EOL +
    "  foo();" + CPD.EOL +
    " }" + CPD.EOL +
    " void foo() {}" + CPD.EOL +
    "}";

    private static final String TEST4 =
    "public class ConstructorCallsOverridableMethodRule4 {" + CPD.EOL +
    " public ConstructorCallsOverridableMethodRule4() {" + CPD.EOL +
    "  foo();" + CPD.EOL +
    " }" + CPD.EOL +
    " private void foo() {}" + CPD.EOL +
    "}";

    private static final String TEST5 =
    "public class ConstructorCallsOverridableMethodRule5 {" + CPD.EOL +
    " public ConstructorCallsOverridableMethodRule5() {" + CPD.EOL +
    "  this(\"Bar\");" + CPD.EOL +
    " }" + CPD.EOL +
    " private ConstructorCallsOverridableMethodRule5(String bar) {" + CPD.EOL +
    "  foo();" + CPD.EOL +
    " }" + CPD.EOL +
    " public void foo() {}" + CPD.EOL +
    "}";


    public void testCallsPublic() throws Throwable {
        runTestFromString(TEST1, 1, new ConstructorCallsOverridableMethodRule());
    }
    public void testCallsProtected() throws Throwable {
        runTestFromString(TEST2, 1, new ConstructorCallsOverridableMethodRule());
    }
    public void testCallsPackage() throws Throwable {
        runTestFromString(TEST3, 1, new ConstructorCallsOverridableMethodRule());
    }
    public void testCallsPrivateMethodOK() throws Throwable {
        runTestFromString(TEST4, 0, new ConstructorCallsOverridableMethodRule());
    }
    public void testCallsPrivateConstructor() throws Throwable {
        runTestFromString(TEST5, 1, new ConstructorCallsOverridableMethodRule());
    }
}
