package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.rules.AvoidDuplicateLiteralsRule;

import java.util.Set;

public class AvoidDuplicateLiteralsRuleTest extends RuleTst {

    public static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    " private void bar() {" + PMD.EOL +
    "    buz(\"Howdy\");" + PMD.EOL +
    "    buz(\"Howdy\");" + PMD.EOL +
    "    buz(\"Howdy\");" + PMD.EOL +
    "    buz(\"Howdy\");" + PMD.EOL +
    " }" + PMD.EOL +
    " private void buz(String x) {}" + PMD.EOL +
    "}";

    public static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    " private void bar() {" + PMD.EOL +
    "    buz(2);" + PMD.EOL +
    " }" + PMD.EOL +
    " private void buz(int x) {}" + PMD.EOL +
    "}";

    public static final String TEST3 =
    "public class Foo {" + PMD.EOL +
    " private static final String FOO = \"foo\";" + PMD.EOL +
    "}";

    private AvoidDuplicateLiteralsRule rule;

    public void setUp() {
        rule = new AvoidDuplicateLiteralsRule();
        rule.setMessage("avoid ''{0}'' and ''{1}''");
        rule.addProperty("threshold", "2");
    }

    public void testTwoLiteralStringArgs() throws Throwable {
        runTestFromString(TEST1, 1, rule);
    }

    public void testLiteralIntArg() throws Throwable {
        runTestFromString(TEST2, 0, rule);
    }

    public void testLiteralFieldDecl() throws Throwable {
        runTestFromString(TEST3, 0, rule);
    }

    public void testStringParserEmptyString() {
        AvoidDuplicateLiteralsRule.ExceptionParser p = new AvoidDuplicateLiteralsRule.ExceptionParser(',');
        Set res = p.parse("");
        assertTrue(res.isEmpty());
    }
    public void testStringParserSimple() {
        AvoidDuplicateLiteralsRule.ExceptionParser p = new AvoidDuplicateLiteralsRule.ExceptionParser(',');
        Set res = p.parse("a,b,c");
        assertEquals(3, res.size());
        assertTrue(res.contains("a"));
        assertTrue(res.contains("b"));
        assertTrue(res.contains("c"));
    }
    public void testStringParserEscapedChar() {
        AvoidDuplicateLiteralsRule.ExceptionParser p = new AvoidDuplicateLiteralsRule.ExceptionParser(',');
        Set res = p.parse("a,b,\\,");
        assertEquals(3, res.size());
        assertTrue(res.contains("a"));
        assertTrue(res.contains("b"));
        assertTrue(res.contains(","));
    }
    public void testStringParserEscapedEscapedChar() {
        AvoidDuplicateLiteralsRule.ExceptionParser p = new AvoidDuplicateLiteralsRule.ExceptionParser(',');
        Set res = p.parse("a,b,\\\\");
        assertEquals(3, res.size());
        assertTrue(res.contains("a"));
        assertTrue(res.contains("b"));
        assertTrue(res.contains("\\"));
    }
}
