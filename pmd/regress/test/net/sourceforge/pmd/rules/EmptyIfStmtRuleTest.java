package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.rules.XPathRule;

public class EmptyIfStmtRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty("xpath", "//IfStatement/Statement/Block[count(*) = 0]");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "one empty, one not empty", 1, rule),
       });
    }

    private static final String TEST1 =
    "public class EmptyIfStmtRule {" + PMD.EOL +
    "    public EmptyIfStmtRule() {" + PMD.EOL +
    "       if (null == null) {" + PMD.EOL +
    "       }" + PMD.EOL +
    "       if (null != null) {" + PMD.EOL +
    "               this.toString();" + PMD.EOL +
    "       }" + PMD.EOL +
    "    }" + PMD.EOL +
    "}";


}
