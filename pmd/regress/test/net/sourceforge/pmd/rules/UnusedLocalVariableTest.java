/*
 * User: tom
 * Date: Jun 28, 2002
 * Time: 1:43:05 PM
 */
package test.net.sourceforge.pmd.rules;

import junit.framework.TestCase;
import net.sourceforge.pmd.*;
import net.sourceforge.pmd.rules.UnusedLocalVariableRule;
import net.sourceforge.pmd.rules.SystemPropsRule;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.io.FileNotFoundException;

public class UnusedLocalVariableTest extends RuleTst {
    public UnusedLocalVariableTest(String name) {
        super(name);
    }

    public void testUnusedLocal1() {
        Report report = process2("Unused1.java", new UnusedLocalVariableRule());
        assertEquals(1, report.countViolationsInCurrentFile());
        assertEquals(new UnusedLocalVariableRule(), ((RuleViolation)report.violationsInCurrentFile().next()).getRule());
    }

    public void testUnusedLocal2() {
        Report report = process2("Unused2.java", new UnusedLocalVariableRule());
        assertEquals(1, report.countViolationsInCurrentFile());
        assertEquals(new UnusedLocalVariableRule(), ((RuleViolation)report.violationsInCurrentFile().next()).getRule());
    }

    public void testUnusedLocal3() {
        Report report = process2("Unused3.java", new UnusedLocalVariableRule());
        assertEquals(1, report.countViolationsInCurrentFile());
        assertEquals(new UnusedLocalVariableRule(), ((RuleViolation)report.violationsInCurrentFile().next()).getRule());
    }

    public void testUnusedLocal4() {
        Report report = process2("Unused4.java", new UnusedLocalVariableRule());
        assertTrue(report.currentFileHasNoViolations());
    }

    public void testUnusedLocal5() {
        Report report = process2("Unused5.java", new UnusedLocalVariableRule());
        assertEquals(1, report.countViolationsInCurrentFile());
        assertEquals(new UnusedLocalVariableRule(), ((RuleViolation)report.violationsInCurrentFile().next()).getRule());
    }

    public void testUnusedLocal6() {
        Report report = process2("Unused6.java", new UnusedLocalVariableRule());
        assertTrue(report.currentFileHasNoViolations());
    }

    public void testUnusedLocal7() {
        Report report = process2("Unused7.java", new UnusedLocalVariableRule());
        assertTrue(report.currentFileHasNoViolations());
    }

    public void testUnusedLocal8() {
        Report report = process2("Unused8.java", new UnusedLocalVariableRule());
        assertTrue(report.currentFileHasNoViolations());
    }

    public void testUnusedLocal9() {
        Report report = process2("Unused9.java", new UnusedLocalVariableRule());
        assertEquals(2, report.countViolationsInCurrentFile());
        Iterator i =  report.violationsInCurrentFile();
        assertEquals(new UnusedLocalVariableRule(), ((RuleViolation)i.next()).getRule());
        assertEquals(new UnusedLocalVariableRule(), ((RuleViolation)i.next()).getRule());
    }

    public void testUnusedLocal10() {
        Report report = process2("Unused10.java", new UnusedLocalVariableRule());
        assertTrue(report.currentFileHasNoViolations());
    }

}
