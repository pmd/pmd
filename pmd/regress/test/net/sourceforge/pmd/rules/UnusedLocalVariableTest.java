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
        runTest("Unused4.java", 0, rule);
    }

    public void test5() throws Throwable {
        Report report = process("Unused5.java", rule);
        assertEquals(1, report.size());
        assertEquals(rule, ((RuleViolation)report.iterator().next()).getRule());
    }

    public void test6() throws Throwable {
        runTest("Unused6.java", 0, rule);
    }

    public void test7() throws Throwable {
        runTest("Unused7.java", 0, rule);
    }

    public void test8() throws Throwable {
        runTest("Unused8.java", 0, rule);
    }

    public void test9() throws Throwable {
        runTest("Unused9.java", 0, rule);
    }

    public void test10() throws Throwable {
        runTest("Unused10.java", 0, rule);
    }

    public void test11() throws Throwable {
        runTest("Unused11.java", 2, rule);
    }
    public void test12() throws Throwable {
        runTest("Unused12.java", 0, rule);
    }

    public void test13() throws Throwable {
        runTest("Unused13.java", 0, rule);
    }

    public void test14() throws Throwable {
        runTest("Unused14.java", 2, rule);
    }
}
