package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.rules.CouplingBetweenObjectsRule;

public class CouplingBetweenObjectsRuleTest extends RuleTst {

    private Rule rule;

    public CouplingBetweenObjectsRuleTest() {
        super();
        rule = new CouplingBetweenObjectsRule();
        rule.addProperty("threshold", "2");
    }

    public void testSimpleBad() throws Throwable {
        super.runTestFromFile("CouplingBetweenObjects1.java", 1, rule);
    }

    public void testSimpleOK() throws Throwable {
        super.runTestFromFile("CouplingBetweenObjects2.java", 0, rule);
    }
}
