/*
 * User: tom
 * Date: Oct 22, 2002
 * Time: 9:50:53 AM
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.rules.AvoidReassigningParametersRule;

public class AvoidReassigningParametersRuleTest extends RuleTst {

    private AvoidReassigningParametersRule rule;

    public void setUp() {
        rule = new AvoidReassigningParametersRule();
        rule.setMessage("Avoid this stuff -> ''{0}''");
    }

    public void testSimple() throws Throwable {
        runTest("AvoidReassigningParameters1.java", 1, rule);
    }

    public void testNoUsage() throws Throwable {
        runTest("AvoidReassigningParameters2.java", 0, rule);
    }

    public void testInstanceVarSameNameAsParam() throws Throwable {
        runTest("AvoidReassigningParameters3.java", 1, rule);
    }

    public void testQualifiedNameInstanceVarSameAsParam() throws Throwable {
        runTest("AvoidReassigningParameters4.java", 0, rule);
    }

    public void testQualifiedNameSameAsParam() throws Throwable {
        runTest("AvoidReassigningParameters5.java", 0, rule);
    }

    public void testAssignmentToParametersField() throws Throwable {
        runTest("AvoidReassigningParameters6.java", 0, rule);
    }
}
