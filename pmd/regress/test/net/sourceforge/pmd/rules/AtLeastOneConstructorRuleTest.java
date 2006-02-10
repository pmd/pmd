/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class AtLeastOneConstructorRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("controversial", "AtLeastOneConstructor");
    }

    public void testAll() {
        runTests(new TestDescriptor[]{
            new TestDescriptor(TEST1, "ok", 0, rule),
            new TestDescriptor(TEST2, "simple failure case", 1, rule),
            new TestDescriptor(TEST3, "inner bad, outer ok", 1, rule),
            new TestDescriptor(TEST4, "inner ok, outer bad", 1, rule),
            new TestDescriptor(TEST5, "inner and outer both bad", 2, rule),
            new TestDescriptor(TEST6, "inner and outer both ok", 0, rule),
            new TestDescriptor(TEST7, "skip interfaces", 0, rule),
        });
    }

    private static final String TEST1 =
            "public class Foo {" + PMD.EOL +
            " public Foo() {}" + PMD.EOL +
            "}";

    private static final String TEST2 =
            "public class Foo {}";

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
            " public class Bar {} " + PMD.EOL +
            "}";

    private static final String TEST6 =
            "public class Foo {" + PMD.EOL +
            " public class Bar { " + PMD.EOL +
            "  public Bar() {}" + PMD.EOL +
            " }" + PMD.EOL +
            " public Foo() {}" + PMD.EOL +
            "}";

    private static final String TEST7 =
            "public interface Foo {" + PMD.EOL +
            "}";

}
