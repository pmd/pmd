/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules.strings;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class StringBufferInstantiationWithCharTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws Exception {
        rule = findRule("strings", "StringBufferInstantiationWithChar");
    }

    public void testAll() {
        runTests(new TestDescriptor[] {
                new TestDescriptor(TEST1, "OK", 0, rule),
                new TestDescriptor(TEST2, "failure case", 1, rule),
       });
    }

    private static final String TEST1 =
        "public class Foo {" + PMD.EOL +
        "  StringBuffer sb = new StringBuffer(\"c\");" + PMD.EOL +
        "}";

    private static final String TEST2 =
        "public class Foo {" + PMD.EOL +
        "  StringBuffer sb = new StringBuffer('c');" + PMD.EOL +
        "}";

}