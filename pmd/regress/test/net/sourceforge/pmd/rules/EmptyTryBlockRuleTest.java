package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.rules.XPathRule;

public class EmptyTryBlockRuleTest extends RuleTst {

    private static final String TEST1 =
    "public class EmptyTryBlock1 {" + CPD.EOL +
    "       public void foo() {" + CPD.EOL +
    "               try {" + CPD.EOL +
    "               } catch (Exception e) {" + CPD.EOL +
    "                       e.printStackTrace();" + CPD.EOL +
    "               }" + CPD.EOL +
    "       }" + CPD.EOL +
    "}";

    private static final String TEST2 =
    "public class EmptyTryBlock2 {" + CPD.EOL +
    "       public void foo() {" + CPD.EOL +
    "               try {" + CPD.EOL +
    "               } finally {" + CPD.EOL +
    "                       int x = 5;" + CPD.EOL +
    "               }" + CPD.EOL +
    "       }" + CPD.EOL +
    "}";

    private static final String TEST3 =
    "public class EmptyTryBlock3 {" + CPD.EOL +
    "       public void foo() {" + CPD.EOL +
    "               try {" + CPD.EOL +
    "                       int f =2;" + CPD.EOL +
    "               } finally {" + CPD.EOL +
    "                       int x = 5;" + CPD.EOL +
    "               }" + CPD.EOL +
    "       }" + CPD.EOL +
    "}";

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty("xpath", "//TryStatement/Block[1][count(*) = 0]");
    }

    public void testEmptyTryBlock1() throws Throwable {
        runTestFromString(TEST1, 1, rule);
    }
    public void testEmptyTryBlock2() throws Throwable {
        runTestFromString(TEST2, 1, rule);
    }
    public void testEmptyTryBlock3() throws Throwable {
        runTestFromString(TEST3, 0, rule);
    }

}
