package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.rules.ExcessivePublicCountRule;

public class ExcessivePublicCountRuleTest extends RuleTst {

    private ExcessivePublicCountRule rule;

    public ExcessivePublicCountRuleTest() {
        rule = new ExcessivePublicCountRule();
    }

    public void testSimpleOK() throws Throwable {
        rule.addProperty("minimum", "50");
        super.runTestFromFile("ExcessivePublicCountRule1.java", 0, rule);
    }

    public void testSimpleBad() throws Throwable {
        rule.addProperty("minimum", "2");
        super.runTestFromFile("ExcessivePublicCountRule2.java", 1, rule);
    }
}
