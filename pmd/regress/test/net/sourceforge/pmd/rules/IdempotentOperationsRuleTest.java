/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.rules.IdempotentOperationsRule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class IdempotentOperationsRuleTest extends SimpleAggregatorTst {

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "assignment of a local to itself", 1, new IdempotentOperationsRule()),
           // FIXME
           new TestDescriptor(TEST2, "assignment of one array element to another ", 1, new IdempotentOperationsRule())
       });
    }

    private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    " private void bar() { " + PMD.EOL +
    "  int x = 2;" + PMD.EOL +
    "  x = x;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    " private void bar() { " + PMD.EOL +
    "  int[] x = {2,3};" + PMD.EOL +
    "  x[0] = x[1];" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

}
