package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.rules.SimplifyBooleanReturnsRule;

public class SimplifyBooleanReturnsRuleTest extends RuleTst {

    private static final String TEST1 =
    "public class SimplifyBooleanReturns1 {" + PMD.EOL +
    " public void foo() {   " + PMD.EOL +
    "  if (true) {" + PMD.EOL +
    "   return true;" + PMD.EOL +
    "  } else {" + PMD.EOL +
    "  return false;" + PMD.EOL +
    "  }" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class SimplifyBooleanReturns2 {" + PMD.EOL +
    " public boolean foo() {        " + PMD.EOL +
    "  if (true) " + PMD.EOL +
    "   return true;" + PMD.EOL +
    "   else " + PMD.EOL +
    "  return false;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class SimplifyBooleanReturns3 {" + PMD.EOL +
    " public Object foo() { " + PMD.EOL +
    "  if (!true) {" + PMD.EOL +
    "   return null;" + PMD.EOL +
    "  } else  {}" + PMD.EOL +
    "  return null;" + PMD.EOL +
    " }" + PMD.EOL +
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
