/*
 * User: tom
 * Date: Sep 4, 2002
 * Time: 11:44:14 AM
 */
package test.net.sourceforge.pmd.rules.design;

import junit.framework.TestCase;
import test.net.sourceforge.pmd.rules.RuleTst;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.rules.design.PositionalIteratorRule;

public class PositionalIteratorRuleTest extends RuleTst {

    public PositionalIteratorRuleTest(String name) {
        super(name);
    }

    public void test1() throws Throwable {
        runTest("PositionalIterator1.java", 1);
    }

    public void test2() throws Throwable {
        runTest("PositionalIterator2.java", 0);
    }

    public void test3() throws Throwable {
        runTest("PositionalIterator3.java", 0);
    }

    private void runTest(String filename, int expectedResults) throws Throwable {
        Report report = process(filename, new PositionalIteratorRule());
        assertEquals(expectedResults, report.size());
    }
}
