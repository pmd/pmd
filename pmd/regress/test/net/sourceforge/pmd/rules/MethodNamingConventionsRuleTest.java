/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.rules.MethodNamingConventionsRule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class MethodNamingConventionsRuleTest extends SimpleAggregatorTst {

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "method names should start with lowercase character", 1, new MethodNamingConventionsRule()),
           new TestDescriptor(TEST2, "method names should not contain underscores", 1, new MethodNamingConventionsRule()),
           new TestDescriptor(TEST3, "all is well", 0, new MethodNamingConventionsRule()),
       });
    }

    private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    " void Bar() {}" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    " void bar_foo() {}" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class Foo {" + PMD.EOL +
    " void foo() {}" + PMD.EOL +
    "}";
}
