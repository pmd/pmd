/*
 * User: tom
 * Date: Jul 3, 2002
 * Time: 9:40:47 PM
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.rules.OverrideBothEqualsAndHashcodeRule;

public class OverrideBothEqualsAndHashcodeRuleTest extends RuleTst {
    public void testHashCodeOnly() throws Throwable{
        runTest("OverrideBothEqualsAndHashcode1.java", 1, new OverrideBothEqualsAndHashcodeRule());
    }
    public void testEqualsOnly() throws Throwable{
        runTest("OverrideBothEqualsAndHashcode2.java", 1, new OverrideBothEqualsAndHashcodeRule());
    }
    public void testCorrectImpl() throws Throwable{
        runTest("OverrideBothEqualsAndHashcode3.java", 0, new OverrideBothEqualsAndHashcodeRule());
    }
    public void testNeither() throws Throwable{
        runTest("OverrideBothEqualsAndHashcode4.java", 0, new OverrideBothEqualsAndHashcodeRule());
    }
    public void testEqualsSignatureUsesStringNotObject() throws Throwable{
        runTest("OverrideBothEqualsAndHashcode5.java", 1, new OverrideBothEqualsAndHashcodeRule());
    }
    public void testInterface() throws Throwable{
        runTest("OverrideBothEqualsAndHashcode6.java", 0, new OverrideBothEqualsAndHashcodeRule());
    }
}
