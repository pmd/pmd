package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.rules.AssignmentInOperandRule;

public class AssignmentInOperandRuleTest extends RuleTst {

    public void testSimple() throws Throwable {
        runTest("AssignmentInOperand1.java", 1, new AssignmentInOperandRule());
    }

    public void testOK() throws Throwable {
        runTest("AssignmentInOperand2.java", 0, new AssignmentInOperandRule());
    }

    public void testAssignmentInIfBody() throws Throwable {
        runTest("AssignmentInOperand3.java", 0, new AssignmentInOperandRule());
    }

    public void testAssignmentInWhileLoop() throws Throwable {
        runTest("AssignmentInOperand4.java", 1, new AssignmentInOperandRule());
    }
}
