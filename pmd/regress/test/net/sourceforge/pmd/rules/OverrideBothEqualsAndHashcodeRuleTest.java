/*
 * User: tom
 * Date: Jul 3, 2002
 * Time: 9:40:47 PM
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.rules.OverrideBothEqualsAndHashcodeRule;

public class OverrideBothEqualsAndHashcodeRuleTest extends RuleTst {
    public void test1() throws Throwable{
        runTest("OverrideBothEqualsAndHashcode1.java", 1, new OverrideBothEqualsAndHashcodeRule());
    }
    public void test2() throws Throwable{
        runTest("OverrideBothEqualsAndHashcode2.java", 1, new OverrideBothEqualsAndHashcodeRule());
    }
    public void test3() throws Throwable{
        runTest("OverrideBothEqualsAndHashcode3.java", 0, new OverrideBothEqualsAndHashcodeRule());
    }
    public void test4() throws Throwable{
        runTest("OverrideBothEqualsAndHashcode4.java", 0, new OverrideBothEqualsAndHashcodeRule());
    }
}
