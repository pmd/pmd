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
        runTest("LongVariableField.java", 1, rule);
    }

    public void testLongVariableLocal() throws Throwable {
        runTest("LongVariableLocal.java", 1, rule);
    }

    public void testLongVariableFor() throws Throwable {
        runTest("LongVariableFor.java", 1, rule);
    }

    public void testLongVariableParam() throws Throwable {
        runTest("LongVariableParam.java", 1, rule);
    }

    public void testLongVariableNone() throws Throwable {
        runTest("LongVariableNone.java", 0, rule);
    }
}
