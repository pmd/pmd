package test.net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.rules.design.PositionalIteratorRule;
import test.net.sourceforge.pmd.rules.RuleTst;

public class PositionalIteratorRuleTest extends RuleTst {

    public void test1() throws Throwable {
        runTestFromFile("PositionalIterator1.java", 1, new PositionalIteratorRule());
    }

    public void test2() throws Throwable {
        runTestFromFile("PositionalIterator2.java", 0, new PositionalIteratorRule());
    }

    public void test3() throws Throwable {
        runTestFromFile("PositionalIterator3.java", 0, new PositionalIteratorRule());
    }
}
