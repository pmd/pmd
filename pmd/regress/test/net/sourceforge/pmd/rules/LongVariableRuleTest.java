package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.rules.XPathRule;

public class LongVariableRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty("xpath", "//VariableDeclaratorId[string-length(@Image) > 12]");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "param", 1, rule),
           new TestDescriptor(TEST2, "ok", 0, rule),
           new TestDescriptor(TEST3, "local", 1, rule),
           new TestDescriptor(TEST4, "field", 1, rule),
           new TestDescriptor(TEST5, "for", 1, rule),
       });
    }

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

}
