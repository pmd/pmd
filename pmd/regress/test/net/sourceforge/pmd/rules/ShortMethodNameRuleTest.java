package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.rules.XPathRule;

public class ShortMethodNameRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty("xpath", "//MethodDeclarator[string-length(@Image) < 3]");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "ok", 0, rule),
           new TestDescriptor(TEST2, "bad", 1, rule),
           new TestDescriptor(TEST3, "2 violations", 2, rule),
           new TestDescriptor(TEST4, "2 methods, 1 violation", 1, rule),
       });
    }

    private static final String TEST1 =
    "public class ShortMethodName0 {" + CPD.EOL +
    "    public int abcd( int i ) {" + CPD.EOL +
    "       // Should not violate." + CPD.EOL +
    "    }" + CPD.EOL +
    "}";

    private static final String TEST2 =
    "public class ShortMethodName1 {" + CPD.EOL +
    "    public int a( int i ) {" + CPD.EOL +
    "       // Should violate." + CPD.EOL +
    "    }" + CPD.EOL +
    "}";

    private static final String TEST3 =
    "public class ShortMethodName2 {" + CPD.EOL +
    "    public int a( int i ) {" + CPD.EOL +
    "       // Should violate" + CPD.EOL +
    "    }" + CPD.EOL +
    "" + CPD.EOL +
    "    public int b( int i ) {" + CPD.EOL +
    "       // Should violate" + CPD.EOL +
    "    }" + CPD.EOL +
    "}";

    private static final String TEST4 =
    "public class ShortMethodName3 {" + CPD.EOL +
    "    public int a( int i ) {" + CPD.EOL +
    "       // Should violate" + CPD.EOL +
    "    }" + CPD.EOL +
    "" + CPD.EOL +
    "    public int bcde( int i ) {" + CPD.EOL +
    "       // Should not violate" + CPD.EOL +
    "    }" + CPD.EOL +
    "}";

}
