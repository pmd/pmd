/*
 * User: tom
 * Date: Aug 23, 2002
 * Time: 9:11:25 PM
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.rules.UnusedImportsRule;

public class UnusedImportsRuleTest extends RuleTst {

    private UnusedImportsRule rule;

    public void setUp() {
        rule = new UnusedImportsRule();
        rule.setMessage("Avoid this stuff -> ''{0}''");
    }

    public void test1() throws Throwable {
        Report report = process("UnusedImports1.java", rule);
        assertEquals(1, report.size());
    }

    public void test2() throws Throwable {
        Report report = process("UnusedImports2.java", rule);
        assertTrue(report.isEmpty());
    }

    public void test3() throws Throwable {
        Report report = process("UnusedImports3.java", rule);
        assertEquals(2, report.size());
    }

    public void test4() throws Throwable {
        Report report = process("UnusedImports4.java", rule);
        assertTrue(report.isEmpty());
    }
}
