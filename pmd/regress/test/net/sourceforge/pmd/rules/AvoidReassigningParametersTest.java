/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class AvoidReassigningParametersTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws Exception {
        rule = findRule("rulesets/design.xml", "AvoidReassigningParameters");
        rule.setMessage("Avoid this stuff -> ''{0}''");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
/*
           new TestDescriptor(TEST1, "reassigned parameter, bad", 1, rule),
           new TestDescriptor(TEST2, "one parameter, not reassigned, good", 0, rule),
           new TestDescriptor(TEST3, "instance variable and parameter have same name", 1, rule),
           new TestDescriptor(TEST4, "qualified instance variable same name as parameter", 0, rule),
           new TestDescriptor(TEST5, "qualified name same as parameter", 0, rule),
           new TestDescriptor(TEST6, "assignment to parameter public field", 0, rule),
           new TestDescriptor(TEST7, "assignment to array parameter slot", 0, rule),
           new TestDescriptor(TEST8, "throws a stacktrace", 1, rule),
*/
           new TestDescriptor(TEST9, "postfix increment in array dereference is bad", 1, rule),
       });
    }

    public static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    " void foo(int bar) {" + PMD.EOL +
    "  bar = 2;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    public static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    " void foo(int bar) {}" + PMD.EOL +
    "}";

    public static final String TEST3 =
    "public class Foo {" + PMD.EOL +
    " private int bar;" + PMD.EOL +
    " void foo(float bar) {" + PMD.EOL +
    "  bar = 2.2;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    public static final String TEST4 =
    "public class Foo {" + PMD.EOL +
    " private int bar;" + PMD.EOL +
    " void foo(float bar) {" + PMD.EOL +
    "  this.bar = 2;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    public static final String TEST5 =
    "public class Faz {" + PMD.EOL +
    " private class Foo {" + PMD.EOL +
    "  public String bar;" + PMD.EOL +
    " }" + PMD.EOL +
    " void foo(String bar) {" + PMD.EOL +
    "  Foo f = new Foo();" + PMD.EOL +
    "  f.bar = bar;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    public static final String TEST6 =
    "public class Foo {" + PMD.EOL +
    " void foo(Bar bar) {" + PMD.EOL +
    "  bar.buz = 2;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    public static final String TEST7 =
    "public class Foo {" + PMD.EOL +
    " void foo(Bar[] bar) {" + PMD.EOL +
    "  bar[0] = 2;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    public static final String TEST8 =
    "public class Foo {" + PMD.EOL +
    " void foo(int x) {" + PMD.EOL +
    "   try {" + PMD.EOL +
    "     x = 2;" + PMD.EOL +
    "   } catch (Throwable t) { " + PMD.EOL +
    "   } " + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    public static final String TEST9 =
    "public class Foo {" + PMD.EOL +
    " void foo(int x) {" + PMD.EOL +
    "  y[x++] = 2;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";
}
