package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.rules.UnusedPrivateFieldRule;

public class UnusedPrivateFieldRuleTest extends SimpleAggregatorTst {

    private UnusedPrivateFieldRule rule;

    public void setUp() {
        rule = new UnusedPrivateFieldRule();
        rule.setMessage("Avoid this stuff -> ''{0}''");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "simple unused private field", 1, rule),
           new TestDescriptor(TEST2, "private field referenced in another field's initializer", 0, rule),
           new TestDescriptor(TEST3, "private field with field of same name in anonymous inner class", 1, rule),
           new TestDescriptor(TEST4, "field is used semantically before it's declared syntactically", 0, rule),
           new TestDescriptor(TEST5, "private field referenced via 'this' modifier", 0, rule),
           new TestDescriptor(TEST6, "private referenced by anonymous inner class", 0, rule),
           new TestDescriptor(TEST7, "interface sanity test", 0, rule),
           new TestDescriptor(TEST8, "unused private field in static inner class", 1, rule),
           new TestDescriptor(TEST9, "private field referenced in nonstatic inner class", 0, rule),
           new TestDescriptor(TEST10, "unused private static field", 1, rule),
           new TestDescriptor(TEST11, "private static final referenced with qualifier", 0, rule),
           new TestDescriptor(TEST12, "unused private field after class decl", 1, rule),
           new TestDescriptor(TEST13, "two unused private fields in separate inner classes", 2, rule),
           new TestDescriptor(TEST14, "method param shadows unused private field", 1, rule),
           new TestDescriptor(TEST15, "private field referenced via 'this' not shadowed by param of same name", 0, rule),
           new TestDescriptor(TEST16, "don't catch public fields", 0, rule),
           new TestDescriptor(TEST17, "instantiate self and reference private field on other object", 0, rule),
           new TestDescriptor(TEST18, "don't count Serialization fields as being unused", 0, rule),
       });
    }
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
    "public class Foo {" + CPD.EOL +
    " private static final int BAR = 2;" + CPD.EOL +
    " int x = Foo.BAR;" + CPD.EOL +
    "}";

    private static final String TEST12 =
    "public class Foo {" + CPD.EOL +
    " public class Foo {}" + CPD.EOL +
    " private int x;" + CPD.EOL +
    "}";

    private static final String TEST13 =
    "public class Foo {" + CPD.EOL +
    " public class Foo {private int x;}" + CPD.EOL +
    " public class Bar {private int x;}" + CPD.EOL +
    "}";

    private static final String TEST14 =
    "public class Foo {" + CPD.EOL +
    " private int value;" + CPD.EOL +
    " public int doSomething(int value) { " + CPD.EOL +
    "  return value + 1; " + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST15 =
    "public class Foo {" + CPD.EOL +
    " private int x; " + CPD.EOL +
    " public UnusedPrivateField17(int x) {" + CPD.EOL +
    "  this.x=x;" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST16 =
    "public class Foo {" + CPD.EOL +
    " public static final String FOO = \"foo\";" + CPD.EOL +
    "}";

    private static final String TEST17 =
    "public class Foo {" + CPD.EOL +
    " private int x;" + CPD.EOL +
    " public void foo() {" + CPD.EOL +
    "  Foo foo = new Foo();  " + CPD.EOL +
    "  foo.x = 2;" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST18 =
    "public class Foo {" + CPD.EOL +
    " private static final ObjectStreamField[] serialPersistentFields = {new ObjectStreamField(\"foo\", String.class)};" + CPD.EOL +
    "}";

}
