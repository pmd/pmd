/*
 * User: tom
 * Date: Jun 28, 2002
 * Time: 2:35:51 PM
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.reports.Report;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.rules.UnusedPrivateInstanceVariableRule;

import java.util.Iterator;

public class UnusedPrivateInstanceVariableRuleTest extends RuleTst {

    public UnusedPrivateInstanceVariableRuleTest(String name) {
        super(name);
    }

    public void testUnusedPrivateInstanceVar1() throws Throwable {
        Report report = process("UnusedPrivateInstanceVar1.java", new UnusedPrivateInstanceVariableRule());
        assertEquals(1, report.countViolationsInCurrentFile());
        Iterator i =  report.violationsInCurrentFile();
        assertEquals(new UnusedPrivateInstanceVariableRule(), ((RuleViolation)i.next()).getRule());
    }

    public void testUnusedPrivateInstanceVar2() throws Throwable {
        Report report = process("UnusedPrivateInstanceVar2.java", new UnusedPrivateInstanceVariableRule());
        assertTrue(report.currentFileHasNoViolations());
    }

    public void testUnusedPrivateInstanceVar3() throws Throwable {
        Report report = process("UnusedPrivateInstanceVar3.java", new UnusedPrivateInstanceVariableRule());
        assertEquals(1, report.countViolationsInCurrentFile());
        Iterator i =  report.violationsInCurrentFile();
        assertEquals(new UnusedPrivateInstanceVariableRule(), ((RuleViolation)i.next()).getRule());
    }

    public void testUnusedPrivateInstanceVar4() throws Throwable {
        Report report = process("UnusedPrivateInstanceVar4.java", new UnusedPrivateInstanceVariableRule());
        assertTrue(report.currentFileHasNoViolations());
    }

    public void testUnusedPrivateInstanceVar6() throws Throwable {
        Report report = process("UnusedPrivateInstanceVar6.java", new UnusedPrivateInstanceVariableRule());
        assertTrue(report.currentFileHasNoViolations());
    }
    public void testUnusedPrivateInstanceVar7() throws Throwable {
        Report report = process("UnusedPrivateInstanceVar7.java", new UnusedPrivateInstanceVariableRule());
        assertTrue(report.currentFileHasNoViolations());
    }
    public void testUnusedPrivateInstanceVar8() throws Throwable {
        Report report = process("UnusedPrivateInstanceVar8.java", new UnusedPrivateInstanceVariableRule());
        assertTrue(report.currentFileHasNoViolations());
    }
/*
TODO - this tests unused variables in nested classes
    public void testUnusedPrivateInstanceVar9() throws Throwable {
        Report report = process("UnusedPrivateInstanceVar9.java");
        assertEquals(1, report.violationsInCurrentFile());
    }
TODO - this tests unused variables in nested classes
*/}
