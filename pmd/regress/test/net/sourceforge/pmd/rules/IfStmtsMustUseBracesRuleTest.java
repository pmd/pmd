package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.rules.XPathRule;

public class IfStmtsMustUseBracesRuleTest extends RuleTst {

    private static final String TEST1 =
    "public class IfStmtsMustUseBraces1 {" + CPD.EOL +
    " public void foo() {" + CPD.EOL +
    "  int x = 0;   " + CPD.EOL +
    "  if (true) x=2;" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST2 =
    "public class IfStmtsMustUseBraces2 {" + CPD.EOL +
    " public void foo() {   " + CPD.EOL +
    "  if (true) {" + CPD.EOL +
    "   int x=2;" + CPD.EOL +
    "  }" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST3 =
    "public class IfStmtsMustUseBraces3 {" + CPD.EOL +
    " public void foo() {   " + CPD.EOL +
    "  if (true) {" + CPD.EOL +
    "   if (true) bar();" + CPD.EOL +
    "  }" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty("xpath", "//IfStatement[count(*) < 3][not(Statement/Block)]");
    }

    public void testSimpleBad() throws Throwable {
        runTestFromString(TEST1, 1, rule);
    }

    public void testSimpleOK() throws Throwable {
        runTestFromString(TEST2, 0, rule);
    }

    public void testNexted() throws Throwable {
        runTestFromString(TEST3, 1, rule);
    }
}
