package test.net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.rules.design.PositionalIteratorRule;
import net.sourceforge.pmd.cpd.CPD;
import test.net.sourceforge.pmd.rules.RuleTst;

public class PositionalIteratorRuleTest extends RuleTst {

    private static final String TEST1 =
    "public class PositionalIterator1 {" + CPD.EOL +
    " public void foo(Iterator i) {" + CPD.EOL +
    "  while(i.hasNext()) {" + CPD.EOL +
    "   Object one = i.next();" + CPD.EOL +
    "   " + CPD.EOL +
    "   // 2 calls to next() inside the loop == bad!" + CPD.EOL +
    "   Object two = i.next(); " + CPD.EOL +
    "  }" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST2 =
    "public class PositionalIterator2 {" + CPD.EOL +
    " public void foo(Iterator i) {" + CPD.EOL +
    "  while(i.hasNext()) {" + CPD.EOL +
    "   Object one = i.next();" + CPD.EOL +
    "  }" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST3 =
    "public class PositionalIterator3 {" + CPD.EOL +
    " public void foo() {" + CPD.EOL +
    "  Iterator i = (new List()).iterator();" + CPD.EOL +
    "  while(i.hasNext()) {" + CPD.EOL +
    "   Object one = i.next();" + CPD.EOL +
    "   Iterator j = (new List()).iterator();" + CPD.EOL +
    "   while (j.hasNext()) {" + CPD.EOL +
    "    j.next();" + CPD.EOL +
    "   }" + CPD.EOL +
    "  }" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    public void test1() throws Throwable {
        runTestFromString(TEST1, 1, new PositionalIteratorRule());
    }
    public void test2() throws Throwable {
        runTestFromString(TEST2, 0, new PositionalIteratorRule());
    }
    public void test3() throws Throwable {
        runTestFromString(TEST3, 0, new PositionalIteratorRule());
    }
}
