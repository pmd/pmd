package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.rules.UnusedPrivateFieldRule;
import net.sourceforge.pmd.cpd.CPD;

public class UnusedPrivateFieldRuleTest extends RuleTst {

    private static final String TEST1 =
    "public class UnusedPrivateField1 {" + CPD.EOL +
    "private String foo;" + CPD.EOL +
    "}";

    private static final String TEST2 =
    "public class UnusedPrivateField2 {" + CPD.EOL +
    " " + CPD.EOL +
    " private String foo;" + CPD.EOL +
    " private String bar = foo;" + CPD.EOL +
    " " + CPD.EOL +
    " public void buz() {" + CPD.EOL +
    "  bar = null;" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST3 =
    "public class UnusedPrivateField3 {" + CPD.EOL +
    "" + CPD.EOL +
    " private String foo;" + CPD.EOL +
    "" + CPD.EOL +
    " public void baz() {" + CPD.EOL +
    "  Runnable r = new Runnable() {" + CPD.EOL +
    "   public void run() {" + CPD.EOL +
    "    String foo = \"buz\";" + CPD.EOL +
    "   }" + CPD.EOL +
    "  };   " + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST4 =
    "// this catches the case where the variable is used semantically before it's declared syntactically" + CPD.EOL +
    "public class UnusedPrivateField4 {" + CPD.EOL +
    " public void bar() {" + CPD.EOL +
    "  foo[0] = 0;" + CPD.EOL +
    " }" + CPD.EOL +
    " private int[] foo;" + CPD.EOL +
    "}";

    private static final String TEST5 =
    "public class UnusedPrivateField5 {" + CPD.EOL +
    " private String foo;" + CPD.EOL +
    " public void bar() {   " + CPD.EOL +
    "  this.foo = null;" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST6 =
    "public class UnusedPrivateField6 {" + CPD.EOL +
    " private static final String FOO = \"foo\";" + CPD.EOL +
    "  public Runnable bar() {      " + CPD.EOL +
    "   return new Runnable() {" + CPD.EOL +
    "    public void run() {" + CPD.EOL +
    "     FOO.toString();" + CPD.EOL +
    "    }" + CPD.EOL +
    "   };" + CPD.EOL +
    "  }" + CPD.EOL +
    "}";

    private static final String TEST7 =
    "public interface UnusedPrivateField7 {" + CPD.EOL +
    " public static final String FOO = \"FOO\"; " + CPD.EOL +
    " public boolean equals(Object another);" + CPD.EOL +
    " public int hashCode();" + CPD.EOL +
    " public String toString();" + CPD.EOL +
    "}";

    private static final String TEST8 =
    "public class UnusedPrivateField8 {" + CPD.EOL +
    " public static class Services {" + CPD.EOL +
    "  private String x;    " + CPD.EOL +
    " }" + CPD.EOL +
    "}" + CPD.EOL +
    "";

    private static final String TEST9 =
    "public class UnusedPrivateField9 {" + CPD.EOL +
    " private int x;" + CPD.EOL +
    " private class Bar {" + CPD.EOL +
    "  public void baz() {" + CPD.EOL +
    "   x = 2;" + CPD.EOL +
    "  }" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST10 =
    "public class UnusedPrivateField10 {" + CPD.EOL +
    " private static String foo;" + CPD.EOL +
    "}";

    private static final String TEST11 =
    "public class UnusedPrivateField11 {" + CPD.EOL +
    " private static String serialVersionUID=\"hey\";" + CPD.EOL +
    "}";

    private static final String TEST12 =
    "public class UnusedPrivateField12 {" + CPD.EOL +
    " private static final int BAR = 2;" + CPD.EOL +
    " int x = UnusedPrivateField12.BAR;" + CPD.EOL +
    "}";

    private static final String TEST13 =
    "public class UnusedPrivateField13 {" + CPD.EOL +
    " public class Foo {}" + CPD.EOL +
    " private int x;" + CPD.EOL +
    "}";

    private static final String TEST14 =
    "public class UnusedPrivateField14 {" + CPD.EOL +
    " public class Foo {private int x;}" + CPD.EOL +
    " public class Bar {private int x;}" + CPD.EOL +
    "}";

    private static final String TEST15 =
    "public class UnusedPrivateField15 {" + CPD.EOL +
    " private int value;" + CPD.EOL +
    " public int doSomething(int value) { " + CPD.EOL +
    "  return value + 1; " + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST16 =
    "public class UnusedPrivateField16 {" + CPD.EOL +
    " private int x; " + CPD.EOL +
    " public UnusedPrivateField17(int x) {" + CPD.EOL +
    "  this.x=x;" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST17 =
    "public class UnusedPrivateField17 {" + CPD.EOL +
    " public static final String FOO = \"foo\";" + CPD.EOL +
    "}";

    private static final String TEST18 =
    "public class UnusedPrivateField18 {" + CPD.EOL +
    " private int x;" + CPD.EOL +
    " public void foo() {" + CPD.EOL +
    "  UnusedPrivateField19 u = new UnusedPrivateField19();  " + CPD.EOL +
    "  u.x = 2;" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST19 =
    "public class UnusedPrivateField19 {" + CPD.EOL +
    " private static final ObjectStreamField[] serialPersistentFields = {new ObjectStreamField(\"foo\", String.class)};" + CPD.EOL +
    "}";

    private UnusedPrivateFieldRule rule;

    public void setUp() {
        rule = new UnusedPrivateFieldRule();
        rule.setMessage("Avoid this stuff -> ''{0}''");
    }

    public void test1() throws Throwable {
        runTestFromString(TEST1, 1, rule);
    }
    public void test2() throws Throwable {
        runTestFromString(TEST2, 0, rule);
    }
    public void test3() throws Throwable {
        runTestFromString(TEST3, 1, rule);
    }
    public void test4() throws Throwable {
        runTestFromString(TEST4, 0, rule);
    }
    public void test5() throws Throwable {
        runTestFromString(TEST5, 0, rule);
    }
    public void test6() throws Throwable {
        runTestFromString(TEST6, 0, rule);
    }
    public void test7() throws Throwable {
        runTestFromString(TEST7, 0, rule);
    }
    public void test8() throws Throwable {
        runTestFromString(TEST8, 1, rule);
    }

    // TODO
    // this test defines the current behavior of this rule
    // i.e., it doesn't check instance vars in inner classes
    // when that's fixed, this test will break
    // and we should replace the current test with the commented out test
    // TODO
    public void test9() throws Throwable {
        runTestFromString(TEST9, 0, rule);
        //runTestFromString(TEST9, 1, rule);
    }

    public void test10() throws Throwable {
        runTestFromString(TEST10, 1, rule);
    }
    public void test11() throws Throwable {
        runTestFromString(TEST11, 0, rule);
    }
    public void test12() throws Throwable {
        runTestFromString(TEST12, 0, rule);
    }
    public void test13() throws Throwable {
        runTestFromString(TEST13, 1, rule);
    }
    public void test14() throws Throwable {
        runTestFromString(TEST14, 2, rule);
    }
    public void test15() throws Throwable {
        runTestFromString(TEST15, 1, rule);
    }
    public void test16() throws Throwable {
        runTestFromString(TEST16, 0, rule);
    }
    public void test17() throws Throwable {
        runTestFromString(TEST17, 0, rule);
    }
    public void test18() throws Throwable {
        runTestFromString(TEST18, 0, rule);
    }
    public void test19() throws Throwable {
        runTestFromString(TEST19, 0, rule);
    }
}
