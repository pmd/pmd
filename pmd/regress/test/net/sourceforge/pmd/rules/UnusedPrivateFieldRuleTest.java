/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class UnusedPrivateFieldRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws Exception {
        rule = findRule("unusedcode", "UnusedPrivateField");
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
           new TestDescriptor(TEST19, "an assignment does not a usage make", 1, rule),
           new TestDescriptor(TEST20, "assignment to field member is a usage", 0, rule),
           new TestDescriptor(TEST21, "assignment to field member using this modifier is a usage", 0, rule),
           new TestDescriptor(TEST22, "this.foo++ shouldn't throw an NPE, but isn't a usage", 1, rule),
           new TestDescriptor(TEST23, "super.foo++ shouldn't throw an NPE", 0, rule),
       });
    }
    private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    "private String foo;" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    " private String foo;" + PMD.EOL +
    " private String bar = foo;" + PMD.EOL +
    " void buz() {" + PMD.EOL +
    "  bar = bar + 1;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class Foo {" + PMD.EOL +
    " private String foo;" + PMD.EOL +
    " void baz() {" + PMD.EOL +
    "  Runnable r = new Runnable() {" + PMD.EOL +
    "   public void run() {" + PMD.EOL +
    "    String foo = \"buz\";" + PMD.EOL +
    "   }" + PMD.EOL +
    "  };   " + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST4 =
    "public class Foo {" + PMD.EOL +
    " void bar() {" + PMD.EOL +
    "  foo[0] = foo[0] + 1;" + PMD.EOL +
    " }" + PMD.EOL +
    " private int[] foo;" + PMD.EOL +
    "}";

    private static final String TEST5 =
    "public class Foo {" + PMD.EOL +
    " private String foo;" + PMD.EOL +
    " void bar() {   " + PMD.EOL +
    "  bar = this.foo;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST6 =
    "public class Foo {" + PMD.EOL +
    " private static final String FOO = \"foo\";" + PMD.EOL +
    "  public Runnable bar() {      " + PMD.EOL +
    "   return new Runnable() {" + PMD.EOL +
    "    public void run() {" + PMD.EOL +
    "     FOO.toString();" + PMD.EOL +
    "    }" + PMD.EOL +
    "   };" + PMD.EOL +
    "  }" + PMD.EOL +
    "}";

    private static final String TEST7 =
    "public interface Foo {" + PMD.EOL +
    " public static final String FOO = \"FOO\"; " + PMD.EOL +
    " public boolean equals(Object another);" + PMD.EOL +
    " public int hashCode();" + PMD.EOL +
    " public String toString();" + PMD.EOL +
    "}";

    private static final String TEST8 =
    "public class Foo {" + PMD.EOL +
    " public static class Services {" + PMD.EOL +
    "  private String x;    " + PMD.EOL +
    " }" + PMD.EOL +
    "}" + PMD.EOL +
    "";

    private static final String TEST9 =
    "public class Foo {" + PMD.EOL +
    " private int x;" + PMD.EOL +
    " private class Bar {" + PMD.EOL +
    "  void baz() {" + PMD.EOL +
    "   x = x + 2;" + PMD.EOL +
    "  }" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST10 =
    "public class Foo {" + PMD.EOL +
    " private static String foo;" + PMD.EOL +
    "}";

    private static final String TEST11 =
    "public class Foo {" + PMD.EOL +
    " private static final int BAR = 2;" + PMD.EOL +
    " int x = Foo.BAR;" + PMD.EOL +
    "}";

    private static final String TEST12 =
    "public class Foo {" + PMD.EOL +
    " public class Foo {}" + PMD.EOL +
    " private int x;" + PMD.EOL +
    "}";

    private static final String TEST13 =
    "public class Foo {" + PMD.EOL +
    " public class Foo {private int x;}" + PMD.EOL +
    " public class Bar {private int x;}" + PMD.EOL +
    "}";

    private static final String TEST14 =
    "public class Foo {" + PMD.EOL +
    " private int value;" + PMD.EOL +
    " int doSomething(int value) { " + PMD.EOL +
    "  return value + 1; " + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST15 =
    "public class Foo {" + PMD.EOL +
    " private int x; " + PMD.EOL +
    " public Foo(int x) {" + PMD.EOL +
    "  this.x= this.x + 1;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST16 =
    "public class Foo {" + PMD.EOL +
    " public static final int FOO = 1;" + PMD.EOL +
    "}";

    private static final String TEST17 =
    "public class Foo {" + PMD.EOL +
    " private int x;" + PMD.EOL +
    " void foo() {" + PMD.EOL +
    "  Foo foo = new Foo();  " + PMD.EOL +
    "  foo.x = foo.x + 2;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST18 =
    "public class Foo {" + PMD.EOL +
    " private static final ObjectStreamField[] serialPersistentFields = {new ObjectStreamField(\"foo\", String.class)};" + PMD.EOL +
    "}";

    private static final String TEST19 =
    "public class Foo {" + PMD.EOL +
    " private int x;" + PMD.EOL +
    " void bar() {" + PMD.EOL +
    "  x = 4;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST20 =
    "public class Foo {" + PMD.EOL +
    " private Foo x = new Foo();" + PMD.EOL +
    " void bar() {" + PMD.EOL +
    "  x.y = 42;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST21 =
    "public class Foo {" + PMD.EOL +
    " private Foo x = new Foo();" + PMD.EOL +
    " void bar() {" + PMD.EOL +
    "  this.x.y = 42;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST22 =
    "public class Foo {" + PMD.EOL +
    " private int x;" + PMD.EOL +
    " void bar() {" + PMD.EOL +
    "  this.x++;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST23 =
    "public class Foo {" + PMD.EOL +
    " void bar() {" + PMD.EOL +
    "  super.x++;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

}
