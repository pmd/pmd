/*
 * User: tom
 * Date: Jun 17, 2002
 * Time: 3:19:33 PM
 */
package test.net.sourceforge.pmd;

import junit.framework.TestCase;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;

import net.sourceforge.pmd.*;

public class FunctionalTest extends TestCase{

    public FunctionalTest(String name) {
        super(name);
    }

    public void testUnusedLocal1() {
        Report report = process(new File(root() + "Unused1.java"));
        assertEquals(1, report.getSize());
        assertEquals(new UnusedLocalVariableRule(), ((RuleViolation)report.iterator().next()).getRule());
    }

    public void testUnusedLocal2() {
        Report report = process(new File(root() + "Unused2.java"));
        assertEquals(1, report.getSize());
        assertEquals(new UnusedLocalVariableRule(), ((RuleViolation)report.iterator().next()).getRule());
    }

    public void testUnusedLocal3() {
        Report report = process(new File(root() + "Unused3.java"));
        assertEquals(1, report.getSize());
        assertEquals(new UnusedLocalVariableRule(), ((RuleViolation)report.iterator().next()).getRule());
    }

    public void testUnusedLocal4() {
        Report report = process(new File(root() + "Unused4.java"));
        assertTrue(report.empty());
    }

    public void testUnusedLocal5() {
        Report report = process(new File(root() + "Unused5.java"));
        assertEquals(1, report.getSize());
        assertEquals(new UnusedLocalVariableRule(), ((RuleViolation)report.iterator().next()).getRule());
    }

    public void testUnusedLocal6() {
        Report report = process(new File(root() + "Unused6.java"));
        assertTrue(report.empty());
    }

    public void testUnusedLocal7() {
        Report report = process(new File(root() + "Unused7.java"));
        assertTrue(report.empty());
    }

    public void testUnusedLocal8() {
        Report report = process(new File(root() + "Unused8.java"));
        assertEquals(1, report.getSize());
        assertEquals(new SystemPropsRule(), ((RuleViolation)report.iterator().next()).getRule());
    }

    public void testUnusedLocal9() {
        Report report = process(new File(root() + "Unused9.java"));
        assertEquals(2, report.getSize());
        Iterator i =  report.iterator();
        assertEquals(new UnusedLocalVariableRule(), ((RuleViolation)i.next()).getRule());
        assertEquals(new UnusedLocalVariableRule(), ((RuleViolation)i.next()).getRule());
    }

    public void testUnusedLocal10() {
        Report report = process(new File(root() + "Unused10.java"));
        assertTrue(report.empty());
    }

    public void testEmptyCatchBlock() {
        Report report = process(new File(root() + "EmptyCatchBlock.java"));
        assertEquals(1, report.getSize());
        assertEquals(new EmptyCatchBlockRule(), ((RuleViolation)report.iterator().next()).getRule());
    }

    public void testUnnecessaryTemporaries() {
        Report report = process(new File(root() + "UnnecessaryTemporary.java"));
        assertEquals(6, report.getSize());
        assertEquals(new UnnecessaryConversionTemporaryRule(), ((RuleViolation)report.iterator().next()).getRule());
    }

    public void testProps() {
        Report report = process(new File(root() + "ContainsSystemGetProps.java"));
        assertEquals(3, report.getSize());
        assertEquals(new SystemPropsRule(), ((RuleViolation)report.iterator().next()).getRule());
    }

    public void testSystemIn() {
        Report report = process(new File(root() + "ContainsSystemIn.java"));
        assertEquals(3, report.getSize());
        assertEquals(new SystemOutRule(), ((RuleViolation)report.iterator().next()).getRule());
    }

    public void testSystemOut() {
        Report report = process(new File(root() + "ContainsSystemOut.java"));
        assertEquals(1, report.getSize());
        assertEquals(new SystemOutRule(), ((RuleViolation)report.iterator().next()).getRule());
    }

    public void testCreateAThread() {
        Report report = process(new File(root() + "CreatesAThread.java"));
        assertEquals(1, report.getSize());
        assertEquals(new DontCreateThreadsRule(), ((RuleViolation)report.iterator().next()).getRule());
    }

    public void testCreateATimer() {
        Report report = process(new File(root() + "CreatesATimer.java"));
        assertEquals(1, report.getSize());
        assertEquals(new DontCreateTimersRule(), ((RuleViolation)report.iterator().next()).getRule());
    }

    public void testEmptyIf() {
        Report report = process(new File(root() + "EmptyIfStmtRule.java"));
        assertEquals(1, report.getSize());
        assertEquals(new EmptyIfStmtRule(), ((RuleViolation)report.iterator().next()).getRule());
    }

/*
    public void testUnusedPrivateInstanceVar1() {
        Report report = process(new File(root() + "UnusedPrivateInstanceVar1.java"));
        assertEquals(1, report.getSize());
        Iterator i =  report.iterator();
        assertEquals(new UnusedPrivateInstanceVariableRule(), ((RuleViolation)i.next()).getRule());
    }

    public void testUnusedPrivateInstanceVar2() {
        Report report = process(new File(root() + "UnusedPrivateInstanceVar2.java"));
        assertTrue(report.empty());
    }

    public void testUnusedPrivateInstanceVar3() {
        Report report = process(new File(root() + "UnusedPrivateInstanceVar3.java"));
        assertEquals(1, report.getSize());
        Iterator i =  report.iterator();
        assertEquals(new UnusedPrivateInstanceVariableRule(), ((RuleViolation)i.next()).getRule());
    }

    public void testUnusedPrivateInstanceVar4() {
        Report report = process(new File(root() + "UnusedPrivateInstanceVar4.java"));
        assertTrue(report.empty());
    }

    public void testUnusedPrivateInstanceVar6() {
        Report report = process(new File(root() + "UnusedPrivateInstanceVar6.java"));
        assertTrue(report.empty());
    }
    public void testUnusedPrivateInstanceVar7() {
        Report report = process(new File(root() + "UnusedPrivateInstanceVar7.java"));
        assertTrue(report.empty());
    }
    public void testUnusedPrivateInstanceVar8() {
        Report report = process(new File(root() + "UnusedPrivateInstanceVar8.java"));
        assertTrue(report.empty());
    }
*/
    // TODO
    // there's another test here that needs to work, see UnusedPrivateInstanceVar5.java
    // 'foo' should be marked as an unused instance variable, but it isn't because the
    // anonymous inner classes are broken for that rule because the "doingIDTraversal" is an
    // instance variable
    // TODO

    private Report process(File file) {
        PMD p = new PMD();
        return p.processFile(file, RuleFactory.ALL);
    }

    private String root() {
        if (System.getProperty("os.name").indexOf("Linux") != -1) {
            return "/home/build/hourly/working/pmd/test-data" + System.getProperty("file.separator");
        }
        return "c:\\data\\pmd\\pmd\\test-data" + System.getProperty("file.separator");
    }

}
