/*
 * User: tom
 * Date: Jul 22, 2002
 * Time: 12:05:25 PM
 */
package test.net.sourceforge.pmd.rules.design;

import test.net.sourceforge.pmd.rules.RuleTst;
import net.sourceforge.pmd.rules.UnusedLocalVariableRule;
import net.sourceforge.pmd.rules.design.LooseCouplingRule;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleViolation;

public class LooseCouplingRuleTest extends RuleTst {
    private LooseCouplingRule rule;

    public void setUp() {
        rule = new LooseCouplingRule();
        rule.setMessage("Avoid this stuff -> ''{0}''");
    }

    public void test1() throws Throwable {
        Report report = process("LooseCoupling1.java", rule);
        assertEquals(1, report.size());
    }

    public void test2() throws Throwable {
        Report report = process("LooseCoupling2.java", rule);
        assertTrue(report.isEmpty());
    }

    public void test3() throws Throwable {
        Report report = process("LooseCoupling3.java", rule);
        assertTrue(report.isEmpty());
    }

    public void test4() throws Throwable {
        Report report = process("LooseCoupling4.java", rule);
        assertTrue(report.isEmpty());
    }
    public void test5() throws Throwable {
        Report report = process("LooseCoupling5.java", rule);
        assertEquals(1, report.size());
    }
    public void test6() throws Throwable {
        Report report = process("LooseCoupling6.java", rule);
        assertEquals(2, report.size());
    }
    public void test7() throws Throwable {
        Report report = process("LooseCoupling7.java", rule);
        assertEquals(2, report.size());
    }
}
