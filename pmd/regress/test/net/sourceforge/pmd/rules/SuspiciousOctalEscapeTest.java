package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class SuspiciousOctalEscapeTest extends SimpleAggregatorTst {
    private Rule rule;

    public void setUp() {
        rule = findRule("controversial", "SuspiciousOctalEscape");
    }
    
    public void testAll() {
        runTests(new TestDescriptor[]{
            new TestDescriptor(TEST1, "ok use of octal", 0, rule),
            new TestDescriptor(TEST2, "should be flagged", 1, rule),
            new TestDescriptor(TEST3, "should be flagged - different octal", 1, rule),
            new TestDescriptor(TEST4, "should be flagged - different octal", 1, rule),
        });
    }

    private static final String TEST1 =
            "public class Foo {" + PMD.EOL +
            " void bar() {" + PMD.EOL +
            "  int x = \128;" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST2 =
            "public class Foo {" + PMD.EOL +
            " void bar() {" + PMD.EOL +
            "  System.out.println(\"foo = \\128\");" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST3 =
        "public class Foo {" + PMD.EOL +
        " void bar() {" + PMD.EOL +
        "  System.out.println(\"foo = \\0008\");" + PMD.EOL +
        " }" + PMD.EOL +
        "}";
    
    private static final String TEST4 =
        "public class Foo {" + PMD.EOL +
        " void bar() {" + PMD.EOL +
        "  System.out.println(\"foo = \\4008\");" + PMD.EOL +
        " }" + PMD.EOL +
        "}";
    
}
