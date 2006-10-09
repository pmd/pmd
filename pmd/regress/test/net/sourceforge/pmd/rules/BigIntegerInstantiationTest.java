/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class BigIntegerInstantiationTest extends SimpleAggregatorTst {

    private Rule rule14;
    private Rule rule15;

    public void setUp() throws RuleSetNotFoundException {
        rule14 = findRule("basic", "BigIntegerInstantiation_1.4");
        rule15 = findRule("basic", "BigIntegerInstantiation_1.5");
    }

    public void testAll() {
        runTests(new TestDescriptor[]{
                new TestDescriptor(TEST1, "Fail, BigInteger(1)", 1, rule14),
                new TestDescriptor(TEST2, "Pass, BigInteger(10)", 0, rule14),
                new TestDescriptor(TEST3, "Fail, BigInteger(0)", 1, rule14),
            });
        runTests(new TestDescriptor[]{
                new TestDescriptor(TEST1, "Fail, BigInteger(1)", 1, rule15),
                new TestDescriptor(TEST2, "Fail, BigInteger(10)", 1, rule15),
                new TestDescriptor(TEST3, "Fail, BigInteger(0)", 1, rule15),
            });
    }

    private static final String TEST1 =
        "public class Foo {" + PMD.EOL +
        " BigInteger b = new BigInteger(1);" + PMD.EOL +
        "}";

    private static final String TEST2 =
        "public class Foo {" + PMD.EOL +
        " BigInteger b = new BigInteger(10);" + PMD.EOL +
        "}";

    private static final String TEST3 =
        "public class Foo {" + PMD.EOL +
        " BigInteger b = new BigInteger(0);" + PMD.EOL +
        "}";

        
}
