package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.rules.XPathRule;

public class ShortVariableRuleTest extends SimpleAggregatorTst {

    private static final String TEST1 =
    "public class ShortVariableParam {" + CPD.EOL +
    "    public static void main(String a[]) { // a should trigger it." + CPD.EOL +
    "    }" + CPD.EOL +
    "}";

    private static final String TEST2 =
    "public class ShortVariableNone {" + CPD.EOL +
    "    public static void main(String args[]) {" + CPD.EOL +
    "       int bugleDeWump = -1;" + CPD.EOL +
    "    }" + CPD.EOL +
    "}";

    private static final String TEST3 =
    "public class ShortVariableLocal {" + CPD.EOL +
    "" + CPD.EOL +
    "    public static void main(String args[]) {" + CPD.EOL +
    "       int ab = -1; " + CPD.EOL +
    "       // Should trigger ShortVariable rule." + CPD.EOL +
    "    }" + CPD.EOL +
    "}";

    private static final String TEST4 =
    "public class ShortVariableFor {" + CPD.EOL +
    "    public static void main(String args[]) {" + CPD.EOL +
    "       for (int i = 0; i < 10; i++) { } // Should NOT!! trigger." + CPD.EOL +
    "    }" + CPD.EOL +
    "}";

    private static final String TEST5 =
    "public class ShortVariableField {" + CPD.EOL +
    "    private int qx; // Should cause a problem." + CPD.EOL +
    "}";

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty("xpath", "//VariableDeclaratorId[string-length(@Image) < 3][not(ancestor::ForInit)]");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "param", 1, rule),
           new TestDescriptor(TEST2, "none", 0, rule),
           new TestDescriptor(TEST3, "local", 1, rule),
           new TestDescriptor(TEST4, "for", 0, rule),
           new TestDescriptor(TEST5, "field", 1, rule),
       });
    }
}
