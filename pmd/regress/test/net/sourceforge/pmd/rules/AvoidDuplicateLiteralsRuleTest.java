/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.rules.AvoidDuplicateLiteralsRule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

import java.util.Set;

public class AvoidDuplicateLiteralsRuleTest extends SimpleAggregatorTst {

    private AvoidDuplicateLiteralsRule rule;

    public void setUp() {
        rule = new AvoidDuplicateLiteralsRule();
        rule.setMessage("avoid ''{0}'' and ''{1}''");
        rule.addProperty("threshold", "2");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "duplicate literals in argument list", 1, rule),
           new TestDescriptor(TEST2, "literal int argument, ok for now", 0, rule),
           new TestDescriptor(TEST3, "duplicate literals in field decl", 1, rule),
       });
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

    public static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    " private void bar() {" + PMD.EOL +
    "    buz(\"Howdy\");" + PMD.EOL +
    "    buz(\"Howdy\");" + PMD.EOL +
    "    buz(\"Howdy\");" + PMD.EOL +
    "    buz(\"Howdy\");" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    public static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    " private void bar() {" + PMD.EOL +
    "    buz(2);" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    public static final String TEST3 =
    "public class Foo {" + PMD.EOL +
    " String[] FOO = {\"foo\", \"foo\", \"foo\", \"foo\", \"foo\", \"foo\", \"foo\", \"foo\", \"foo\"};" + PMD.EOL +
    "}";

}
