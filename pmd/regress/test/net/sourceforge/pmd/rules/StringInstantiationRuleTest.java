package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.rules.XPathRule;

public class StringInstantiationRuleTest extends RuleTst {

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty("xpath", "//AllocationExpression[Name/@Image='String'][count(.//Expression) < 2][not(ArrayDimsAndInits)]");
    }

    public void test1() throws Throwable {
        runTestFromFile("StringInstantiation1.java", 2, rule);
    }

    public void test2() throws Throwable {
        runTestFromFile("StringInstantiation2.java", 0, rule);
    }

    public void test3() throws Throwable {
        runTestFromFile("StringInstantiation3.java", 0, rule);
    }

    public void test4() throws Throwable {
        runTestFromFile("StringInstantiation4.java", 0, rule);
    }
}
