package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.rules.XPathRule;

public class ShortMethodNameRuleTest extends RuleTst {

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty("xpath", "//MethodDeclarator[string-length(@Image) < 3]");
    }

    public void testShortMethodName0() throws Throwable {
        runTestFromFile("ShortMethodName0.java", 0, rule);
    }

    public void testShortMethodName1() throws Throwable {
        runTestFromFile("ShortMethodName1.java", 1, rule);
    }

    public void testShortMethodName2() throws Throwable {
        runTestFromFile("ShortMethodName2.java", 2, rule);
    }

    public void testShortMethodName3() throws Throwable {
        runTestFromFile("ShortMethodName3.java", 1, rule);
    }
}
