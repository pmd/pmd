package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.rules.XPathRule;

public class IfStmtsMustUseBracesRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty("xpath", "//IfStatement[count(*) < 3][not(Statement/Block)]");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "simple failure case", 1, rule),
           new TestDescriptor(TEST2, "ok", 0, rule),
           new TestDescriptor(TEST3, "nested ifs", 1, rule),
       });
    }

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

}
