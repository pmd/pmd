/*
 * User: tom
 * Date: Jul 19, 2002
 * Time: 12:25:27 PM
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.rules.UnusedPrivateMethodRule;
import net.sourceforge.pmd.cpd.CPD;

public class UnusedPrivateMethodRuleTest extends RuleTst {

    private static final String TEST1 =
    "public class UnusedPrivateMethod1 {" + CPD.EOL +
    " public void bar() {" + CPD.EOL +
    "  foo();" + CPD.EOL +
    " }" + CPD.EOL +
    " private void foo() {}" + CPD.EOL +
    "}";

    private static final String TEST2 =
    "public class UnusedPrivateMethod2 {" + CPD.EOL +
    " private void foo() {}" + CPD.EOL +
    "}";

    private static final String TEST3 =
    "public class UnusedPrivateMethod3 {" + CPD.EOL +
    " public void bar() {" + CPD.EOL +
    "  new Runnable() {" + CPD.EOL +
    "   public void run() {" + CPD.EOL +
    "    foo();" + CPD.EOL +
    "   }" + CPD.EOL +
    "  };" + CPD.EOL +
    " }" + CPD.EOL +
    "" + CPD.EOL +
    " private void foo() {}" + CPD.EOL +
    "}";

    private static final String TEST4 =
    "public class UnusedPrivateMethod4 {" + CPD.EOL +
    " private void foo() {}" + CPD.EOL +
    " private void foo(String baz) {}" + CPD.EOL +
    " public void bar() {" + CPD.EOL +
    "  foo();" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST5 =
    "public class UnusedPrivateMethod5 {" + CPD.EOL +
    " private void foo(String[] args) {}" + CPD.EOL +
    " public static void main(String[] args) {" + CPD.EOL +
    "  UnusedPrivateMethod5 u = new UnusedPrivateMethod5();" + CPD.EOL +
    "  u.foo(args); " + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST6 =
    "public class UnusedPrivateMethod6 {" + CPD.EOL +
    " public void bar() {" + CPD.EOL +
    "  this.foo();" + CPD.EOL +
    " }" + CPD.EOL +
    " private void foo() {}" + CPD.EOL +
    "}";

    private UnusedPrivateMethodRule rule;

    public void setUp() {
        rule = new UnusedPrivateMethodRule();
        rule.setMessage("Avoid this stuff -> ''{0}''");
    }

    public void test1() throws Throwable {
        runTestFromString(TEST1, 0, rule);
    }

    public void test2() throws Throwable {
        runTestFromString(TEST2, 1, rule);
    }

    public void test3() throws Throwable {
        runTestFromString(TEST3, 0, rule);
    }

    public void test4() throws Throwable {
        runTestFromString(TEST4, 1, rule);
    }

    public void test5() throws Throwable {
        runTestFromString(TEST5, 0, rule);
    }

    public void test6() throws Throwable {
        runTestFromString(TEST6, 0, rule);
    }
}
