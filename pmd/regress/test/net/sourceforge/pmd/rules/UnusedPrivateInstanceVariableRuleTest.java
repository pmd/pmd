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

    public void test1() throws Throwable {
        Report report = process("UnusedPrivateInstanceVar1.java", rule);
        assertEquals(1, report.size());
    }

    public void test2() throws Throwable {
        Report report = process("UnusedPrivateInstanceVar2.java", rule);
        assertTrue(report.isEmpty());
    }

    public void test3() throws Throwable {
        Report report = process("UnusedPrivateInstanceVar3.java", rule);
        assertEquals(1, report.size());
    }

    public void test4() throws Throwable {
        Report report = process("UnusedPrivateInstanceVar4.java", rule);
        assertTrue(report.isEmpty());
    }

    public void test6() throws Throwable {
        Report report = process("UnusedPrivateInstanceVar6.java", rule);
        assertTrue(report.isEmpty());
    }
    public void test7() throws Throwable {
        Report report = process("UnusedPrivateInstanceVar7.java", rule);
        assertTrue(report.isEmpty());
    }

    public void test8() throws Throwable {
        Report report = process("UnusedPrivateInstanceVar8.java", rule);
        assertTrue(report.isEmpty());
    }

    // TODO
    // this test defines the current behavior of this rule
    // i.e., it doesn't check instance vars in inner classes
    // when that's fixed, this test will break
    // and we should replace the assertTrue() with the commented out assertEquals()
    // TODO
    public void test9() throws Throwable {
        Report report = process("UnusedPrivateInstanceVar10.java", rule);
        //assertEquals(1, report.size());
        assertTrue(report.isEmpty());
    }

    public void test10() throws Throwable {
        Report report = process("UnusedPrivateInstanceVar10.java", rule);
        assertTrue(report.isEmpty());
    }

    public void test11() throws Throwable {
        Report report = process("UnusedPrivateInstanceVar11.java", rule);
        assertEquals(1, report.size());
    }

    public void test12() throws Throwable {
        Report report = process("UnusedPrivateInstanceVar12.java", rule);
        assertTrue(report.isEmpty());
    }

    public void test13() throws Throwable {
        Report report = process("UnusedPrivateInstanceVar13.java", rule);
        assertTrue(report.isEmpty());
    }

    public void test14() throws Throwable {
        Report report = process("UnusedPrivateInstanceVar14.java", rule);
        assertEquals(1, report.size());
    }

    public void test15() throws Throwable {
        Report report = process("UnusedPrivateInstanceVar15.java", rule);
        assertTrue(report.isEmpty());
    }
}
