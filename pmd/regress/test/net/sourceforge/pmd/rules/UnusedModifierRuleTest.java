package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.rules.UnusedModifierRule;

public class UnusedModifierRuleTest extends RuleTst {

    public void testAbstract() throws Throwable {
        runTest("UnusedModifier1.java", 1, new UnusedModifierRule());
    }

    public void testAbstractClass() throws Throwable {
        runTest("UnusedModifier2.java", 0, new UnusedModifierRule());
    }

    public void testPublicAndAbstract() throws Throwable {
        runTest("UnusedModifier3.java", 1, new UnusedModifierRule());
    }
}
