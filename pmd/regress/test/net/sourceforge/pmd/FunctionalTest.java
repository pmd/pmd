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

    public void testRemoveMe() {}

/*

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

    public void testEmptyWhileStmtRule() {
        Report report = process("EmptyWhileStmtRule.java");
        assertEquals(1, report.countViolationsInCurrentFile());
        assertEquals(new EmptyWhileStmtRule(), ((RuleViolation)report.violationsInCurrentFile().next()).getRule());
    }


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
