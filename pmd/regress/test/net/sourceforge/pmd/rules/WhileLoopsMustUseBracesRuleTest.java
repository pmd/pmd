/*
 * User: tom
 * Date: Jul 19, 2002
 * Time: 11:17:08 PM
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.rules.XPathRule;

public class WhileLoopsMustUseBracesRuleTest extends RuleTst {

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty("xpath", "//WhileStatement[not(Statement/Block)]");
    }

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

    public void test1() throws Throwable {
        runTestString(TEST1, 1, rule);
    }

    public void test2() throws Throwable {
        runTestString(TEST2, 0, rule);
    }
}
