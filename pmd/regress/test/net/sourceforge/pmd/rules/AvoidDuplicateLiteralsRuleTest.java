package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.rules.AvoidDuplicateLiteralsRule;

public class AvoidDuplicateLiteralsRuleTest extends RuleTst {

    private AvoidDuplicateLiteralsRule rule;

    public void setUp() {
        rule = new AvoidDuplicateLiteralsRule();
        rule.setMessage("avoid ''{0}'' and ''{1}''");
        rule.addProperty("threshold", "2");
    }

    public void testTwoLiteralStringArgs() throws Throwable {
        runTest("AvoidDuplicateLiterals1.java", 1, rule);
    }

    public void testLiteralIntArg() throws Throwable {
        runTest("AvoidDuplicateLiterals2.java", 0, rule);
    }

    public void testLiteralFieldDecl() throws Throwable {
        runTest("AvoidDuplicateLiterals3.java", 0, rule);
    }
}
