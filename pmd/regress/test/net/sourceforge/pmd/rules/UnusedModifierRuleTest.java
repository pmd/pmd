package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.rules.UnusedModifierRule;

public class UnusedModifierRuleTest extends RuleTst {

    public void test1() throws Throwable {
        runTest("UnusedModifier1.java", 1, new UnusedModifierRule());
    }

    public void test2() throws Throwable {
        runTest("UnusedModifier2.java", 0, new UnusedModifierRule());
    }
}
