package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.rules.AvoidDeeplyNestedIfStmtsRule;

public class AvoidDeeplyNestedIfStmtsRuleTest extends RuleTst {

    private Rule rule;

    public void setUp() {
        rule = new AvoidDeeplyNestedIfStmtsRule();
        rule.addProperty("problemDepth", "3");
    }

    public void test1() throws Throwable {
        runTestFromFile("AvoidDeeplyNestedIfStmtsRule1.java", 1, rule);
    }

    public void test2() throws Throwable {
        runTestFromFile("AvoidDeeplyNestedIfStmtsRule2.java", 0, rule);
    }
}
