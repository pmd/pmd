/*
 * User: tom
 * Date: Jun 28, 2002
 * Time: 1:43:05 PM
 */
package test.net.sourceforge.pmd.rules;

import junit.framework.TestCase;
import net.sourceforge.pmd.*;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.rules.UnusedLocalVariableRule;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

public class UnusedLocalVariableTest extends RuleTst {

    private UnusedLocalVariableRule rule;

    public UnusedLocalVariableTest(String name) {
        super(name);
    }

    public void setUp() {
        rule = new UnusedLocalVariableRule();
        rule.setMessage("Avoid this stuff -> ''{0}''");
    }

    public void test1() throws Throwable {
        Report report = process("Unused1.java", rule);
        assertEquals(1, report.size());
        assertEquals(rule, ((RuleViolation)report.iterator().next()).getRule());
    }

    public void test2() throws Throwable {
        Report report = process("Unused2.java", rule);
        assertEquals(1, report.size());
        assertEquals(rule, ((RuleViolation)report.iterator().next()).getRule());
    }

    public void test3() throws Throwable {
        Report report = process("Unused3.java", rule);
        assertEquals(1, report.size());
        assertEquals(rule, ((RuleViolation)report.iterator().next()).getRule());
    }

    public void test4() throws Throwable {
        Report report = process("Unused4.java", rule);
        assertTrue(report.isEmpty());
    }

    public void test5() throws Throwable {
        Report report = process("Unused5.java", rule);
        assertEquals(1, report.size());
        assertEquals(rule, ((RuleViolation)report.iterator().next()).getRule());
    }

    public void test6() throws Throwable {
        Report report = process("Unused6.java", rule);
        assertTrue(report.isEmpty());
    }

    public void test7() throws Throwable {
        Report report = process("Unused7.java", rule);
        assertTrue(report.isEmpty());
    }

    public void test8() throws Throwable {
        Report report = process("Unused8.java", rule);
        assertTrue(report.isEmpty());
    }

    public void test9() throws Throwable {
        Report report = process("Unused9.java", rule);
        assertTrue(report.isEmpty());
    }

    public void test10() throws Throwable {
        Report report = process("Unused10.java", rule);
        assertTrue(report.isEmpty());
    }

    public void test11() throws Throwable {
        Report report = process("Unused11.java", rule);
        assertEquals(2, report.size());
    }
    public void test12() throws Throwable {
        Report report = process("Unused12.java", rule);
        assertTrue(report.isEmpty());
    }

    public void test13() throws Throwable {
        Report report = process("Unused13.java", rule);
        assertTrue(report.isEmpty());
    }
}
