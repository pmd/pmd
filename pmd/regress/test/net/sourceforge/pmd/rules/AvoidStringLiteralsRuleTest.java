/*
 * User: tom
 * Date: Nov 4, 2002
 * Time: 10:42:01 AM
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.rules.AvoidStringLiteralsRule;

public class AvoidStringLiteralsRuleTest extends RuleTst {

    private Rule rule;

    public void setUp() {
        rule = new AvoidStringLiteralsRule();
        rule.setMessage("Avoid this stuff -> ''{0}''");
    }
    public void testLiteralStringArg() throws Throwable {
        runTest("AvoidStringLiterals1.java", 1, rule);
    }

    public void testLiteralIntArg() throws Throwable {
        runTest("AvoidStringLiterals2.java", 0, rule);
    }

    public void testLiteralFieldDecl() throws Throwable {
        runTest("AvoidStringLiterals3.java", 0, rule);
    }
}
