package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.rules.XPathRule;

public class SimplifyBooleanExpressionsRuleTest extends RuleTst {

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty("xpath", "//Expression/PrimaryExpression[position()>1]/PrimaryPrefix/Literal/BooleanLiteral");
    }

    public void testInFieldAssignment() throws Throwable {
        runTest("SimplifyBooleanExpressions1.java", 1, rule);
    }

    public void testInMethodBody() throws Throwable {
        runTest("SimplifyBooleanExpressions2.java", 1, rule);
    }

    public void testOK() throws Throwable {
        runTest("SimplifyBooleanExpressions3.java", 0, rule);
    }
}
