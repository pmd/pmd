package test.net.sourceforge.pmd.rules.junit;

import net.sourceforge.pmd.rules.junit.JUnitAssertionsShouldIncludeMessageRule;
import net.sourceforge.pmd.cpd.CPD;
import test.net.sourceforge.pmd.rules.RuleTst;

public class JUnitAssertionsShouldIncludeMessageRuleTest extends RuleTst {

    public void testAssertEqualsOK() throws Throwable {
        runTestFromString(TEST1, 0, new JUnitAssertionsShouldIncludeMessageRule());
    }
    public void testAssertEqualsBad() throws Throwable {
        runTestFromString(TEST2, 1, new JUnitAssertionsShouldIncludeMessageRule());
    }
    public void testAssertTrueOK() throws Throwable {
        runTestFromString(TEST3, 0, new JUnitAssertionsShouldIncludeMessageRule());
    }
    public void testAssertTrueBad() throws Throwable {
        runTestFromString(TEST4, 1, new JUnitAssertionsShouldIncludeMessageRule());
    }
    public void testAssertNullOK() throws Throwable {
        runTestFromString(TEST5, 0, new JUnitAssertionsShouldIncludeMessageRule());
    }
    public void testAssertNullBad() throws Throwable {
        runTestFromString(TEST6, 1, new JUnitAssertionsShouldIncludeMessageRule());
    }
    public void testAssertSameOK() throws Throwable {
        runTestFromString(TEST7, 0, new JUnitAssertionsShouldIncludeMessageRule());
    }
    public void testAssertSameBad() throws Throwable {
        runTestFromString(TEST8, 1, new JUnitAssertionsShouldIncludeMessageRule());
    }
    public void testAssertNotNullOK() throws Throwable {
        runTestFromString(TEST9, 0, new JUnitAssertionsShouldIncludeMessageRule());
    }
    public void testAssertNotNullBad() throws Throwable {
        runTestFromString(TEST10, 1, new JUnitAssertionsShouldIncludeMessageRule());
    }
    public void testFindBug() throws Throwable {
        runTestFromString(TEST11, 0, new JUnitAssertionsShouldIncludeMessageRule());
    }

    private static final String TEST1 =
    "public class JUnitAssertionsShouldIncludeMessageRule1 {" + CPD.EOL +
    " public void test1() {" + CPD.EOL +
    "  assertEquals(\"1 == 1\", 1, 1);" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST2 =
    "public class JUnitAssertionsShouldIncludeMessageRule2 {" + CPD.EOL +
    " public void test1() {" + CPD.EOL +
    "  assertEquals(1, 1);" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST3 =
    "public class JUnitAssertionsShouldIncludeMessageRule3 {" + CPD.EOL +
    " public void test1() {" + CPD.EOL +
    "  assertTrue(\"foo\", \"foo\".equals(\"foo\"));" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST4 =
    "public class JUnitAssertionsShouldIncludeMessageRule4 {" + CPD.EOL +
    " public void test1() {" + CPD.EOL +
    "  assertTrue(\"foo\".equals(\"foo\"));" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST5 =
    "public class JUnitAssertionsShouldIncludeMessageRule5 {" + CPD.EOL +
    " public void test1() {" + CPD.EOL +
    "  assertNull(\"it's not null\", null);" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST6 =
    "public class JUnitAssertionsShouldIncludeMessageRule6 {" + CPD.EOL +
    " public void test1() {" + CPD.EOL +
    "  assertNull(null);" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST7 =
    "public class JUnitAssertionsShouldIncludeMessageRule7 {" + CPD.EOL +
    " public void test1() {" + CPD.EOL +
    "  assertSame(\"not same!\", null, null);" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST8 =
    "public class JUnitAssertionsShouldIncludeMessageRule8 {" + CPD.EOL +
    " public void test1() {" + CPD.EOL +
    "  assertSame(null, null);" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST9 =
    "public class JUnitAssertionsShouldIncludeMessageRule9 {" + CPD.EOL +
    " public void test1() {" + CPD.EOL +
    "  assertNotNull(\"foo\", null);" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST10 =
    "public class JUnitAssertionsShouldIncludeMessageRule10 {" + CPD.EOL +
    " public void test1() {" + CPD.EOL +
    "  assertNotNull(null);" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST11 =
    "public class JUnitAssertionsShouldIncludeMessageRule11 {" + CPD.EOL +
    " public void test1() {" + CPD.EOL +
    "  this.test1(\"foo\");" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

}
