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

public class UnusedLocalVariableTest extends TestCase {
    public UnusedLocalVariableTest(String name) {
        super(name);
    }

    public void testUnusedLocal1() {
        Report report = process("Unused1.java");
        assertEquals(1, report.countViolationsInCurrentFile());
        assertEquals(new UnusedLocalVariableRule(), ((RuleViolation)report.violationsInCurrentFile().next()).getRule());
    }

    public void testUnusedLocal2() {
        Report report = process("Unused2.java");
        assertEquals(1, report.countViolationsInCurrentFile());
        assertEquals(new UnusedLocalVariableRule(), ((RuleViolation)report.violationsInCurrentFile().next()).getRule());
    }

    public void testUnusedLocal3() {
        Report report = process("Unused3.java");
        assertEquals(1, report.countViolationsInCurrentFile());
        assertEquals(new UnusedLocalVariableRule(), ((RuleViolation)report.violationsInCurrentFile().next()).getRule());
    }

    public void testUnusedLocal4() {
        Report report = process("Unused4.java");
        assertTrue(report.currentFileHasNoViolations());
    }

    public void testUnusedLocal5() {
        Report report = process("Unused5.java");
        assertEquals(1, report.countViolationsInCurrentFile());
        assertEquals(new UnusedLocalVariableRule(), ((RuleViolation)report.violationsInCurrentFile().next()).getRule());
    }

    public void testUnusedLocal6() {
        Report report = process("Unused6.java");
        assertTrue(report.currentFileHasNoViolations());
    }

    public void testUnusedLocal7() {
        Report report = process("Unused7.java");
        assertTrue(report.currentFileHasNoViolations());
    }

    public void testUnusedLocal8() {
        Report report = process("Unused8.java");
        assertTrue(report.currentFileHasNoViolations());
    }

    public void testUnusedLocal9() {
        Report report = process("Unused9.java");
        assertEquals(2, report.countViolationsInCurrentFile());
        Iterator i =  report.violationsInCurrentFile();
        assertEquals(new UnusedLocalVariableRule(), ((RuleViolation)i.next()).getRule());
        assertEquals(new UnusedLocalVariableRule(), ((RuleViolation)i.next()).getRule());
    }

    public void testUnusedLocal10() {
        Report report = process("Unused10.java");
        assertTrue(report.currentFileHasNoViolations());
    }

    private Report process(String file) {
        try {
            PMD p = new PMD();
            RuleContext ctx = new RuleContext();
            ctx.setReport(new Report("xml", file));
            p.processFile(file, getClass().getClassLoader().getResourceAsStream(file), new UnusedLocalVariableRule(), ctx);
            return ctx.getReport();
        } catch (FileNotFoundException fnfe) {
            throw new RuntimeException("File " + file + " not found");
        }
    }
}
