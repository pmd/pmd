/*
 * User: tom
 * Date: Jul 12, 2002
 * Time: 10:51:59 PM
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.rules.DuplicateImportsRule;

public class DuplicateImportsRuleTest extends RuleTst {

    private DuplicateImportsRule rule;

    public void setUp() {
        rule = new DuplicateImportsRule();
        rule.setMessage("Avoid this stuff -> ''{0}''");
    }

    public void test1() throws Throwable {
        runTest("DuplicateImports.java", 1, rule);
    }
    public void test2() throws Throwable {
        runTest("DuplicateImports2.java", 1, rule);
    }
    public void test3() throws Throwable {
        runTest("DuplicateImports3.java", 1, rule);
    }
    public void test4() throws Throwable {
        runTest("DuplicateImports4.java", 0, rule);
    }
}
