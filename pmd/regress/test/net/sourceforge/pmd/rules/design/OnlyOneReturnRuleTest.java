package test.net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.rules.design.OnlyOneReturnRule;
import test.net.sourceforge.pmd.rules.RuleTst;

public class OnlyOneReturnRuleTest extends RuleTst {

    private static final String TEST1 =
    "public class OnlyOneReturn1 {" + CPD.EOL +
    " public String foo(int x) {    " + CPD.EOL +
    "  if (x > 0) {" + CPD.EOL +
    "   return \"hey\";" + CPD.EOL +
    "  }" + CPD.EOL +
    "  return \"hi\";" + CPD.EOL +
    " }" + CPD.EOL +
    "}";


    private static final String TEST2 =
    "public class OnlyOneReturn2 {" + CPD.EOL +
    " public String foo(int x) {    " + CPD.EOL +
    "  return \"hi\";" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST3 =
    "public class OnlyOneReturn3 {" + CPD.EOL +
    " public void foo(int x) {      " + CPD.EOL +
    "  int y =2;" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST4 =
    "public class OnlyOneReturn4 {" + CPD.EOL +
    " public void foo(int x) {      " + CPD.EOL +
    "  if (x>2) {" + CPD.EOL +
    "    return;" + CPD.EOL +
    "  }" + CPD.EOL +
    "  int y =2;" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST5 =
    "public class OnlyOneReturn5 {" + CPD.EOL +
    " public int foo(int x) {" + CPD.EOL +
    "  try {" + CPD.EOL +
    "   x += 2;" + CPD.EOL +
    "   return x;" + CPD.EOL +
    "  } finally {" + CPD.EOL +
    "   System.err.println(\"WunderBuggy!\");" + CPD.EOL +
    "  }" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST6 =
    "public class OnlyOneReturn6 {" + CPD.EOL +
    " public int foo() {" + CPD.EOL +
    "  FileFilter f = new FileFilter() {" + CPD.EOL +
    "   public boolean accept(File file) {" + CPD.EOL +
    "    return false;" + CPD.EOL +
    "   }" + CPD.EOL +
    "  };" + CPD.EOL +
    "  return 2;" + CPD.EOL +
    " }" + CPD.EOL +
    "}";



    public void testTwoReturns() throws Throwable {
        runTestFromString(TEST1, 1, new OnlyOneReturnRule());
    }
    public void testOneReturn() throws Throwable {
        runTestFromString(TEST2, 0, new OnlyOneReturnRule());
    }
    public void testNoReturns() throws Throwable {
        runTestFromString(TEST3, 0, new OnlyOneReturnRule());
    }
    public void testVoidRtn() throws Throwable {
        runTestFromString(TEST4, 0, new OnlyOneReturnRule());
    }
    public void testFinally() throws Throwable {
        runTestFromString(TEST5, 0, new OnlyOneReturnRule());
    }
    public void testReturnInsideAnonymousInnerClass() throws Throwable {
        runTestFromString(TEST6, 0, new OnlyOneReturnRule());
    }
}
