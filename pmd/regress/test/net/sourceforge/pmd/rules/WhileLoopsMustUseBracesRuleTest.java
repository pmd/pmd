package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.rules.XPathRule;

public class WhileLoopsMustUseBracesRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty("xpath", "//WhileStatement[not(Statement/Block)]");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "no braces", 1, rule),
           new TestDescriptor(TEST2, "with braces", 0, rule),
       });
    }

    private static final String TEST1 =
    "public class WhileLoopsNeedBraces1 {" + PMD.EOL +
    " public void foo() {   " + PMD.EOL +
    "  int x =0;" + PMD.EOL +
    "  while (true)" + PMD.EOL +
    "   x++;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class WhileLoopsNeedBraces2 {" + PMD.EOL +
    " public void foo() {   " + PMD.EOL +
    "  int x =0;" + PMD.EOL +
    "  while (true) {" + PMD.EOL +
    "   x++;" + PMD.EOL +
    "  }" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

}
