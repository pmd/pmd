package test.net.sourceforge.pmd.rules.junit;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.rules.XPathRule;
import test.net.sourceforge.pmd.rules.RuleTst;

public class JUnitSpellingRuleTest extends RuleTst {

    private static final String TEST1 =
    "public class JUnitSpelling1 {" + PMD.EOL +
    " // these should be 'setUp'" + PMD.EOL +
    " public void setup() {}" + PMD.EOL +
    " public void SetUp() {}" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class JUnitSpelling2 {" + PMD.EOL +
    " // these should be 'tearDown'" + PMD.EOL +
    " public void TearDown() {}" + PMD.EOL +
    " public void teardown() {}" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class JUnitSpelling3 {" + PMD.EOL +
    " // these are OK" + PMD.EOL +
    " public void setUp() {}" + PMD.EOL +
    " public void tearDown() {}" + PMD.EOL +
    "}";

    private static final String TEST4 =
    "public class JUnitSpelling4 {" + PMD.EOL +
    " // these are OK" + PMD.EOL +
    " public void utility() {}" + PMD.EOL +
    " public void foobr() {}" + PMD.EOL +
    "}";

    private static final String TEST5 =
    "public class JUnitSpelling5 {" + PMD.EOL +
    " public void setup(String x) {}" + PMD.EOL +
    "}";

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty("xpath", "//MethodDeclarator[(not(@Image = 'setUp') and translate(@Image, 'SETuP', 'setUp') = 'setUp') or (not(@Image = 'tearDown') and translate(@Image, 'TEARdOWN', 'tearDown') = 'tearDown')][FormalParameters[count(*) = 0]]");
    }

    public void testSetupMisspellings1() throws Throwable {
        runTestFromString(TEST1, 2, rule);
    }
    public void testTeardownMisspellings() throws Throwable {
        runTestFromString(TEST2, 2, rule);
    }
    public void testMethodsSpelledOK() throws Throwable {
        runTestFromString(TEST3, 0, rule);
    }
    public void testUnrelatedMethods() throws Throwable {
        runTestFromString(TEST4, 0, rule);
    }
    public void testMethodWithParams() throws Throwable {
        runTestFromString(TEST5, 0, rule);
    }
}
