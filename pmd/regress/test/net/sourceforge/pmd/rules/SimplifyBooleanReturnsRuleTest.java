package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.rules.SimplifyBooleanReturnsRule;

public class SimplifyBooleanReturnsRuleTest extends RuleTst {

    private static final String TEST1 =
    "public class SimplifyBooleanReturns1 {" + CPD.EOL +
    " public void foo() {   " + CPD.EOL +
    "  if (true) {" + CPD.EOL +
    "   return true;" + CPD.EOL +
    "  } else {" + CPD.EOL +
    "  return false;" + CPD.EOL +
    "  }" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST2 =
    "public class SimplifyBooleanReturns2 {" + CPD.EOL +
    " public boolean foo() {        " + CPD.EOL +
    "  if (true) " + CPD.EOL +
    "   return true;" + CPD.EOL +
    "   else " + CPD.EOL +
    "  return false;" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST3 =
    "public class SimplifyBooleanReturns3 {" + CPD.EOL +
    " public Object foo() { " + CPD.EOL +
    "  if (!true) {" + CPD.EOL +
    "   return null;" + CPD.EOL +
    "  } else  {}" + CPD.EOL +
    "  return null;" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    public void test1() throws Throwable {
        runTestFromString(TEST1, 1, new SimplifyBooleanReturnsRule());
    }
    public void test2() throws Throwable {
        runTestFromString(TEST2, 1, new SimplifyBooleanReturnsRule());
    }
    public void test3() throws Throwable {
        runTestFromString(TEST3, 0, new SimplifyBooleanReturnsRule());
    }
}
