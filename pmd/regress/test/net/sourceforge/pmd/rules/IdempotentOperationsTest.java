/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.rules.IdempotentOperations;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class IdempotentOperationsTest extends SimpleAggregatorTst {

    public void testAll() {
        runTests(new TestDescriptor[]{
            new TestDescriptor(TEST1, "assignment of a variable (local or field) to itself", 1, new IdempotentOperations()),
            new TestDescriptor(TEST2, "assignment of one array element to another", 0, new IdempotentOperations()),
            new TestDescriptor(TEST3, "qualified names causing NPE troubleshooting", 0, new IdempotentOperations()),
            new TestDescriptor(TEST4, "check for method calls", 0, new IdempotentOperations()),
            new TestDescriptor(TEST5, "compound assignments are OK", 0, new IdempotentOperations())
        });
    }

    private static final String TEST1 =
            "public class Foo {" + PMD.EOL +
            " private void bar() { " + PMD.EOL +
            "  x = x;" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST2 =
            "public class Foo {" + PMD.EOL +
            " private void bar() { " + PMD.EOL +
            "  int[] x = {2,3};" + PMD.EOL +
            "  x = x[1];" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST3 =
            "public class Foo {" + PMD.EOL +
            "  void bar() {this.x = foo;}" + PMD.EOL +
            "  void buz() {foo = this.x;}" + PMD.EOL +
            "}";

    private static final String TEST4 =
            "public class Foo {" + PMD.EOL +
            "  void bar() {x = x();}" + PMD.EOL +
            "}";

    private static final String TEST5 =
            "public class Foo {" + PMD.EOL +
            "  void bar() {x += x;}" + PMD.EOL +
            "}";

}
