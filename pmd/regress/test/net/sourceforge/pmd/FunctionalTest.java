/*
 * User: tom
 * Date: Jun 17, 2002
 * Time: 3:19:33 PM
 */
package test.net.sourceforge.pmd;

import junit.framework.TestCase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Iterator;

import net.sourceforge.pmd.*;
import net.sourceforge.pmd.rules.*;

public class FunctionalTest extends TestCase{

    public FunctionalTest(String name) {
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
        assertEquals(1, report.countViolationsInCurrentFile());
        assertEquals(new SystemPropsRule(), ((RuleViolation)report.violationsInCurrentFile().next()).getRule());
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

    public void testEmptyCatchBlock() {
        Report report = process("EmptyCatchBlock.java");
        assertEquals(1, report.countViolationsInCurrentFile());
        assertEquals(new EmptyCatchBlockRule(), ((RuleViolation)report.violationsInCurrentFile().next()).getRule());
    }

    public void testEmptyCatchBlock2() {
        Report report = process("EmptyCatchBlock2.java");
        assertTrue(report.currentFileHasNoViolations());
    }

    public void testEmptyCatchBlock3() {
        Report report = process("EmptyCatchBlock3.java");
        assertEquals(1, report.countViolationsInCurrentFile());
        assertEquals(new EmptyCatchBlockRule(), ((RuleViolation)report.violationsInCurrentFile().next()).getRule());
    }

    public void testUnnecessaryTemporaries() {
        Report report = process("UnnecessaryTemporary.java");
        assertEquals(6, report.countViolationsInCurrentFile());
        assertEquals(new UnnecessaryConversionTemporaryRule(), ((RuleViolation)report.violationsInCurrentFile().next()).getRule());
    }

    public void testProps() {
        Report report = process("ContainsSystemGetProps.java");
        assertEquals(3, report.countViolationsInCurrentFile());
        assertEquals(new SystemPropsRule(), ((RuleViolation)report.violationsInCurrentFile().next()).getRule());
    }

    public void testSystemIn() {
        Report report = process("ContainsSystemIn.java");
        assertEquals(3, report.countViolationsInCurrentFile());
        assertEquals(new SystemOutRule(), ((RuleViolation)report.violationsInCurrentFile().next()).getRule());
    }

    public void testSystemOut() {
        Report report = process("ContainsSystemOut.java");
        assertEquals(1, report.countViolationsInCurrentFile());
        assertEquals(new SystemOutRule(), ((RuleViolation)report.violationsInCurrentFile().next()).getRule());
    }

    public void testCreateAThread() {
        Report report = process("CreatesAThread.java");
        assertEquals(1, report.countViolationsInCurrentFile());
        assertEquals(new DontCreateThreadsRule(), ((RuleViolation)report.violationsInCurrentFile().next()).getRule());
    }

    public void testCreateATimer() {
        Report report = process("CreatesATimer.java");
        assertEquals(1, report.countViolationsInCurrentFile());
        assertEquals(new DontCreateTimersRule(), ((RuleViolation)report.violationsInCurrentFile().next()).getRule());
    }

    public void testEmptyIf() {
        Report report = process("EmptyIfStmtRule.java");
        assertEquals(1, report.countViolationsInCurrentFile());
        assertEquals(new EmptyIfStmtRule(), ((RuleViolation)report.violationsInCurrentFile().next()).getRule());
    }

    public void testUnusedPrivateInstanceVar1() {
        Report report = process("UnusedPrivateInstanceVar1.java");
        assertEquals(1, report.countViolationsInCurrentFile());
        Iterator i =  report.violationsInCurrentFile();
        assertEquals(new UnusedPrivateInstanceVariableRule(), ((RuleViolation)i.next()).getRule());
    }

    public void testUnusedPrivateInstanceVar2() {
        Report report = process("UnusedPrivateInstanceVar2.java");
        assertTrue(report.currentFileHasNoViolations());
    }

    public void testUnusedPrivateInstanceVar3() {
        Report report = process("UnusedPrivateInstanceVar3.java");
        assertEquals(1, report.countViolationsInCurrentFile());
        Iterator i =  report.violationsInCurrentFile();
        assertEquals(new UnusedPrivateInstanceVariableRule(), ((RuleViolation)i.next()).getRule());
    }

    public void testUnusedPrivateInstanceVar4() {
        Report report = process("UnusedPrivateInstanceVar4.java");
        assertTrue(report.currentFileHasNoViolations());
    }

    public void testUnusedPrivateInstanceVar6() {
        Report report = process("UnusedPrivateInstanceVar6.java");
        assertTrue(report.currentFileHasNoViolations());
    }
    public void testUnusedPrivateInstanceVar7() {
        Report report = process("UnusedPrivateInstanceVar7.java");
        assertTrue(report.currentFileHasNoViolations());
    }
    public void testUnusedPrivateInstanceVar8() {
        Report report = process("UnusedPrivateInstanceVar8.java");
        assertTrue(report.currentFileHasNoViolations());
    }

    public void testIfElseStmtsMustUseBraces1() {
        Report report = process("IfElseStmtsNeedBraces1.java");
        assertEquals(1, report.countViolationsInCurrentFile());
    }
    public void testIfElseStmtsMustUseBraces2() {
        Report report = process("IfElseStmtsNeedBraces2.java");
        assertTrue(report.currentFileHasNoViolations());
    }
/*
TODO - this tests unused variables in nested classes
    public void testUnusedPrivateInstanceVar9() {
        Report report = process("UnusedPrivateInstanceVar9.java");
        assertEquals(1, report.violationsInCurrentFile());
    }
TODO - this tests unused variables in nested classes
*/

    private Report process(String file) {
        try {
            PMD p = new PMD();
            RuleContext ctx = new RuleContext();
            ctx.setReport(new Report("xml", file));
            p.processFile(file, getClass().getClassLoader().getResourceAsStream(file), RuleFactory.ALL, ctx);
            return ctx.getReport();
        } catch (FileNotFoundException fnfe) {
            throw new RuntimeException("File " + file + " not found");
        }
    }
}
