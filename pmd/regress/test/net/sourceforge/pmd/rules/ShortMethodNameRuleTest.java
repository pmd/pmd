package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.rules.XPathRule;

public class ShortMethodNameRuleTest extends RuleTst {

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

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty("xpath", "//MethodDeclarator[string-length(@Image) < 3]");
    }

    public void testShortMethodName0() throws Throwable {
        runTestFromString(TEST1, 0, rule);
    }

    public void testShortMethodName1() throws Throwable {
        runTestFromString(TEST2, 1, rule);
    }

    public void testShortMethodName2() throws Throwable {
        runTestFromString(TEST3, 2, rule);
    }

    public void testShortMethodName3() throws Throwable {
        runTestFromString(TEST4, 1, rule);
    }
}
