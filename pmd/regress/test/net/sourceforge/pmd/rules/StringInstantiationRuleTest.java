/*
 * User: tom
 * Date: Sep 12, 2002
 * Time: 2:01:10 PM
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.rules.StringInstantiationRule;

public class StringInstantiationRuleTest extends RuleTst {

    public StringInstantiationRuleTest(String name) {
        super(name);
    }

    public void test1() throws Throwable {
        runTest("StringInstantiation1.java", 2, new StringInstantiationRule());
    }

    public void test2() throws Throwable {
        runTest("StringInstantiation2.java", 0, new StringInstantiationRule());
    }
}
