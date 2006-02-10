/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class SimplifyBooleanExpressionsRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("design", "SimplifyBooleanExpressions");
    }

    public void testAll() {
        runTests(new TestDescriptor[]{
            new TestDescriptor(TEST1, "in field assignment", 1, rule),
            new TestDescriptor(TEST2, "in method body", 1, rule),
            new TestDescriptor(TEST3, "ok", 0, rule),
            new TestDescriptor(TEST4, "two cases in an && expression", 2, rule),
            new TestDescriptor(TEST5, "simple use of BooleanLiteral, should not be flagged", 0, rule),
        });
    }

    private static final String TEST1 =
            "public class Foo {" + PMD.EOL +
            " private boolean foo = (isFoo() == true);" + PMD.EOL +
            " boolean isFoo() {return foo;}" + PMD.EOL +
            "}";

    private static final String TEST2 =
            "public class Foo {" + PMD.EOL +
            " void foo() {" + PMD.EOL +
            "  boolean bar = (new String().length() >2) == false;" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST3 =
            "public class Foo {" + PMD.EOL +
            " boolean bar = true;" + PMD.EOL +
            "}";

    private static final String TEST4 =
            "public class Foo {" + PMD.EOL +
            " void bar() {" + PMD.EOL +
            "  if (getFoo() == false && isBar() == true) {}" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST5 =
            "public class Foo {" + PMD.EOL +
            " void bar() {" + PMD.EOL +
            "  if (true) {}" + PMD.EOL +
            "  if (false) {}" + PMD.EOL +
            " }" + PMD.EOL +
            "}";
}
