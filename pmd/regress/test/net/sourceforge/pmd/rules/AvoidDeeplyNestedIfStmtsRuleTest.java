package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.rules.AvoidDeeplyNestedIfStmtsRule;

public class AvoidDeeplyNestedIfStmtsRuleTest extends RuleTst {

    private static final String TEST1 =
    "public class AvoidDeeplyNestedIfStmtsRule1 {" + CPD.EOL +
    " public void bar() { " + CPD.EOL +
    "  int x=2; " + CPD.EOL +
    "  int y=3; " + CPD.EOL +
    "  int z=4; " + CPD.EOL +
    "  if (x>y) { " + CPD.EOL +
    "   if (y>z) { " + CPD.EOL +
    "    if (z==x) { " + CPD.EOL +
    "     // this is officially out of control now " + CPD.EOL +
    "    } " + CPD.EOL +
    "   } " + CPD.EOL +
    "  }" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST2 =
    "public class AvoidDeeplyNestedIfStmtsRule2 {" + CPD.EOL +
    " public void bar() { " + CPD.EOL +
    "  if (true) {" + CPD.EOL +
    "  } else if (true) {" + CPD.EOL +
    "  } else if (true) {" + CPD.EOL +
    "  } else {" + CPD.EOL +
    "    // this ain't good code, but it shouldn't trigger this rule" + CPD.EOL +
    "  }" + CPD.EOL +
    " }" + CPD.EOL +
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
