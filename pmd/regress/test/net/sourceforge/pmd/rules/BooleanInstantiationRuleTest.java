/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class BooleanInstantiationRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("basic", "BooleanInstantiation");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "simple failure case", 1, rule),
           new TestDescriptor(TEST2, "new java.lang.Boolean", 1, rule),
           new TestDescriptor(TEST3, "ok", 0, rule),
           new TestDescriptor(TEST4, "don't use Boolean.valueOf() with literal", 2, rule),
           new TestDescriptor(TEST5, "valueOf() with variable is fine", 0, rule),
       });
    }

    private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    " Boolean b = new Boolean(\"true\");" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    " Boolean b = new java.lang.Boolean(\"true\");" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class Foo {" + PMD.EOL +
    " Boolean b = Boolean.TRUE;" + PMD.EOL +
    "}";

    private static final String TEST4 =
    "public class Foo {" + PMD.EOL +
    " Boolean b = Boolean.valueOf(true);" + PMD.EOL +
    " Boolean b1 = Boolean.valueOf(false);" + PMD.EOL +
    "}";

    private static final String TEST5 =
    "public class Foo {" + PMD.EOL +
    " Boolean b = Boolean.valueOf(x);" + PMD.EOL +
    "}";

}
