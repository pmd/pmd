/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class AvoidDeeplyNestedIfStmtsRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() {
        rule = findRule("design", "AvoidDeeplyNestedIfStmts");
        rule.addProperty("problemDepth", "3");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "Bad, very deep", 1, rule),
           new TestDescriptor(TEST2, "OK, not so deep", 0, rule),
       });
    }

    public static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    " public void bar() { " + PMD.EOL +
    "  int x=2; " + PMD.EOL +
    "  int y=3; " + PMD.EOL +
    "  int z=4; " + PMD.EOL +
    "  if (x>y) { " + PMD.EOL +
    "   if (y>z) { " + PMD.EOL +
    "    if (z==x) { " + PMD.EOL +
    "     // this is officially out of control now " + PMD.EOL +
    "    } " + PMD.EOL +
    "   } " + PMD.EOL +
    "  }" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    public static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    " public void bar() { " + PMD.EOL +
    "  if (true) {" + PMD.EOL +
    "  } else if (true) {" + PMD.EOL +
    "  } else if (true) {" + PMD.EOL +
    "  } else {" + PMD.EOL +
    "    // this ain't good code, but it shouldn't trigger this rule" + PMD.EOL +
    "  }" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

}
