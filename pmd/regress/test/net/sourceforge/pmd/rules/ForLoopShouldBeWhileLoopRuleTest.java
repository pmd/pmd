package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.rules.ForLoopShouldBeWhileLoopRule;

public class ForLoopShouldBeWhileLoopRuleTest extends RuleTst {

    public void testSimple() throws Throwable {
        super.runTest("ForLoopShouldBeWhileLoop1.java", 1, new ForLoopShouldBeWhileLoopRule());
    }

    public void testOK() throws Throwable {
        super.runTest("ForLoopShouldBeWhileLoop2.java", 0, new ForLoopShouldBeWhileLoopRule());
    }

    public void testForSemicolonSemicolon() throws Throwable {
        super.runTest("ForLoopShouldBeWhileLoop3.java", 0, new ForLoopShouldBeWhileLoopRule());
    }
}
