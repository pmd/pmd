package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.rules.UnusedPrivateMethodRule;

public class UnusedPrivateMethodRuleTest extends SimpleAggregatorTst {

    private UnusedPrivateMethodRule rule;

    public void setUp() {
        rule = new UnusedPrivateMethodRule();
        rule.setMessage("Avoid this stuff -> ''{0}''");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "private method called by public method", 0, rule),
           new TestDescriptor(TEST2, "simple unused private method", 1, rule),
           new TestDescriptor(TEST3, "anonymous inner class calls private method", 0, rule),
           new TestDescriptor(TEST4, "two private methods with same name but different parameters", 1, rule),
           new TestDescriptor(TEST5, "calling private method after instantiating new copy of myself", 0, rule),
           new TestDescriptor(TEST6, "calling private method using 'this' modifier", 0, rule),
       });
    }
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

}
