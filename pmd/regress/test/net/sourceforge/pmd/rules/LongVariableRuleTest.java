package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
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
    "public class LongVariableParam {" + PMD.EOL +
    "    public static void main(String argsWithExtraMustard[]) { " + PMD.EOL +
    "       // a should trigger it." + PMD.EOL +
    "    }" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class LongVariableNone {" + PMD.EOL +
    "    public static void main(String args[]) {" + PMD.EOL +
    "       int bugleDeWump = -1;" + PMD.EOL +
    "    }" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class LongVariableLocal {" + PMD.EOL +
    "" + PMD.EOL +
    "    public static void main(String args[]) {" + PMD.EOL +
    "       int abcdefghijklmnopqrstuvwxyz = -1; " + PMD.EOL +
    "       // Should trigger ShortVariable rule." + PMD.EOL +
    "    }" + PMD.EOL +
    "}";

    private static final String TEST4 =
    "public class LongVariableFor {" + PMD.EOL +
    "    public static void main(String args[]) {" + PMD.EOL +
    "       for (int interestingIntIterator = 0; " + PMD.EOL +
    "            interestingIntIterator < 10; " + PMD.EOL +
    "            interestingIntIterator++) { } // Should trigger." + PMD.EOL +
    "    }" + PMD.EOL +
    "}";

    private static final String TEST5 =
    "public class LongVariableField {" + PMD.EOL +
    "    private int abcdefghijklmnopqrstuvwxyz; // Should cause a problem." + PMD.EOL +
    "}";

}
