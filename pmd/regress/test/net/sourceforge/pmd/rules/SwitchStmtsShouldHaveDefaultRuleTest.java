package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.rules.XPathRule;

public class SwitchStmtsShouldHaveDefaultRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty("xpath", "//SwitchStatement[not(SwitchLabel[count(*) = 0])]");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "simple failure case", 1, rule),
           new TestDescriptor(TEST2, "simple ok case", 0, rule)
       });
    }

    private static final String TEST1 =
    "public class SwitchStmtsShouldHaveDefault1 {" + PMD.EOL +
    " public void bar() {" + PMD.EOL +
    "  int x = 2;" + PMD.EOL +
    "  switch (x) {" + PMD.EOL +
    "   case 2: int y=8;" + PMD.EOL +
    "  }" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class SwitchStmtsShouldHaveDefault2 {" + PMD.EOL +
    " public void bar() {" + PMD.EOL +
    "  int x = 2;" + PMD.EOL +
    "  switch (x) {" + PMD.EOL +
    "   case 2: int y=8;" + PMD.EOL +
    "   default: int j=8;" + PMD.EOL +
    "  }" + PMD.EOL +
    " }" + PMD.EOL +
    "}";


}
