package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.rules.XPathRule;

public class LongVariableRuleTest extends RuleTst {

    private static final String TEST1 =
    "public class LongVariableParam {" + CPD.EOL +
    "    public static void main(String argsWithExtraMustard[]) { " + CPD.EOL +
    "       // a should trigger it." + CPD.EOL +
    "    }" + CPD.EOL +
    "}";

    private static final String TEST2 =
    "public class LongVariableNone {" + CPD.EOL +
    "    public static void main(String args[]) {" + CPD.EOL +
    "       int bugleDeWump = -1;" + CPD.EOL +
    "    }" + CPD.EOL +
    "}";

    private static final String TEST3 =
    "public class LongVariableLocal {" + CPD.EOL +
    "" + CPD.EOL +
    "    public static void main(String args[]) {" + CPD.EOL +
    "       int abcdefghijklmnopqrstuvwxyz = -1; " + CPD.EOL +
    "       // Should trigger ShortVariable rule." + CPD.EOL +
    "    }" + CPD.EOL +
    "}";

    private static final String TEST4 =
    "public class LongVariableFor {" + CPD.EOL +
    "    public static void main(String args[]) {" + CPD.EOL +
    "       for (int interestingIntIterator = 0; " + CPD.EOL +
    "            interestingIntIterator < 10; " + CPD.EOL +
    "            interestingIntIterator++) { } // Should trigger." + CPD.EOL +
    "    }" + CPD.EOL +
    "}";

    private static final String TEST5 =
    "public class LongVariableField {" + CPD.EOL +
    "    private int abcdefghijklmnopqrstuvwxyz; // Should cause a problem." + CPD.EOL +
    "}";

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty("xpath", "//VariableDeclaratorId[string-length(@Image) > 12]");
    }

    public void testLongVariableParam() throws Throwable {
        runTestFromString(TEST1, 1, rule);
    }
    public void testLongVariableNone() throws Throwable {
        runTestFromString(TEST2, 0, rule);
    }
    public void testLongVariableLocal() throws Throwable {
        runTestFromString(TEST3, 1, rule);
    }
    public void testLongVariableField() throws Throwable {
        runTestFromString(TEST4, 1, rule);
    }
    public void testLongVariableFor() throws Throwable {
        runTestFromString(TEST5, 1, rule);
    }
}
