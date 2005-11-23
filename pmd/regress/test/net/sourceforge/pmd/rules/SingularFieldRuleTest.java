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
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "failure case", 1, rule),
           new TestDescriptor(TEST2, "ok", 0, rule),
           new TestDescriptor(TEST3, "second method uses 'this'", 0, rule),
           new TestDescriptor(TEST4, "skip publics", 0, rule),
           new TestDescriptor(TEST5, "skip statics", 0, rule),
           new TestDescriptor(TEST6, "unused fields shouldn't show up", 0, rule),
           new TestDescriptor(TEST7, "inner class", 0, rule),
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
    "  Object o = new FooAdapter() {"  + PMD.EOL +
    "   public void bar(Event e) {"  + PMD.EOL +
    "    a = e.GetInt();"  + PMD.EOL +
    "   }"  + PMD.EOL +
    "  }; " + PMD.EOL +
    " }" + PMD.EOL +
    " int baz() {" + PMD.EOL +
    "  return a; " + PMD.EOL +
    " }" + PMD.EOL +
    "}";

}
