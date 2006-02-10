/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class NullAssignmentRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("controversial", "NullAssignment");
    }

    public void testAll() {
        runTests(new TestDescriptor[]{
            new TestDescriptor(TEST1, "initial assignment", 0, rule),
            new TestDescriptor(TEST2, "bad assignment", 1, rule),
            new TestDescriptor(TEST3, "check test", 0, rule),
            new TestDescriptor(TEST4, "null param on right hand sidel", 0, rule),
            new TestDescriptor(TEST5, "null assignment in ternary", 1, rule),
            new TestDescriptor(TEST6, "null assignment in ternary, part deux", 1, rule),
            new TestDescriptor(TEST7, "comparison is not assignment", 0, rule),
            new TestDescriptor(TEST8, "final fields must be assigned", 0, rule),
        });
    }

    private static final String TEST1 =
            "public class Foo {" + PMD.EOL +
            " public Object foo() {" + PMD.EOL +
            "  Object x = null; // OK" + PMD.EOL +
            "  return x;" + PMD.EOL +
            " }       " + PMD.EOL +
            "}";

    private static final String TEST2 =
            "public class Foo {" + PMD.EOL +
            " public void foo() {" + PMD.EOL +
            "  Object x;" + PMD.EOL +
            "  x = new Object();" + PMD.EOL +
            "  bar(x);  " + PMD.EOL +
            "  x = null; // This is bad" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST3 =
            "public class Foo {" + PMD.EOL +
            " public void foo() {" + PMD.EOL +
            "  Object x;" + PMD.EOL +
            "  if (x == null) { // This is OK" + PMD.EOL +
            "   return;" + PMD.EOL +
            "  }" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST4 =
            "public class Foo {" + PMD.EOL +
            " public void foo() {" + PMD.EOL +
            "  String x = null;" + PMD.EOL +
            "  x = new String(null);" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST5 =
            "public class Foo {" + PMD.EOL +
            " public void foo() {" + PMD.EOL +
            "  String x = bar() ? \"fiz\" : null;" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST6 =
            "public class Foo {" + PMD.EOL +
            " public void foo() {" + PMD.EOL +
            "  String x = bar() ? null : \"fiz\";" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST7 =
            "public class Foo {" + PMD.EOL +
            " public String foo() {" + PMD.EOL +
            "  return x == null ? \"42\" : x;" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST8 =
            "public class Foo {" + PMD.EOL +
            " private final String x;" + PMD.EOL +
            " public Foo(String y) {" + PMD.EOL +
            "  if (y == \"\") x = null;" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

}
