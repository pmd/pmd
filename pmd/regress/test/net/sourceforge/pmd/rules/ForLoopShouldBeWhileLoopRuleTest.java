package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.rules.XPathRule;

public class ForLoopShouldBeWhileLoopRuleTest extends RuleTst {

    private static final String TEST1 =
    "public class ForLoopShouldBeWhileLoop1 {" + CPD.EOL +
    " public void foo() {" + CPD.EOL +
    "  int x = 2;" + CPD.EOL +
    "  for (;x<5;) { " + CPD.EOL +
    "   x++;" + CPD.EOL +
    "  }" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST2 =
    "public class ForLoopShouldBeWhileLoop2 {" + CPD.EOL +
    " public void foo() {" + CPD.EOL +
    "  for (int x=2;x<5;) { " + CPD.EOL +
    "   x++;" + CPD.EOL +
    "  }" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST3 =
    "public class ForLoopShouldBeWhileLoop3 {" + CPD.EOL +
    " public void foo() {" + CPD.EOL +
    "  for (;;) {}" + CPD.EOL +
    " }" + CPD.EOL +
    "}";


    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty(
            "xpath",
            "//ForStatement[count(*) > 1][not(ForInit)][not(ForUpdate)]");
    }

    public void testSimple() throws Throwable {
        runTestFromString(TEST1, 1, rule);
    }

    public void testOK() throws Throwable {
        runTestFromString(TEST2, 0, rule);
    }

    public void testForSemicolonSemicolon() throws Throwable {
        runTestFromString(TEST3, 0, rule);
    }
}
