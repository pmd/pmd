package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.rules.XPathRule;

public class IfElseStmtsMustUseBracesRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty("xpath", "//IfStatement[count(*) > 2][not(Statement/Block)]");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "simple failure case", 1, rule),
           new TestDescriptor(TEST2, "ok", 0, rule),
       });
    }

    private static final String TEST1 =
    "public class IfElseStmtsNeedBraces1 {" + PMD.EOL +
    "       public void foo() {     " + PMD.EOL +
    "               int x =0;" + PMD.EOL +
    "               if (true == true) " + PMD.EOL +
    "                       x=2;" + PMD.EOL +
    "                else " + PMD.EOL +
    "                       x=4;" + PMD.EOL +
    "               " + PMD.EOL +
    "       }" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class IfElseStmtsNeedBraces2 {" + PMD.EOL +
    "       public void foo() {     " + PMD.EOL +
    "               int x =0;" + PMD.EOL +
    "               if (true == true) {" + PMD.EOL +
    "                       x=2;" + PMD.EOL +
    "               } else {" + PMD.EOL +
    "                       x=4;" + PMD.EOL +
    "               }" + PMD.EOL +
    "       }" + PMD.EOL +
    "}";



}
