/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules.strictexception;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.rules.strictexception.AvoidCatchingThrowable;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class AvoidCatchingThrowableRuleTest extends SimpleAggregatorTst {

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "simple failure case", 1, new AvoidCatchingThrowable()),
           new TestDescriptor(TEST2, "ok", 0, new AvoidCatchingThrowable()),
       });
    }

    private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    " void foo() {" + PMD.EOL +
    "  try {} catch (Throwable t) {}   " + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    " void foo() {" + PMD.EOL +
    "  try {} catch (RuntimeException t) {}   " + PMD.EOL +
    " }" + PMD.EOL +
    "}";
}
