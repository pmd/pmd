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
        runTests(new TestDescriptor[]{
            new TestDescriptor(TEST1, "simple failure case", 1, rule),
            new TestDescriptor(TEST2, "new java.lang.Boolean", 1, rule),
            new TestDescriptor(TEST3, "ok", 0, rule),
            new TestDescriptor(TEST4, "don't use Boolean.valueOf() with literal", 2, rule),
            new TestDescriptor(TEST5, "valueOf() with variable is fine", 0, rule),
            new TestDescriptor(TEST6, "don't use Boolean.valueOf() with string literal", 1, rule),
            new TestDescriptor(TEST7, "don't use Boolean.valueOf() in method call", 1, rule),
            new TestDescriptor(TEST8, "don't use new Boolean() in method call", 1, rule),
            new TestDescriptor(TEST9, "ok", 0, rule),
            new TestDescriptor(TEST10, "ok", 0, rule),
            new TestDescriptor(TEST11, "don't use new Boolean() in static block", 1, rule),
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

    private static final String TEST6 =
        "public class Foo {" + PMD.EOL +
        " Boolean b = Boolean.valueOf(\"true\");" + PMD.EOL +
        "}";

    private static final String TEST7 =
        "public class Foo {" + PMD.EOL +
        "   public void go(){" + PMD.EOL +
        "     foo(new Boolean(\"true\"));" + PMD.EOL +
        "   }" + PMD.EOL +
        "}";

    private static final String TEST8 =
        "public class Foo {" + PMD.EOL +
        "   public void go(){" + PMD.EOL +
        "      foo(Boolean.valueOf(\"true\"));" + PMD.EOL +
        "   }" + PMD.EOL +
        "}";

    private static final String TEST9 =
        "public class Foo {" + PMD.EOL +
        "   public void go(){" + PMD.EOL +
        " foo(Boolean.TRUE);" + PMD.EOL +
        "   }" + PMD.EOL +
        "}";

    private static final String TEST10 =
        "public class Foo {" + PMD.EOL +
        "   static {" + PMD.EOL +
        " foo(Boolean.TRUE);" + PMD.EOL +
        "   }" + PMD.EOL +
        "}";

    private static final String TEST11 =
        "public class Foo {" + PMD.EOL +
        "   static {" + PMD.EOL +
        "      foo(Boolean.valueOf(\"true\"));" + PMD.EOL +
        "   }" + PMD.EOL +
        "}";

        
}
