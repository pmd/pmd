/*
 * User: tom
 * Date: Sep 12, 2002
 * Time: 2:01:10 PM
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.rules.XPathRule;
import net.sourceforge.pmd.Rule;

public class StringInstantiationRuleTest extends RuleTst {

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty("xpath", "//AllocationExpression[Name/@Image='String'][count(.//Expression) < 2][not(ArrayDimsAndInits)]");
    }

    public void test1() throws Throwable {
        runTest("StringInstantiation1.java", 2, rule);
    }

    public void test2() throws Throwable {
        runTest("StringInstantiation2.java", 0, rule);
    }

    public void test3() throws Throwable {
        runTest("StringInstantiation3.java", 0, rule);
    }

    public void test4() throws Throwable {
        runTest("StringInstantiation4.java", 0, rule);
    }
}
