package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.rules.XPathRule;

public class ShortVariableRuleTest extends RuleTst {

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

    public void testShortVariableParam() throws Throwable {
        runTestFromString(TEST1, 1, rule);
    }
    public void testShortVariableNone() throws Throwable {
        runTestFromString(TEST2, 0, rule);
    }
    public void testShortVariableLocal() throws Throwable {
        runTestFromString(TEST3, 1, rule);
    }
    public void testShortVariableFor() throws Throwable {
        runTestFromString(TEST4, 0, rule);
    }
    public void testShortVariableField() throws Throwable {
        runTestFromString(TEST5, 1, rule);
    }
}
