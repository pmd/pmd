package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.rules.XPathRule;

public class EmptyFinallyBlockRuleTest extends RuleTst {

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty(
            "xpath",
            "//TryStatement[@Finally='true']/Block[position() = last()][count(*) = 0]");
    }

    public void testEmptyFinallyBlock1() throws Throwable {
        runTestFromFile("EmptyFinallyBlock1.java", 1, rule);
    }

    public void testEmptyFinallyBlock2() throws Throwable {
        runTestFromFile("EmptyFinallyBlock2.java", 1, rule);
    }

    public void testEmptyFinallyBlock3() throws Throwable {
        runTestFromFile("EmptyFinallyBlock3.java", 0, rule);
    }

    public void testMultipleCatchBlocksWithFinally() throws Throwable {
        runTestFromFile("EmptyFinallyBlock4.java", 1, rule);
    }
}
