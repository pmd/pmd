package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.rules.AvoidDuplicateLiteralsRule;
import net.sourceforge.pmd.cpd.CPD;

public class AvoidDuplicateLiteralsRuleTest extends RuleTst {

    public static final String TEST1 =
    "public class Foo {" + CPD.EOL +
    " private void bar() {" + CPD.EOL +
    "    buz(\"Howdy\");" + CPD.EOL +
    "    buz(\"Howdy\");" + CPD.EOL +
    "    buz(\"Howdy\");" + CPD.EOL +
    "    buz(\"Howdy\");" + CPD.EOL +
    " }" + CPD.EOL +
    " private void buz(String x) {}" + CPD.EOL +
    "}";

    public static final String TEST2 =
    "public class Foo {" + CPD.EOL +
    " private void bar() {" + CPD.EOL +
    "    buz(2);" + CPD.EOL +
    " }" + CPD.EOL +
    " private void buz(int x) {}" + CPD.EOL +
    "}";

    public static final String TEST3 =
    "public class Foo {" + CPD.EOL +
    " private static final String FOO = \"foo\";" + CPD.EOL +
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
}
