package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.rules.XPathRule;

public class ShortVariableRuleTest extends RuleTst {

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty("xpath", "//VariableDeclaratorId[string-length(@Image) < 3][not(ancestor::ForInit)]");
    }

    public void testShortVariableField() throws Throwable {
        runTestFromFile("ShortVariableField.java", 1, rule);
    }

    public void testShortVariableLocal() throws Throwable {
        runTestFromFile("ShortVariableLocal.java", 1, rule);
    }

    public void testShortVariableFor() throws Throwable {
        runTestFromFile("ShortVariableFor.java", 0, rule);
    }

    public void testShortVariableParam() throws Throwable {
        runTestFromFile("ShortVariableParam.java", 1, rule);
    }

    public void testShortVariableNone() throws Throwable {
        runTestFromFile("ShortVariableNone.java", 0, rule);
    }
}
