package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.rules.XPathRule;

public class ShortVariableRuleTest extends RuleTst {

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty("xpath", "//VariableDeclaratorId[string-length(@Image) < 3][not(ancestor::ForInit)]");
    }

    public void testShortVariableField() throws Throwable {
        runTest("ShortVariableField.java", 1, rule);
    }

    public void testShortVariableLocal() throws Throwable {
        runTest("ShortVariableLocal.java", 1, rule);
    }

    public void testShortVariableFor() throws Throwable {
        runTest("ShortVariableFor.java", 0, rule);
    }

    public void testShortVariableParam() throws Throwable {
        runTest("ShortVariableParam.java", 1, rule);
    }

    public void testShortVariableNone() throws Throwable {
        runTest("ShortVariableNone.java", 0, rule);
    }
}
