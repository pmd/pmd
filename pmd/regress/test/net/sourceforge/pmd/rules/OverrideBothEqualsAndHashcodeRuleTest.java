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
        runTestFromFile("OverrideBothEqualsAndHashcode1.java", 1, rule);
    }

    public void testEqualsOnly() throws Throwable {
        runTestFromFile("OverrideBothEqualsAndHashcode2.java", 1, rule);
    }

    public void testCorrectImpl() throws Throwable {
        runTestFromFile("OverrideBothEqualsAndHashcode3.java", 0, rule);
    }

    public void testNeither() throws Throwable {
        runTestFromFile("OverrideBothEqualsAndHashcode4.java", 0, rule);
    }

    public void testEqualsSignatureUsesStringNotObject() throws Throwable {
        runTestFromFile("OverrideBothEqualsAndHashcode5.java", 1, rule);
    }

    public void testInterface() throws Throwable {
        runTestFromFile("OverrideBothEqualsAndHashcode6.java", 0, rule);
    }
}
