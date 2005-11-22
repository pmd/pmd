/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class MissingStaticMethodInNonInstantiatableClassTest extends SimpleAggregatorTst {
    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("design", "MissingStaticMethodInNonInstantiatableClass");
    }
    
    public void testAll() {
       runTests(new TestDescriptor[] {
               new TestDescriptor(TEST1, "ok", 0, rule),
               new TestDescriptor(TEST2, "ok, default constructor", 0, rule),
               new TestDescriptor(TEST3, "simple failure", 1, rule),
               new TestDescriptor(TEST4, "failure with multiple constructors", 1, rule),
               new TestDescriptor(TEST5, "protected constructor is ok", 0, rule),
               new TestDescriptor(TEST6, "ok, one static method", 0, rule),
               new TestDescriptor(TEST7, "nested class", 0, rule),
               new TestDescriptor(TEST8, "ok, public static field", 0, rule),
               new TestDescriptor(TEST9, "not ok, non-public static field", 1, rule),
       });
    }
    
    private static final String TEST1 =
        "public class Foo {" + PMD.EOL +
        "}";
    
    private static final String TEST2 =
        "public class Foo {" + PMD.EOL +
        " public void bar() {} " + PMD.EOL +
        "}";

    private static final String TEST3 =
        "public class Foo {" + PMD.EOL +
        " private Foo() {}" + PMD.EOL +
        " public void bar() {}" + PMD.EOL +
        "}";

    private static final String TEST4 =
        "public class Foo {" + PMD.EOL +
        " private Foo(){}" + PMD.EOL +
        " private Foo(Object o){}" + PMD.EOL +
        " public void bar() {} " + PMD.EOL +
        "}";

    private static final String TEST5 =
        "public class Foo {" + PMD.EOL +
        " private Foo(){}" + PMD.EOL +
        " protected Foo(Object o){}" + PMD.EOL +
        " public void bar() {} " + PMD.EOL +
        "}";

    private static final String TEST6 =
        "public class Foo {" + PMD.EOL +
        " private Foo(){}" + PMD.EOL +
        " private Foo(Object o){}" + PMD.EOL +
        " public static void bar() {} " + PMD.EOL +
        "}";

    private static final String TEST7 =
        "public class Foo {" + PMD.EOL +
        " private static class Bar {" + PMD.EOL +
        "  private Bar() {}" + PMD.EOL +
        " }" + PMD.EOL +
        "}";

    private static final String TEST8 =
        "public class Foo {" + PMD.EOL +
        " public static int BUZ = 2;" + PMD.EOL +
        "  private Foo() {}" + PMD.EOL +
        "}";

    private static final String TEST9 =
        "public class Foo {" + PMD.EOL +
        " private static int BUZ = 2;" + PMD.EOL +
        "  private Foo() {}" + PMD.EOL +
        "}";

}
