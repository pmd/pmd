/*
 * User: tom
 * Date: Jun 28, 2002
 * Time: 2:35:51 PM
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.rules.UnusedPrivateInstanceVariableRule;
import net.sourceforge.pmd.rules.UnusedLocalVariableRule;

import java.util.Iterator;

public class UnusedPrivateInstanceVariableRuleTest extends RuleTst {

    private UnusedPrivateInstanceVariableRule rule;

    public UnusedPrivateInstanceVariableRuleTest(String name) {
        super(name);
    }

    public void setUp() {
        rule = new UnusedPrivateInstanceVariableRule();
        rule.setMessage("Avoid this stuff -> ''{0}''");
    }

    public void testUnusedPrivateInstanceVar1() throws Throwable {
        Report report = process("UnusedPrivateInstanceVar1.java", rule);
        assertEquals(1, report.size());
        Iterator i =  report.iterator();
        assertEquals(rule, ((RuleViolation)i.next()).getRule());
    }

    public void testUnusedPrivateInstanceVar2() throws Throwable {
        Report report = process("UnusedPrivateInstanceVar2.java", rule);
        assertTrue(report.isEmpty());
    }

    public void testUnusedPrivateInstanceVar3() throws Throwable {
        Report report = process("UnusedPrivateInstanceVar3.java", rule);
        assertEquals(1, report.size());
        Iterator i =  report.iterator();
        assertEquals(rule, ((RuleViolation)i.next()).getRule());
    }

    public void testUnusedPrivateInstanceVar4() throws Throwable {
        Report report = process("UnusedPrivateInstanceVar4.java", rule);
        assertTrue(report.isEmpty());
    }

    public void testUnusedPrivateInstanceVar6() throws Throwable {
        Report report = process("UnusedPrivateInstanceVar6.java", rule);
        assertTrue(report.isEmpty());
    }
    public void testUnusedPrivateInstanceVar7() throws Throwable {
        Report report = process("UnusedPrivateInstanceVar7.java", rule);
        assertTrue(report.isEmpty());
    }
    public void testUnusedPrivateInstanceVar8() throws Throwable {
        Report report = process("UnusedPrivateInstanceVar8.java", rule);
        assertTrue(report.isEmpty());
    }

    public void testUnusedPrivateInstanceVar9() throws Throwable {
        Report report = process("UnusedPrivateInstanceVar10.java", rule);
        assertEquals(1, report.size());
    }
}
