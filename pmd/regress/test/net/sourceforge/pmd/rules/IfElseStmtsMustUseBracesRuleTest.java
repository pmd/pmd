/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class IfElseStmtsMustUseBracesRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws Exception {
        rule = findRule("rulesets/braces.xml", "IfElseStmtsMustUseBracesRule");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "else without braces", 1, rule),
           new TestDescriptor(TEST2, "ok", 0, rule),
           new TestDescriptor(TEST3, "two sets of missing braces", 2, rule),
           new TestDescriptor(TEST4, "elseif with missing braces", 1, rule),
           new TestDescriptor(TEST5, "elseif with braces after else", 0, rule),
           new TestDescriptor(TEST6, "elseif with missing braces, first braces on separate line", 1, rule),
       });
    }

    private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    " void foo() {     " + PMD.EOL +
    "  if (true) {" + PMD.EOL +
    "   x=2;" + PMD.EOL +
    "  } else " + PMD.EOL +
    "   y=4;" + PMD.EOL +
    "  " + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    " void foo() {     " + PMD.EOL +
    "  if (true) {" + PMD.EOL +
    "   x=2;" + PMD.EOL +
    "  } else {" + PMD.EOL +
    "   x=4;" + PMD.EOL +
    "  }" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class Foo {" + PMD.EOL +
    " void foo() {     " + PMD.EOL +
    "  if (true) " + PMD.EOL +
    "   y=2;" + PMD.EOL +
    "  else " + PMD.EOL +
    "   x=4;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST4 =
    "public class Foo {" + PMD.EOL +
    " void foo() {     " + PMD.EOL +
    "  if (true) {" + PMD.EOL +
    "   x=2;" + PMD.EOL +
    "  } else if (true) { " + PMD.EOL +
    "   x = 3; " + PMD.EOL +
    "  } else " + PMD.EOL +
    "   y=4;" + PMD.EOL +
    "  " + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST5 =
    "public class Foo {" + PMD.EOL +
    " void foo() {     " + PMD.EOL +
    "  if (true) {" + PMD.EOL +
    "   x=2;" + PMD.EOL +
    "  } else if (true) { " + PMD.EOL +
    "   x = 3; " + PMD.EOL +
    "  } else { " + PMD.EOL +
    "   y=4;" + PMD.EOL +
    "  }" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST6 =
    "public class Foo {" + PMD.EOL +
    " void foo() {     " + PMD.EOL +
    "  if (true) " + PMD.EOL +
    "  {" + PMD.EOL +
    "   x=2;" + PMD.EOL +
    "  } " + PMD.EOL +
    "  else " + PMD.EOL +
    "   y=4;" + PMD.EOL +
    "  " + PMD.EOL +
    " }" + PMD.EOL +
    "}";

}
