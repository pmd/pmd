/*
 * User: tom
 * Date: Aug 23, 2002
 * Time: 9:11:25 PM
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.rules.UnusedImportsRule;

public class UnusedImportsRuleTest extends RuleTst {

    private UnusedImportsRule rule;

    public void setUp() {
        rule = new UnusedImportsRule();
        rule.setMessage("Avoid this stuff -> ''{0}''");
    }

    public void test1() throws Throwable {
        runTest("UnusedImports1.java", 1, rule);
    }
    public void test2() throws Throwable {
        runTest("UnusedImports2.java", 0, rule);
    }
    public void test3() throws Throwable {
        runTest("UnusedImports3.java", 2, rule);
    }
    public void test4() throws Throwable {
        runTest("UnusedImports4.java", 0, rule);
    }
}
