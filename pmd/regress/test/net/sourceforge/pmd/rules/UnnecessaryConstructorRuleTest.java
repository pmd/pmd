/*
 * User: tom
 * Date: Nov 1, 2002
 * Time: 9:12:42 AM
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.rules.UnnecessaryConstructorRule;

public class UnnecessaryConstructorRuleTest extends RuleTst {

    public void test1() throws Throwable {
        super.runTest("UnnecessaryConstructor1.java", 1, new UnnecessaryConstructorRule());
    }

    public void testPrivate() throws Throwable {
        super.runTest("UnnecessaryConstructor2.java", 0, new UnnecessaryConstructorRule());
    }

    public void testHasArgs() throws Throwable {
        super.runTest("UnnecessaryConstructor3.java", 0, new UnnecessaryConstructorRule());
    }

    public void testHasBody() throws Throwable {
        super.runTest("UnnecessaryConstructor4.java", 0, new UnnecessaryConstructorRule());
    }
}
