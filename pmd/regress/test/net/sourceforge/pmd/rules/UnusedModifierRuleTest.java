package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.rules.XPathRule;

public class UnusedModifierRuleTest extends RuleTst {

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty("xpath", "//InterfaceDeclaration//MethodDeclaration[@Public='true' or @Abstract = 'true']");
    }

    public void testAbstract() throws Throwable {
        runTestFromFile("UnusedModifier1.java", 1, rule);
    }

    public void testAbstractClass() throws Throwable {
        runTestFromFile("UnusedModifier2.java", 0, rule);
    }

    public void testPublicAndAbstract() throws Throwable {
        runTestFromFile("UnusedModifier3.java", 1, rule);
    }
}
