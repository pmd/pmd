package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.cpd.CPD;
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
    "public class JumbledIncrementerRule1 {" + CPD.EOL +
    " public void foo() { " + CPD.EOL +
    "  for (int i = 0; i < 10; i++) { " + CPD.EOL +
    "   for (int k = 0; k < 20; i++) { " + CPD.EOL +
    "    System.out.println(\"Hello\"); " + CPD.EOL +
    "   } " + CPD.EOL +
    "  } " + CPD.EOL +
    " } " + CPD.EOL +
    "}";

    private static final String TEST2 =
    "public class JumbledIncrementerRule2 {" + CPD.EOL +
    " public void foo() { " + CPD.EOL +
    "  for (int i = 0; i < 10; i++) { " + CPD.EOL +
    "   for (int k = 0; k < 20; k++) { " + CPD.EOL +
    "    System.out.println(\"Hello\"); " + CPD.EOL +
    "   } " + CPD.EOL +
    "  } " + CPD.EOL +
    " } " + CPD.EOL +
    "}";

    private static final String TEST3 =
    "public class JumbledIncrementerRule3 {" + CPD.EOL +
    " public void foo() { " + CPD.EOL +
    "  for (int i=0; i<5; ) {" + CPD.EOL +
    "   i++;" + CPD.EOL +
    "  }" + CPD.EOL +
    "  for (int i=0;;) {" + CPD.EOL +
    "   if (i<5) {" + CPD.EOL +
    "    break;" + CPD.EOL +
    "   }" + CPD.EOL +
    "   i++;" + CPD.EOL +
    "  }" + CPD.EOL +
    "  for (;;) {" + CPD.EOL +
    "   int x =5;" + CPD.EOL +
    "  }" + CPD.EOL +
    "  for (int i=0; i<5;i++) ;" + CPD.EOL +
    "  for (int i=0; i<5;i++) " + CPD.EOL +
    "   foo();" + CPD.EOL +
    " } " + CPD.EOL +
    "}";

}
