/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.rules.AvoidDeeplyNestedIfStmtsRule;
import test.net.sourceforge.pmd.testframework.RuleTst;

public class AvoidDeeplyNestedIfStmtsRuleTest extends RuleTst {

    public static final String TEST1 =
    "public class AvoidDeeplyNestedIfStmtsRule1 {" + PMD.EOL +
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
    "public class AvoidDeeplyNestedIfStmtsRule2 {" + PMD.EOL +
    " public void bar() { " + PMD.EOL +
    "  if (true) {" + PMD.EOL +
    "  } else if (true) {" + PMD.EOL +
    "  } else if (true) {" + PMD.EOL +
    "  } else {" + PMD.EOL +
    "    // this ain't good code, but it shouldn't trigger this rule" + PMD.EOL +
    "  }" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private Rule rule;

    public void setUp() {
        rule = new AvoidDeeplyNestedIfStmtsRule();
        rule.addProperty("problemDepth", "3");
    }

    public void test1() throws Throwable {
        runTestFromString(TEST1, 1, rule);
    }

    public void test2() throws Throwable {
        runTestFromString(TEST2, 0, rule);
    }
}
