package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.rules.XPathRule;

public class ForLoopsMustUseBracesRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty("xpath", "//ForStatement[not(Statement/Block)]");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "simple failure case", 1, rule),
           new TestDescriptor(TEST2, "ok", 0, rule),
           new TestDescriptor(TEST3, "", 1, rule),
           new TestDescriptor(TEST4, "", 1, rule),
           new TestDescriptor(TEST5, "", 1, rule),
       });
    }

    private static final String TEST1 =
    "public class ForLoopsNeedBraces1 {" + CPD.EOL +
    " public void foo() {" + CPD.EOL +
    "  for (int i=0; i<42;i++)" + CPD.EOL +
    "       foo();" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST2 =
    "public class ForLoopsNeedBraces2 {" + CPD.EOL +
    " public void foo() {   " + CPD.EOL +
    "  for (int i=0; i<42;i++) {" + CPD.EOL +
    "       foo();" + CPD.EOL +
    "  }" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST3 =
    "public class ForLoopsNeedBraces3 {" + CPD.EOL +
    " public void foo() {   " + CPD.EOL +
    "  for (int i=0; i<42;) " + CPD.EOL +
    "       foo();" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST4 =
    "public class ForLoopsNeedBraces4 {" + CPD.EOL +
    " public void foo() {   " + CPD.EOL +
    "  for (int i=0;;) " + CPD.EOL +
    "       foo();" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST5 =
    "public class ForLoopsNeedBraces5 {" + CPD.EOL +
    " public void foo() {   " + CPD.EOL +
    "  for (;;) " + CPD.EOL +
    "       foo();" + CPD.EOL +
    " }" + CPD.EOL +
    "}";
}
