package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.rules.XPathRule;

public class SimplifyBooleanExpressionsRuleTest extends RuleTst {
    private static final String TEST1 =
    "public class SimplifyBooleanExpressions1 {" + PMD.EOL +
    " private boolean foo = (isFoo() == true);" + PMD.EOL +
    " public boolean isFoo() {return foo;}" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class SimplifyBooleanExpressions2 {" + PMD.EOL +
    " public void foo() {" + PMD.EOL +
    "  boolean bar = (new String().length() >2) == false;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class SimplifyBooleanExpressions3 {" + PMD.EOL +
    " boolean bar = true;" + PMD.EOL +
    "}";

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty("xpath", "//Expression/ConditionalAndExpression/InstanceOfExpression[position()>1]/PrimaryExpression/PrimaryPrefix/Literal/BooleanLiteral");
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
