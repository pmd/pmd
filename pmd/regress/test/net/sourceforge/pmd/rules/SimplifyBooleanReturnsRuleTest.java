/*
 * User: tom
 * Date: Aug 14, 2002
 * Time: 11:29:16 AM
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.rules.SimplifyBooleanReturnsRule;

public class SimplifyBooleanReturnsRuleTest extends RuleTst {
    public SimplifyBooleanReturnsRuleTest(String name) {
        super(name);
    }

    public void test1() throws Throwable {
        Report report = process("SimplifyBooleanReturns1.java", new SimplifyBooleanReturnsRule());
        assertEquals(1, report.size());
    }

    public void test2() throws Throwable {
        Report report = process("SimplifyBooleanReturns2.java", new SimplifyBooleanReturnsRule());
        assertEquals(1, report.size());
    }

    public void test3() throws Throwable {
        Report report = process("SimplifyBooleanReturns3.java", new SimplifyBooleanReturnsRule());
        assertTrue(report.isEmpty());
    }

}
