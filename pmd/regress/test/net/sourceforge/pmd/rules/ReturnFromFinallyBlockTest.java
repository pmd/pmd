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
        runTestFromFile("ReturnFromFinallyBlock1.java", 1, rule);
    }

    public void testLotsOfReturns() throws Throwable {
        runTestFromFile("ReturnFromFinallyBlock2.java", 1, rule);
    }

    public void testOK() throws Throwable {
        runTestFromFile("ReturnFromFinallyBlock3.java", 0, rule);
    }

}
