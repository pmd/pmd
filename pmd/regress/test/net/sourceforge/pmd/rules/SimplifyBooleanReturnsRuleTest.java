package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.rules.SimplifyBooleanReturnsRule;

public class SimplifyBooleanReturnsRuleTest extends RuleTst {

    public void test1() throws Throwable {
        Report report = runTestFromFile("SimplifyBooleanReturns1.java", new SimplifyBooleanReturnsRule());
        assertEquals(1, report.size());
    }

    public void test2() throws Throwable {
        Report report = runTestFromFile("SimplifyBooleanReturns2.java", new SimplifyBooleanReturnsRule());
        assertEquals(1, report.size());
    }

    public void test3() throws Throwable {
        Report report = runTestFromFile("SimplifyBooleanReturns3.java", new SimplifyBooleanReturnsRule());
        assertTrue(report.isEmpty());
    }

}
