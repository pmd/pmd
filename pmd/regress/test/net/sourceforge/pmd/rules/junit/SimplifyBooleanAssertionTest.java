package test.net.sourceforge.pmd.rules.junit;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class SimplifyBooleanAssertionTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() {
        rule = findRule("junit", "SimplifyBooleanAssertion");
    }

    public void testAll() {
        runTests(new TestDescriptor[]{
            new TestDescriptor(TEST1, "assertFalse(!)", 1, rule),
            new TestDescriptor(TEST2, "assertTrue(!)", 1, rule),
            new TestDescriptor(TEST3, "ok", 0, rule),
        });
    }

    private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    "   void testBar() { " + PMD.EOL +
    "    boolean bar; " + PMD.EOL +
    "    assertFalse(!bar);" + PMD.EOL +
    "   } " + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    "   void testBar() { " + PMD.EOL +
    "    boolean bar; " + PMD.EOL +
    "    assertTrue(!bar);" + PMD.EOL +
    "   } " + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class Foo {" + PMD.EOL +
    "   void testBar() { " + PMD.EOL +
    "    boolean bar; " + PMD.EOL +
    "    assertTrue(bar);" + PMD.EOL +
    "    assertFalse(bar);" + PMD.EOL +
    "   } " + PMD.EOL +
    "}";
}
