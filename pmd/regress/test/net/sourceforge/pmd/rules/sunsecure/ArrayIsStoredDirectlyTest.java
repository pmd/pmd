/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules.sunsecure;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class ArrayIsStoredDirectlyTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("rulesets/sunsecure.xml", "ArrayIsStoredDirectly");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "Clear violation", 1, rule),
           new TestDescriptor(TEST2, "Clear violation with this.", 1, rule),
           new TestDescriptor(TEST3, "TEST3", 1, rule),
           new TestDescriptor(TEST4, "TEST4", 0, rule),
       });
    }

    private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    " String [] arr;" + PMD.EOL +
    " void foo (String[] x) {arr = x;} ;" + PMD.EOL +
    "}";

    private static final String TEST2 =
        "public class Foo {" + PMD.EOL +
        " String [] arr;" + PMD.EOL +
        " void foo (String[] arr) {this.arr = arr;} ;" + PMD.EOL +
        "}";

    private static final String TEST3 =
        "public class Foo {" + PMD.EOL +
        " String [] arr;" + PMD.EOL +
        " void foo (String[] x) {this.arr = x;} ;" + PMD.EOL +
        "}";

    private static final String TEST4 =
        "public class Foo {" + PMD.EOL +
        " String [] arr;" + PMD.EOL +
        " void getArr(String[] arr) {String[] foo; foo = arr;} ;" + PMD.EOL +
        "}";


}
