/*
 * User: tom
 * Date: Nov 4, 2002
 * Time: 10:42:01 AM
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.rules.AvoidStringLiteralsRule;

public class AvoidStringLiteralsRuleTest extends RuleTst {

    public void testLiteralStringArg() throws Throwable {
        runTest("AvoidStringLiterals1.java", 1, new AvoidStringLiteralsRule());
    }

    public void testLiteralIntArg() throws Throwable {
        runTest("AvoidStringLiterals2.java", 0, new AvoidStringLiteralsRule());
    }

    public void testLiteralFieldDecl() throws Throwable {
        runTest("AvoidStringLiterals3.java", 0, new AvoidStringLiteralsRule());
    }
}
