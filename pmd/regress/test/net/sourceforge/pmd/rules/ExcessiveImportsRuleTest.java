package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.rules.ExcessiveImportsRule;
import net.sourceforge.pmd.Rule;

public class ExcessiveImportsRuleTest extends RuleTst {

    private Rule rule;

    public ExcessiveImportsRuleTest() {
        super();
        rule = new ExcessiveImportsRule();
        rule.addProperty("minimum", "20");
    }

    public void testSimpleBad() throws Throwable {
        super.runTest("ExcessiveImports1.java", 1, rule);
    }

    public void testSimpleOK() throws Throwable {
        super.runTest("ExcessiveImports2.java", 0, rule);
    }
}
