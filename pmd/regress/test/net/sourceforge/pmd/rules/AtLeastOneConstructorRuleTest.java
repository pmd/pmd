/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.rules.AtLeastOneConstructorRule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class AtLeastOneConstructorRuleTest extends SimpleAggregatorTst {

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "ok", 0, new AtLeastOneConstructorRule()),
           new TestDescriptor(TEST2, "simple failure case", 1, new AtLeastOneConstructorRule()),
           new TestDescriptor(TEST3, "inner bad, outer ok", 1, new AtLeastOneConstructorRule()),
           new TestDescriptor(TEST4, "inner ok, outer bad", 1, new AtLeastOneConstructorRule()),
           new TestDescriptor(TEST5, "inner and outer both bad", 2, new AtLeastOneConstructorRule()),
           new TestDescriptor(TEST6, "inner and outer both ok", 0, new AtLeastOneConstructorRule()),
       });
    }

    private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    " public Foo() {}" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class Foo {" + PMD.EOL +
    " public class Bar {}" + PMD.EOL +
    " public Foo() {}" + PMD.EOL +
    "}";

    private static final String TEST4 =
    "public class Foo {" + PMD.EOL +
    " public class Bar { " + PMD.EOL +
    "  public Bar() {}" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST5 =
    "public class Foo {" + PMD.EOL +
    " public class Bar { " + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST6 =
    "public class Foo {" + PMD.EOL +
    " public class Bar { " + PMD.EOL +
    "  public Bar() {}" + PMD.EOL +
    " }" + PMD.EOL +
    " public Foo() {}" + PMD.EOL +
    "}";


}
