/*
 * User: tom
 * Date: Jul 19, 2002
 * Time: 11:32:02 PM
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.rules.ForLoopsMustUseBracesRule;

public class ForLoopsMustUseBracesRuleTest extends RuleTst {

    public void test1() throws Throwable {
        Report report = process("ForLoopsNeedBraces1.java", new ForLoopsMustUseBracesRule());
        assertEquals(1, report.size());
    }

    public void test2() throws Throwable {
        Report report = process("ForLoopsNeedBraces2.java", new ForLoopsMustUseBracesRule());
        assertTrue(report.isEmpty());
    }
    public void test3() throws Throwable {
        Report report = process("ForLoopsNeedBraces3.java", new ForLoopsMustUseBracesRule());
        assertEquals(1, report.size());
    }

    public void test4() throws Throwable {
        Report report = process("ForLoopsNeedBraces4.java", new ForLoopsMustUseBracesRule());
        assertEquals(1, report.size());
    }

    public void test5() throws Throwable {
        Report report = process("ForLoopsNeedBraces5.java", new ForLoopsMustUseBracesRule());
        assertEquals(1, report.size());
    }

}
