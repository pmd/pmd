package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.rules.XPathRule;

public class ForLoopShouldBeWhileLoopRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty(
            "xpath",
            "//ForStatement[count(*) > 1][not(ForInit)][not(ForUpdate)]");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "simple failure case", 1, rule),
           new TestDescriptor(TEST2, "ok", 0, rule),
           new TestDescriptor(TEST3, "for loop like this: for (;;) {} ", 0, rule),
       });
    }

    private static final String TEST1 =
    "public class ForLoopShouldBeWhileLoop1 {" + PMD.EOL +
    " public void foo() {" + PMD.EOL +
    "  int x = 2;" + PMD.EOL +
    "  for (;x<5;) { " + PMD.EOL +
    "   x++;" + PMD.EOL +
    "  }" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class ForLoopShouldBeWhileLoop2 {" + PMD.EOL +
    " public void foo() {" + PMD.EOL +
    "  for (int x=2;x<5;) { " + PMD.EOL +
    "   x++;" + PMD.EOL +
    "  }" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class ForLoopShouldBeWhileLoop3 {" + PMD.EOL +
    " public void foo() {" + PMD.EOL +
    "  for (;;) {}" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

}
