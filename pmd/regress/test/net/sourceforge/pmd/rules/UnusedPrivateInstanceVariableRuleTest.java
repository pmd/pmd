/*
 * User: tom
 * Date: Jun 28, 2002
 * Time: 2:35:51 PM
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.rules.UnusedPrivateInstanceVariableRule;

import java.util.Iterator;

public class UnusedPrivateInstanceVariableRuleTest extends RuleTst {

    public UnusedPrivateInstanceVariableRuleTest(String name) {
        super(name);
    }

    public void testUnusedPrivateInstanceVar1() {
        Report report = process2("UnusedPrivateInstanceVar1.java", new UnusedPrivateInstanceVariableRule());
        assertEquals(1, report.countViolationsInCurrentFile());
        Iterator i =  report.violationsInCurrentFile();
        assertEquals(new UnusedPrivateInstanceVariableRule(), ((RuleViolation)i.next()).getRule());
    }

    public void testUnusedPrivateInstanceVar2() {
        Report report = process2("UnusedPrivateInstanceVar2.java", new UnusedPrivateInstanceVariableRule());
        assertTrue(report.currentFileHasNoViolations());
    }

    public void testUnusedPrivateInstanceVar3() {
        Report report = process2("UnusedPrivateInstanceVar3.java", new UnusedPrivateInstanceVariableRule());
        assertEquals(1, report.countViolationsInCurrentFile());
        Iterator i =  report.violationsInCurrentFile();
        assertEquals(new UnusedPrivateInstanceVariableRule(), ((RuleViolation)i.next()).getRule());
    }

    public void testUnusedPrivateInstanceVar4() {
        Report report = process2("UnusedPrivateInstanceVar4.java", new UnusedPrivateInstanceVariableRule());
        assertTrue(report.currentFileHasNoViolations());
    }

    public void testUnusedPrivateInstanceVar6() {
        Report report = process2("UnusedPrivateInstanceVar6.java", new UnusedPrivateInstanceVariableRule());
        assertTrue(report.currentFileHasNoViolations());
    }
    public void testUnusedPrivateInstanceVar7() {
        Report report = process2("UnusedPrivateInstanceVar7.java", new UnusedPrivateInstanceVariableRule());
        assertTrue(report.currentFileHasNoViolations());
    }
    public void testUnusedPrivateInstanceVar8() {
        Report report = process2("UnusedPrivateInstanceVar8.java", new UnusedPrivateInstanceVariableRule());
        assertTrue(report.currentFileHasNoViolations());
    }
}
