/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class AvoidFieldNameMatchingTypeNameTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("naming", "AvoidFieldNameMatchingTypeName");
    }

    public void testAll() {
        runTests(new TestDescriptor[]{
            new TestDescriptor(TEST1, "TEST1", 1, rule),
            new TestDescriptor(TEST2, "TEST2", 1, rule),
            new TestDescriptor(TEST3, "TEST3", 0, rule),
            new TestDescriptor(TEST4, "TEST4", 1, rule),
        });
    }

    private static final String TEST1 =
            "public class Foo {" + PMD.EOL +
            "   int foo;" + PMD.EOL +
            "} ";

    private static final String TEST2 =
            "public class Foo {" + PMD.EOL +
            "   String foo;" + PMD.EOL +
            "} ";

    private static final String TEST3 =
            "public class Foo {" + PMD.EOL +
            "   class Bar {" + PMD.EOL +
            "		Foo foo;" + PMD.EOL +
            "   }" + PMD.EOL +
            "} ";

    private static final String TEST4 =
            "public class Foo {" + PMD.EOL +
            "   class Bar {" + PMD.EOL +
            "		String bar;" + PMD.EOL +
            "   }" + PMD.EOL +
            "} ";
}
