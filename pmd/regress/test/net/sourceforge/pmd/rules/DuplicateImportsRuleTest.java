/*
 * User: tom
 * Date: Jul 12, 2002
 * Time: 10:51:59 PM
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.rules.DuplicateImportsRule;
import net.sourceforge.pmd.rules.UnusedLocalVariableRule;

public class DuplicateImportsRuleTest extends RuleTst {

    private DuplicateImportsRule rule;

    public void setUp() {
        rule = new DuplicateImportsRule();
        rule.setMessage("Avoid this stuff -> ''{0}''");
    }

    public void test1() throws Throwable {
        Report report = process("DuplicateImports.java", rule);
        assertEquals(1, report.size());
    }
    public void test2() throws Throwable {
        Report report = process("DuplicateImports2.java", rule);
        assertEquals(1, report.size());
    }
    public void test3() throws Throwable {
        Report report = process("DuplicateImports3.java", rule);
        assertEquals(1, report.size());
    }
    public void test4() throws Throwable {
        Report report = process("DuplicateImports4.java", rule);
        assertTrue(report.isEmpty());
    }
}
