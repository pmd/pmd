package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.rules.XPathRule;

public class SimplifyBooleanExpressionsRuleTest extends RuleTst {
    private static final String TEST1 =
    "public class SimplifyBooleanExpressions1 {" + CPD.EOL +
    " private boolean foo = (isFoo() == true);" + CPD.EOL +
    " public boolean isFoo() {return foo;}" + CPD.EOL +
    "}" + CPD.EOL +
    "";

    private static final String TEST2 =
    "public class SimplifyBooleanExpressions2 {" + CPD.EOL +
    " public void foo() {" + CPD.EOL +
    "  boolean bar = (new String().length() >2) == false;" + CPD.EOL +
    " }" + CPD.EOL +
    "}" + CPD.EOL +
    "";

    private static final String TEST3 =
    "public class SimplifyBooleanExpressions3 {" + CPD.EOL +
    " boolean bar = true;" + CPD.EOL +
    "}" + CPD.EOL +
    "";

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty("xpath", "//Expression/ConditionalAndExpression/PrimaryExpression[position()>1]/PrimaryPrefix/Literal/BooleanLiteral");
    }

    public void testInFieldAssignment() throws Throwable {
        runTestFromString(TEST1, 1, rule);
    }
    public void testInMethodBody() throws Throwable {
        runTestFromString(TEST2, 1, rule);
    }
    public void testOK() throws Throwable {
        runTestFromString(TEST3, 0, rule);
    }
}
