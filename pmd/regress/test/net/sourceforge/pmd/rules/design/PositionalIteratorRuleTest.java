/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.rules.design.PositionalIteratorRule;
import test.net.sourceforge.pmd.testframework.RuleTst;

public class PositionalIteratorRuleTest extends RuleTst {

    private static final String TEST1 =
    "public class PositionalIterator1 {" + PMD.EOL +
    " public void foo(Iterator i) {" + PMD.EOL +
    "  while(i.hasNext()) {" + PMD.EOL +
    "   Object one = i.next();" + PMD.EOL +
    "   " + PMD.EOL +
    "   // 2 calls to next() inside the loop == bad!" + PMD.EOL +
    "   Object two = i.next(); " + PMD.EOL +
    "  }" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class PositionalIterator2 {" + PMD.EOL +
    " public void foo(Iterator i) {" + PMD.EOL +
    "  while(i.hasNext()) {" + PMD.EOL +
    "   Object one = i.next();" + PMD.EOL +
    "  }" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class PositionalIterator3 {" + PMD.EOL +
    " public void foo() {" + PMD.EOL +
    "  Iterator i = (new List()).iterator();" + PMD.EOL +
    "  while(i.hasNext()) {" + PMD.EOL +
    "   Object one = i.next();" + PMD.EOL +
    "   Iterator j = (new List()).iterator();" + PMD.EOL +
    "   while (j.hasNext()) {" + PMD.EOL +
    "    j.next();" + PMD.EOL +
    "   }" + PMD.EOL +
    "  }" + PMD.EOL +
    " }" + PMD.EOL +
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
