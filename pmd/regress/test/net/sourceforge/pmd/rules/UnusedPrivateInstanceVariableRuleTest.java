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

    public void setUp() {
        rule = new UnusedPrivateInstanceVariableRule();
        rule.setMessage("Avoid this stuff -> ''{0}''");
    }

   public void test1() throws Throwable {
       runTest("UnusedPrivateInstanceVar1.java", 1, rule);
    }

    public void test2() throws Throwable {
        runTest("UnusedPrivateInstanceVar2.java", 0, rule);
    }

    public void test3() throws Throwable {
        runTest("UnusedPrivateInstanceVar3.java", 1, rule);
    }

    public void test4() throws Throwable {
        runTest("UnusedPrivateInstanceVar4.java", 0, rule);
    }

    public void test6() throws Throwable {
        runTest("UnusedPrivateInstanceVar6.java", 0, rule);
    }
    public void test7() throws Throwable {
        runTest("UnusedPrivateInstanceVar7.java", 0, rule);
    }

    public void test8() throws Throwable {
        runTest("UnusedPrivateInstanceVar8.java", 0, rule);
    }

    public void test9() throws Throwable {
        runTest("UnusedPrivateInstanceVar9.java", 1, rule);
    }

    // TODO
    // this test defines the current behavior of this rule
    // i.e., it doesn't check instance vars in inner classes
    // when that's fixed, this test will break
    // and we should replace the current test with the commented out test
    // TODO
    public void test10() throws Throwable {
        runTest("UnusedPrivateInstanceVar10.java", 0, rule);
        //runTest("UnusedPrivateInstanceVar10.java", 1, rule);
    }

    public void test11() throws Throwable {
        runTest("UnusedPrivateInstanceVar11.java", 1, rule);
    }

    public void test12() throws Throwable {
        runTest("UnusedPrivateInstanceVar12.java", 0, rule);
    }

    public void test13() throws Throwable {
        runTest("UnusedPrivateInstanceVar13.java", 0, rule);
    }

    public void test14() throws Throwable {
        runTest("UnusedPrivateInstanceVar14.java", 1, rule);
    }
    public void test15() throws Throwable {
        runTest("UnusedPrivateInstanceVar15.java", 2, rule);
    }

    public void test16() throws Throwable {
        runTest("UnusedPrivateInstanceVar16.java", 1, rule);
    }

    public void test17() throws Throwable {
        runTest("UnusedPrivateInstanceVar17.java", 0, rule);
    }
}
