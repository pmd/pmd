package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class SingularFieldRuleTest extends SimpleAggregatorTst {
    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("controversial", "SingularField");
    }

    public void testAll() {
        runTests(new TestDescriptor[]{
            new TestDescriptor(TEST1, "failure case", 1, rule),
            new TestDescriptor(TEST2, "ok", 0, rule),
            new TestDescriptor(TEST3, "second method uses 'this'", 0, rule),
            new TestDescriptor(TEST4, "skip publics", 0, rule),
            new TestDescriptor(TEST5, "skip statics", 0, rule),
            new TestDescriptor(TEST6, "unused fields shouldn't show up", 0, rule),
            new TestDescriptor(TEST7, "inner class", 0, rule),
            new TestDescriptor(TEST8, "initialized in constructor", 0, rule),
            new TestDescriptor(TEST9, "failure case in inner class", 1, rule),
            new TestDescriptor(TEST10, "failure case with Object", 1, rule),
            new TestDescriptor(TEST11, "failure case in static inner class", 1, rule),
            new TestDescriptor(TEST12, "ok, shouldn't catch unused variable", 0, rule),
            new TestDescriptor(TEST13, "failure case with self-instantiation", 1, rule),
            new TestDescriptor(TEST14, "Reuse variable name as params in method calls", 1, rule),
            new TestDescriptor(TEST15, "failure case in two static inner classes", 1, rule),
            new TestDescriptor(TEST16, "OK, instantiates own self internally", 0, rule),
            new TestDescriptor(TEST17, "failure, variable accessed twice in same method", 1, rule),
            new TestDescriptor(TEST18, "failure, static", 1, rule),
            new TestDescriptor(TEST19, "failure, second method re-uses class level name", 1, rule),
        });
    }

    private static final String TEST1 =
            "public class Foo {" + PMD.EOL +
            " private int x;" + PMD.EOL +
            " int bar(int y) {" + PMD.EOL +
            "  x = y + 5; " + PMD.EOL +
            "  return x;" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST2 =
            "public class Foo {" + PMD.EOL +
            " private int x;" + PMD.EOL +
            " void setX(int x) {" + PMD.EOL +
            "  this.x = x;" + PMD.EOL +
            " }" + PMD.EOL +
            " int getX() {" + PMD.EOL +
            "  return x;" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST3 =
            "public class Foo {" + PMD.EOL +
            " private int x;" + PMD.EOL +
            " void setX(int x) {" + PMD.EOL +
            "  this.x = x;" + PMD.EOL +
            " }" + PMD.EOL +
            " int getX() {" + PMD.EOL +
            "  return this.x;" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST4 =
            "public class Foo {" + PMD.EOL +
            " public int x;" + PMD.EOL +
            " int bar(int y) {" + PMD.EOL +
            "  x = y + 5; " + PMD.EOL +
            "  return x;" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST5 =
            "public class Foo {" + PMD.EOL +
            " private static int x;" + PMD.EOL +
            " int bar(int y) {" + PMD.EOL +
            "  x = y + 5; " + PMD.EOL +
            "  return x;" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST6 =
            "public class Foo {" + PMD.EOL +
            " int a = 3;" + PMD.EOL +
            " int b = 3;" + PMD.EOL +
            " void bar() {" + PMD.EOL +
            "  foo(b); " + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST7 =
            "public class Foo {" + PMD.EOL +
            " private int a;" + PMD.EOL +
            " void bar() {" + PMD.EOL +
            "  Object o = new FooAdapter() {" + PMD.EOL +
            "   public void bar(Event e) {" + PMD.EOL +
            "    a = e.GetInt();" + PMD.EOL +
            "   }" + PMD.EOL +
            "  }; " + PMD.EOL +
            " }" + PMD.EOL +
            " int baz() {" + PMD.EOL +
            "  return a; " + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST8 =
            "public class Foo {" + PMD.EOL +
            " private int x;" + PMD.EOL +
            " public Foo(int y) {" + PMD.EOL +
            "  x = y; " + PMD.EOL +
            " }" + PMD.EOL +
            " int bar(int y) {" + PMD.EOL +
            "  x = y + 5; " + PMD.EOL +
            "  return x;" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST9 =
        "public class Foo {" + PMD.EOL +
        " class Bar {" + PMD.EOL +
        " private int x;" + PMD.EOL +
        " int bar(int y) {" + PMD.EOL +
        "  x = y + 5; " + PMD.EOL +
        "  return x;" + PMD.EOL +
        " }" + PMD.EOL +
        " }" + PMD.EOL +
        "}";

    private static final String TEST10 =
        "public class Foo {" + PMD.EOL +
        " private Integer x = null;" + PMD.EOL +
        " private Integer getFoo() {" + PMD.EOL +
        "  if(x == null){; " + PMD.EOL +
        "      x = new Integer(1);; " + PMD.EOL +
        "  }; " + PMD.EOL +
        "  return x;" + PMD.EOL +
        " }" + PMD.EOL +
        "}";

    private static final String TEST11 =
        "public class Foo {" + PMD.EOL +
        " static class Bar {" + PMD.EOL +
        " private int x;" + PMD.EOL +
        " int bar(int y) {" + PMD.EOL +
        "   this.x = y + 5; " + PMD.EOL +
        " }" + PMD.EOL +
        " }" + PMD.EOL +
        "}";
    

    private static final String TEST12 =
            "public class Foo {" + PMD.EOL +
            " private int x;" + PMD.EOL +
            " void bar(int y) {" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST13 =
        "public class Foo {" + PMD.EOL +
        " private Integer x = new Integer(1);" + PMD.EOL +
        " private Integer getFoo() {" + PMD.EOL +
        "  return x;" + PMD.EOL +
        " }" + PMD.EOL +
        "}";

    private static final String TEST14 =
        "public class Foo {" + PMD.EOL +
        " private Integer x = new Integer(1);" + PMD.EOL +
        " public Foo(Integer x) {" + PMD.EOL +
        " }" + PMD.EOL +
        " private void getFoo(Integer x) {" + PMD.EOL +
        "  this.x = x;" + PMD.EOL +
        " }" + PMD.EOL +
        "}";

    private static final String TEST15 =
        "public class Foo {" + PMD.EOL +
        " static class Bar1 {" + PMD.EOL +
        " private int x;" + PMD.EOL +
        " int bar(int y) {" + PMD.EOL +
        "   this.x = y + 5; " + PMD.EOL +
        " }" + PMD.EOL +
        " }" + PMD.EOL +
        " static class Bar2 {" + PMD.EOL +
        " private int x;" + PMD.EOL +
        " int getX() {" + PMD.EOL +
        "   return x; " + PMD.EOL +
        " }" + PMD.EOL +
        " int setX(int y) {" + PMD.EOL +
        "   this.x = y + 5; " + PMD.EOL +
        " }" + PMD.EOL +
        " }" + PMD.EOL +
        "}";
    
    private static final String TEST16 =
        "public class Foo {" + PMD.EOL +
        " private int x;" + PMD.EOL +
        " int bar(int y) {" + PMD.EOL +
        "  x = y + 5; " + PMD.EOL +
        "  return x;" + PMD.EOL +
        " }" + PMD.EOL +
        " private void bar() {" + PMD.EOL +
        "  Foo foo = new Foo();" + PMD.EOL +
        "  foo.x = new Integer(5);" + PMD.EOL +
        " }" + PMD.EOL +
        "}";


    private static final String TEST17 =
            "public class Foo {" + PMD.EOL +
            " private Map map = new SomeMap();" + PMD.EOL +
            " private boolean bar(Object o) {" + PMD.EOL +
            "     boolean ret = true;" + PMD.EOL +
            "     if(super.isTrue) {" + PMD.EOL +
            "     if(map.get(o) != null) {" + PMD.EOL +
            "         ret = false; " + PMD.EOL +
            "     } else {" + PMD.EOL +
            "         map.put(o,o); " + PMD.EOL +
            "     } " + PMD.EOL +
            "     } " + PMD.EOL +
            "     return ret; " + PMD.EOL +
            " }" + PMD.EOL +
            "}";
    
    private static final String TEST18 =
        "public class Foo {" + PMD.EOL +
        " private int x;" + PMD.EOL +
        " static {" + PMD.EOL +
        "  x = 5; " + PMD.EOL +
        " }" + PMD.EOL +
        "}";

    private static final String TEST19 =
        "public class Foo {" + PMD.EOL +
        " private int x;" + PMD.EOL +
        " void setX(int x) {" + PMD.EOL +
        "  this.x = x;" + PMD.EOL +
        " }" + PMD.EOL +
        " int doX() {" + PMD.EOL +
        "  int x = 5;" + PMD.EOL +
        "  return x*3;" + PMD.EOL +
        " }" + PMD.EOL +
        "}";
    
}
