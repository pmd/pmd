package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.rules.XPathRule;

public class ForLoopShouldBeWhileLoopRuleTest extends RuleTst {

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty(
            "xpath",
            "//ForStatement[count(*) > 1][not(ForInit)][not(ForUpdate)]");
    }

    public void testSimple() throws Throwable {
        runTest("ForLoopShouldBeWhileLoop1.java", 1, rule);
    }

    public void testOK() throws Throwable {
        runTest("ForLoopShouldBeWhileLoop2.java", 0, rule);
    }

    public void testForSemicolonSemicolon() throws Throwable {
        runTest("ForLoopShouldBeWhileLoop3.java", 0, rule);
    }
}
