/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.rules.design.OnlyOneReturnRule;
import test.net.sourceforge.pmd.testframework.RuleTst;

public class OnlyOneReturnRuleTest extends RuleTst {

    private static final String TEST1 =
    "public class OnlyOneReturn1 {" + PMD.EOL +
    " public String foo(int x) {    " + PMD.EOL +
    "  if (x > 0) {" + PMD.EOL +
    "   return \"hey\";" + PMD.EOL +
    "  }" + PMD.EOL +
    "  return \"hi\";" + PMD.EOL +
    " }" + PMD.EOL +
    "}";


    private static final String TEST2 =
    "public class OnlyOneReturn2 {" + PMD.EOL +
    " public String foo(int x) {    " + PMD.EOL +
    "  return \"hi\";" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class OnlyOneReturn3 {" + PMD.EOL +
    " public void foo(int x) {      " + PMD.EOL +
    "  int y =2;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST4 =
    "public class OnlyOneReturn4 {" + PMD.EOL +
    " public void foo(int x) {      " + PMD.EOL +
    "  if (x>2) {" + PMD.EOL +
    "    return;" + PMD.EOL +
    "  }" + PMD.EOL +
    "  int y =2;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST5 =
    "public class OnlyOneReturn5 {" + PMD.EOL +
    " public int foo(int x) {" + PMD.EOL +
    "  try {" + PMD.EOL +
    "   x += 2;" + PMD.EOL +
    "   return x;" + PMD.EOL +
    "  } finally {" + PMD.EOL +
    "   System.err.println(\"WunderBuggy!\");" + PMD.EOL +
    "  }" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST6 =
    "public class OnlyOneReturn6 {" + PMD.EOL +
    " public int foo() {" + PMD.EOL +
    "  FileFilter f = new FileFilter() {" + PMD.EOL +
    "   public boolean accept(File file) {" + PMD.EOL +
    "    return false;" + PMD.EOL +
    "   }" + PMD.EOL +
    "  };" + PMD.EOL +
    "  return 2;" + PMD.EOL +
    " }" + PMD.EOL +
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
