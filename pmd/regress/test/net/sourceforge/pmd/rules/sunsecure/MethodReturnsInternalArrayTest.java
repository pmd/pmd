/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules.sunsecure;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class MethodReturnsInternalArrayTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("sunsecure", "MethodReturnsInternalArray");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "Clear violation", 1, rule),
           new TestDescriptor(TEST2, "Clear violation with this.", 1, rule),
           new TestDescriptor(TEST3, "ok", 0, rule),
           new TestDescriptor(TEST4, "tricky field hiding", 0, rule),
           new TestDescriptor(TEST5, "really sick code", 1, rule),
           new TestDescriptor(TEST6, "returning a local array is ok", 0, rule),
           new TestDescriptor(TEST7, "returning a local array is ok part deux", 0, rule),
           new TestDescriptor(TEST8, "returning a cloned field", 0, rule),
       });
    }

    private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    " String [] arr;" + PMD.EOL +
    " String [] getArr() {return arr;} ;" + PMD.EOL +
    "}";

    private static final String TEST2 =
        "public class Foo {" + PMD.EOL +
        " String [] arr;" + PMD.EOL +
        " String [] getArr() {return this.arr;} ;" + PMD.EOL +
        "}";

    private static final String TEST3 =
        "public class Foo {" + PMD.EOL +
        " String [] arr;" + PMD.EOL +
        " String [] getArr() {String[] foo; return foo;} ;" + PMD.EOL +
        "}";

    private static final String TEST4 =
        "public class Foo {" + PMD.EOL +
        " String [] arr;" + PMD.EOL +
        " String [] getArr() {String[] arr; return arr;} ;" + PMD.EOL +
        "}";

    private static final String TEST5 =
        "public class Foo {" + PMD.EOL +
        " String [] arr;" + PMD.EOL +
        " String [] getArr() {String[] arr; return this.arr;} ;" + PMD.EOL +
        "}";

    private static final String TEST6 =
        "public class Foo {" + PMD.EOL +
        " int[] getArr() {" + PMD.EOL +
        "  int[] x = new int[] {1,2,3};" + PMD.EOL +
        "  return x;" + PMD.EOL +
        " } ;" + PMD.EOL +
        "}";

    private static final String TEST7 =
        "public class Foo {" + PMD.EOL +
        " Object[] getArr() {" + PMD.EOL +
        "  return new Object[] {foo,bar};" + PMD.EOL +
        " } ;" + PMD.EOL +
        "}";

    private static final String TEST8 =
    "public class Foo {" + PMD.EOL +
    " private Object[] x;" + PMD.EOL +
    " Object[] getArr() {" + PMD.EOL +
    "  return this.x.clone();" + PMD.EOL +
    " } ;" + PMD.EOL +
    "}";

}
