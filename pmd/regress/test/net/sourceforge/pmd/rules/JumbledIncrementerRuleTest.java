package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.rules.XPathRule;

public class JumbledIncrementerRuleTest extends RuleTst {

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty(
            "xpath",
            "//ForStatement[ForUpdate//Name/@Image = ancestor::ForStatement/ForInit//VariableDeclaratorId/@Image]");
    }

    public void test1() throws Throwable {
        runTestFromFile("JumbledIncrementerRule1.java", 1, rule);
    }

    public void test2() throws Throwable {
        runTestFromFile("JumbledIncrementerRule2.java", 0, rule);
    }

    public void test3() throws Throwable {
        runTestFromFile("JumbledIncrementerRule3.java", 0, rule);
    }
}
