package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.rules.XPathRule;

public class ReturnFromFinallyBlockTest extends RuleTst {

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty("xpath", "//TryStatement[@Finally='true']/Block[position() = last()]//ReturnStatement");
    }

    public void testThrowExceptionButReturnFromFinally() throws Throwable {
        runTest("ReturnFromFinallyBlock1.java", 1, rule);
    }

    public void testLotsOfReturns() throws Throwable {
        runTest("ReturnFromFinallyBlock2.java", 1, rule);
    }

    public void testOK() throws Throwable {
        runTest("ReturnFromFinallyBlock3.java", 0, rule);
    }

}
