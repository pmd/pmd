/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class UncommentedEmptyConstructorRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() {
        rule = findRule("design", "UncommentedEmptyConstructor");
    }

    public void testAll() {
        runTests(new TestDescriptor[] {
            new TestDescriptor(TEST1, "simple failure", 1, rule),
            new TestDescriptor(TEST2, "only 'this(...)' failure", 1, rule),
            new TestDescriptor(TEST3, "only 'super(...)' failure", 1, rule),
            new TestDescriptor(TEST4, "single-line comment is OK", 0, rule),
            new TestDescriptor(TEST5, "multiple-line comment is OK", 0, rule),
            new TestDescriptor(TEST6, "Javadoc comment is OK", 0, rule),
            new TestDescriptor(TEST7, "ok", 0, rule),
            new TestDescriptor(TEST8, "with 'this(...)' ok", 0, rule),
            new TestDescriptor(TEST9, "with 'super(...)' ok", 0, rule),
        });
    }

    public static final String TEST1 =
            "public class Foo {" + PMD.EOL +
            " Foo() {" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    public static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    " Foo() {" + PMD.EOL +
    "  this();" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    public static final String TEST3 =
    "public class Foo {" + PMD.EOL +
    " Foo() {" + PMD.EOL +
    "  super();" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    public static final String TEST4 =
            "public class Foo {" + PMD.EOL +
            " Foo() {" + PMD.EOL +
            "  // Comment" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    public static final String TEST5 =
    "public class Foo {" + PMD.EOL +
    " Foo() {" + PMD.EOL +
    "  this();" + PMD.EOL +
    "  /* Comment */" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    public static final String TEST6 =
    "public class Foo {" + PMD.EOL +
    " Foo() {" + PMD.EOL +
    "  super();" + PMD.EOL +
    "  /** Comment */" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    public static final String TEST7 =
            "public class Foo {" + PMD.EOL +
            " Foo() {" + PMD.EOL +
            "  int bar;" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    public static final String TEST8 =
    "public class Foo {" + PMD.EOL +
    " Foo() {" + PMD.EOL +
    "  this();" + PMD.EOL +
    "  bar();" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    public static final String TEST9 =
    "public class Foo {" + PMD.EOL +
    " Foo() {" + PMD.EOL +
    "  super();" + PMD.EOL +
    "  bar++;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";
}
