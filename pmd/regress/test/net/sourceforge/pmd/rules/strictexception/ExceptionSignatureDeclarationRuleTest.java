/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules.strictexception;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.rules.strictexception.ExceptionSignatureDeclaration;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class ExceptionSignatureDeclarationRuleTest extends SimpleAggregatorTst {

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "method throws Exception", 1, new ExceptionSignatureDeclaration()),
           new TestDescriptor(TEST2, "ok", 0, new ExceptionSignatureDeclaration()),
           new TestDescriptor(TEST3, "constructor throws Exception", 1, new ExceptionSignatureDeclaration()),
       });
    }

    private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    " void foo() throws Exception {}" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    " void foo() {}" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class Foo {" + PMD.EOL +
    " Foo() throws Exception {}" + PMD.EOL +
    "}";


}
