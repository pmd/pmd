package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.rules.XPathRule;

public class LongVariableRuleTest extends RuleTst {

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty("xpath", "//VariableDeclaratorId[string-length(@Image) > 12]");
    }

    public void testLongVariableField() throws Throwable {
        runTestFromFile("LongVariableField.java", 1, rule);
    }

    public void testLongVariableLocal() throws Throwable {
        runTestFromFile("LongVariableLocal.java", 1, rule);
    }

    public void testLongVariableFor() throws Throwable {
        runTestFromFile("LongVariableFor.java", 1, rule);
    }

    public void testLongVariableParam() throws Throwable {
        runTestFromFile("LongVariableParam.java", 1, rule);
    }

    public void testLongVariableNone() throws Throwable {
        runTestFromFile("LongVariableNone.java", 0, rule);
    }
}
