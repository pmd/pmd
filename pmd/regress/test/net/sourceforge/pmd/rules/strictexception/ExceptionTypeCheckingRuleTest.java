/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules.strictexception;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.rules.strictexception.ExceptionTypeChecking;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class ExceptionTypeCheckingRuleTest extends SimpleAggregatorTst  {

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "checks for NPE", 1, new ExceptionTypeChecking()),
           new TestDescriptor(TEST2, "ok", 0, new ExceptionTypeChecking()),
       });
    }

    private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    " void foo() {" + PMD.EOL +
    "  try {} catch (Exception e) {" + PMD.EOL +
    "   if (e instanceof NullPointerException) {}" + PMD.EOL +
    "  }" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    " void foo() {" + PMD.EOL +
    "  try {} catch (Exception e) {}" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

}
