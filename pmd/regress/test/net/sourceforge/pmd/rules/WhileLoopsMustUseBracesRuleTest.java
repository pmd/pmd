package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.rules.XPathRule;

public class WhileLoopsMustUseBracesRuleTest extends RuleTst {

    private static final String TEST1 =
    "public class WhileLoopsNeedBraces1 {" + CPD.EOL +
    " public void foo() {   " + CPD.EOL +
    "  int x =0;" + CPD.EOL +
    "  while (true)" + CPD.EOL +
    "   x++;" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST2 =
    "public class WhileLoopsNeedBraces2 {" + CPD.EOL +
    " public void foo() {   " + CPD.EOL +
    "  int x =0;" + CPD.EOL +
    "  while (true) {" + CPD.EOL +
    "   x++;" + CPD.EOL +
    "  }" + CPD.EOL +
    " }" + CPD.EOL +
    "}";


    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty("xpath", "//WhileStatement[not(Statement/Block)]");
    }

    public void test1() throws Throwable {
        runTestFromString(TEST1, 1, rule);
    }
    public void test2() throws Throwable {
        runTestFromString(TEST2, 0, rule);
    }
}
