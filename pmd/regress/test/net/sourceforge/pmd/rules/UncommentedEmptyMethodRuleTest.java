/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class UncommentedEmptyMethodRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() {
        rule = findRule("design", "UncommentedEmptyMethod");
    }

    public void testAll() {
        runTests(new TestDescriptor[] {
            new TestDescriptor(TEST1, "simple failure", 1, rule),
            new TestDescriptor(TEST2, "single-line comment is OK", 0, rule),
            new TestDescriptor(TEST3, "multiple-line comment is OK", 0, rule),
            new TestDescriptor(TEST4, "Javadoc comment is OK", 0, rule),
            new TestDescriptor(TEST5, "ok", 0, rule),
        });
    }

    public static final String TEST1 =
            "public class Foo {" + PMD.EOL +
            " void bar() {" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST2 =
            "public class Foo {" + PMD.EOL +
            " void bar() {" + PMD.EOL +
            "  // Comment" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST3 =
            "public class Foo {" + PMD.EOL +
            " void foo() {" + PMD.EOL +
            "  /* Comment */" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST4 =
            "public class Foo {" + PMD.EOL +
            " void foo() {" + PMD.EOL +
            "  /** Comment */" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST5 =
            "public class Foo {" + PMD.EOL +
            " void foo() {" + PMD.EOL +
            "  statement();" + PMD.EOL +
            " }" + PMD.EOL +
            "}";
}
