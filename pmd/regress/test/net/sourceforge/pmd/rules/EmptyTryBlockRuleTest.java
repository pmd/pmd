package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.rules.XPathRule;

public class EmptyTryBlockRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty("xpath", "//TryStatement/Block[1][count(*) = 0]");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "bad", 1, rule),
           new TestDescriptor(TEST2, "bad", 1, rule),
           new TestDescriptor(TEST3, "ok", 0, rule)
       });
    }

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

}
