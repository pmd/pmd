package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.rules.XPathRule;
import net.sourceforge.pmd.Rule;

public class AssignmentInOperandRuleTest extends RuleTst {

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty("xpath", "//*[name()='WhileStatement' or name()='IfStatement'][Expression//AssignmentOperator]");
    }

    public void testSimple() throws Throwable {
        runTest("AssignmentInOperand1.java", 1, rule);
    }

    public void testOK() throws Throwable {
        runTest("AssignmentInOperand2.java", 0, rule);
    }

    public void testAssignmentInIfBody() throws Throwable {
        runTest("AssignmentInOperand3.java", 0, rule);
    }

    public void testAssignmentInWhileLoop() throws Throwable {
        runTest("AssignmentInOperand4.java", 1, rule);
    }
}
