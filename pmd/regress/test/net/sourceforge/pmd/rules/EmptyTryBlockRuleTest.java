package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
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
    "public class EmptyTryBlock1 {" + PMD.EOL +
    "       public void foo() {" + PMD.EOL +
    "               try {" + PMD.EOL +
    "               } catch (Exception e) {" + PMD.EOL +
    "                       e.printStackTrace();" + PMD.EOL +
    "               }" + PMD.EOL +
    "       }" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class EmptyTryBlock2 {" + PMD.EOL +
    "       public void foo() {" + PMD.EOL +
    "               try {" + PMD.EOL +
    "               } finally {" + PMD.EOL +
    "                       int x = 5;" + PMD.EOL +
    "               }" + PMD.EOL +
    "       }" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class EmptyTryBlock3 {" + PMD.EOL +
    "       public void foo() {" + PMD.EOL +
    "               try {" + PMD.EOL +
    "                       int f =2;" + PMD.EOL +
    "               } finally {" + PMD.EOL +
    "                       int x = 5;" + PMD.EOL +
    "               }" + PMD.EOL +
    "       }" + PMD.EOL +
    "}";

}
