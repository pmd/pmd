/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.rules.ClassNamingConventionsRule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class ClassNamingConventionsRuleTest extends SimpleAggregatorTst {

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "class names should not start with lowercase character", 1, new ClassNamingConventionsRule()),
           new TestDescriptor(TEST2, "class names should not contain underscores", 1, new ClassNamingConventionsRule()),
           new TestDescriptor(TEST3, "all is well", 0, new ClassNamingConventionsRule()),
       });
    }

    private static final String TEST1 =
    "public class foo {};";

    private static final String TEST2 =
    "public class foo_bar {};";

    private static final String TEST3 =
    "public class FooBar {};";

}
