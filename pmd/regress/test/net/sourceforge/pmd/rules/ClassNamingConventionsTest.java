/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.rules.ClassNamingConventions;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class ClassNamingConventionsTest extends SimpleAggregatorTst {

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "class names should not start with lowercase character", 1, new ClassNamingConventions()),
           new TestDescriptor(TEST2, "all is well", 0, new ClassNamingConventions()),
       });
    }

    private static final String TEST1 =
    "public class foo {};";

    private static final String TEST2 =
    "public class FooBar {};";

}
