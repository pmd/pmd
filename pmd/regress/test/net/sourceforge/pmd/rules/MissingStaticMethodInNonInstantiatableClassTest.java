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
        runTests(new TestDescriptor[]{
            new TestDescriptor(TEST1, "ok", 0, rule),
            new TestDescriptor(TEST2, "ok, default constructor", 0, rule),
            new TestDescriptor(TEST3, "simple failure", 1, rule),
            new TestDescriptor(TEST4, "failure with multiple constructors", 1, rule),
            new TestDescriptor(TEST5, "protected constructor is ok", 0, rule),
            new TestDescriptor(TEST6, "ok, one static method", 0, rule),
            new TestDescriptor(TEST7, "nested class", 0, rule),
            new TestDescriptor(TEST8, "ok, public static field", 0, rule),
            new TestDescriptor(TEST9, "not ok, non-public static field", 1, rule),
            new TestDescriptor(TEST10, "ok, protected static field", 0, rule),
            new TestDescriptor(TEST11, "ok, package private static field", 0, rule),
            new TestDescriptor(TEST12, "ok, checking for bug 1432595", 0, rule),
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

    private static final String TEST10 =
            "public class Foo {" + PMD.EOL +
            " protected static Foo INSTANCE = new Foo();" + PMD.EOL +
            "  private Foo() {}" + PMD.EOL +
            "}";

    private static final String TEST11 =
            "public class Foo {" + PMD.EOL +
            " static Foo INSTANCE = new Foo();" + PMD.EOL +
            "  private Foo() {}" + PMD.EOL +
            "}";
    
    private static final String TEST12 =
        "public class Suit {" + PMD.EOL +
        "   private final String name;" + PMD.EOL +
        "        private Suit(String name) {" + PMD.EOL +
        "        this.name = name;" + PMD.EOL +
        "  }" + PMD.EOL +
        "  public String toString() {" + PMD.EOL +
        "      return name;" + PMD.EOL +
        "  }" + PMD.EOL +
        "  public static final Suit CLUBS = new Suit(\"Clubs\");" + PMD.EOL +
        "  public static final Suit DIAMONDS = new Suit(\"Diamonds\");" + PMD.EOL +
        "  public static final Suit HEARTS = new Suit(\"Hearts\");" + PMD.EOL +
        "  public static final Suit SPADES = new Suit(\"Spades\");" + PMD.EOL +
        "}";

}
