/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class LooseCouplingTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() {
        rule = findRule("coupling", "LooseCoupling");
    }

    public void testAll() {
        runTests(new TestDescriptor[]{
            new TestDescriptor(TEST1, "returning a HashSet, bad", 1, rule),
            new TestDescriptor(TEST2, "returning a Map, OK", 0, rule),
            new TestDescriptor(TEST3, "no problemo", 0, rule),
            new TestDescriptor(TEST4, "returning a set", 0, rule),
            new TestDescriptor(TEST5, "field declared of type HashSet", 1, rule),
            new TestDescriptor(TEST6, "field, return type both HashSet", 2, rule),
            new TestDescriptor(TEST7, "two fields", 2, rule),
            new TestDescriptor(TEST8, "method param is HashMap", 1, rule),
            new TestDescriptor(TEST9, "Vector could be List", 1, rule),
        });
    }

    private static final String TEST1 =
            "public class Foo {" + PMD.EOL +
            " HashSet foo() {" + PMD.EOL +
            "  return new HashSet();" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST2 =
            "public class Foo {" + PMD.EOL +
            " Map getFoo() {" + PMD.EOL +
            "  return new HashMap();" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST3 =
            "public class Foo {" + PMD.EOL +
            " void foo() {}" + PMD.EOL +
            "}";

    private static final String TEST4 =
            "import java.util.*;" + PMD.EOL +
            "public class Foo {" + PMD.EOL +
            " Set fooSet = new HashSet(); // OK" + PMD.EOL +
            " Set foo() {" + PMD.EOL +
            "  return fooSet;" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST5 =
            "public class Foo {" + PMD.EOL +
            " HashSet fooSet = new HashSet(); // NOT OK" + PMD.EOL +
            "}";

    private static final String TEST6 =
            "public class Foo {" + PMD.EOL +
            " HashSet fooSet = new HashSet(); // NOT OK" + PMD.EOL +
            " HashSet foo() { // NOT OK" + PMD.EOL +
            "  return fooSet;" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST7 =
            "public class Foo {" + PMD.EOL +
            " HashSet fooSet = new HashSet();" + PMD.EOL +
            " HashMap fooMap = new HashMap();" + PMD.EOL +
            "}";

    private static final String TEST8 =
            "public class Foo {" + PMD.EOL +
            " void foo(HashMap bar) {}" + PMD.EOL +
            "}";

    private static final String TEST9 =
            "import java.util.*;" + PMD.EOL +
            "public class Foo {" + PMD.EOL +
            " public void foo(Vector bar) {}" + PMD.EOL +
            "}";

}
