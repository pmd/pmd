/*
 * User: tom
 * Date: Jul 3, 2002
 * Time: 9:40:47 PM
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.rules.XPathRule;

public class OverrideBothEqualsAndHashcodeRuleTest extends RuleTst {
    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty("xpath", "//ClassDeclaration//MethodDeclarator" +
        	"[" +
        	"(" +
        	"@Image = 'equals'" +
        	" and count(FormalParameters/*) = 1" +
        	" and not(//MethodDeclarator[count(FormalParameters/*) = 0][@Image = 'hashCode'])" +
        	") or (" +
        	"@Image='hashCode'" +
        	" and count(FormalParameters/*) = 0" +
        	" and not(//MethodDeclarator[count(FormalParameters//Type/Name[@Image = 'Object']) = 1][@Image = 'equals'])" +
        	")]");
    }

    public void testHashCodeOnly() throws Throwable {
        runTest("OverrideBothEqualsAndHashcode1.java", 1, rule);
    }

    public void testEqualsOnly() throws Throwable {
        runTest("OverrideBothEqualsAndHashcode2.java", 1, rule);
    }

    public void testCorrectImpl() throws Throwable {
        runTest("OverrideBothEqualsAndHashcode3.java", 0, rule);
    }

    public void testNeither() throws Throwable {
        runTest("OverrideBothEqualsAndHashcode4.java", 0, rule);
    }

    public void testEqualsSignatureUsesStringNotObject() throws Throwable {
        runTest("OverrideBothEqualsAndHashcode5.java", 1, rule);
    }

    public void testInterface() throws Throwable {
        runTest("OverrideBothEqualsAndHashcode6.java", 0, rule);
    }
}
