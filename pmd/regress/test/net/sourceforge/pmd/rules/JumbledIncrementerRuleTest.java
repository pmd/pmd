package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.rules.XPathRule;

public class JumbledIncrementerRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty(
            "xpath",
            "//ForStatement[ForUpdate//Name/@Image = ancestor::ForStatement/ForInit//VariableDeclaratorId/@Image]");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "", 1, rule),
           new TestDescriptor(TEST2, "", 0, rule),
           new TestDescriptor(TEST3, "", 0, rule),
       });
    }

    private static final String TEST1 =
    "public class JumbledIncrementerRule1 {" + PMD.EOL +
    " public void foo() { " + PMD.EOL +
    "  for (int i = 0; i < 10; i++) { " + PMD.EOL +
    "   for (int k = 0; k < 20; i++) { " + PMD.EOL +
    "    System.out.println(\"Hello\"); " + PMD.EOL +
    "   } " + PMD.EOL +
    "  } " + PMD.EOL +
    " } " + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class JumbledIncrementerRule2 {" + PMD.EOL +
    " public void foo() { " + PMD.EOL +
    "  for (int i = 0; i < 10; i++) { " + PMD.EOL +
    "   for (int k = 0; k < 20; k++) { " + PMD.EOL +
    "    System.out.println(\"Hello\"); " + PMD.EOL +
    "   } " + PMD.EOL +
    "  } " + PMD.EOL +
    " } " + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class JumbledIncrementerRule3 {" + PMD.EOL +
    " public void foo() { " + PMD.EOL +
    "  for (int i=0; i<5; ) {" + PMD.EOL +
    "   i++;" + PMD.EOL +
    "  }" + PMD.EOL +
    "  for (int i=0;;) {" + PMD.EOL +
    "   if (i<5) {" + PMD.EOL +
    "    break;" + PMD.EOL +
    "   }" + PMD.EOL +
    "   i++;" + PMD.EOL +
    "  }" + PMD.EOL +
    "  for (;;) {" + PMD.EOL +
    "   int x =5;" + PMD.EOL +
    "  }" + PMD.EOL +
    "  for (int i=0; i<5;i++) ;" + PMD.EOL +
    "  for (int i=0; i<5;i++) " + PMD.EOL +
    "   foo();" + PMD.EOL +
    " } " + PMD.EOL +
    "}";

}
