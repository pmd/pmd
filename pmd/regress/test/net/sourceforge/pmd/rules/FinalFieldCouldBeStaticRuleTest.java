/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class FinalFieldCouldBeStaticRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("rulesets/design.xml", "FinalFieldCouldBeStatic");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "simple failure case", 1, rule),
           new TestDescriptor(TEST2, "already static, OK", 0, rule),
           new TestDescriptor(TEST3, "non-final, OK", 0, rule),
           new TestDescriptor(TEST4, "non-primitive failure case - only works for String", 1, rule),
           new TestDescriptor(TEST5, "final field that's a thread, OK", 0, rule),
           new TestDescriptor(TEST6, "don't flag interfaces", 0, rule)
       });
    }

    private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    " public final int BAR = 42;" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    " public static final int BAR = 42;" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class Foo {" + PMD.EOL +
    " public int BAR = 42;" + PMD.EOL +
    "}";

    private static final String TEST4 =
    "public class Foo {" + PMD.EOL +
    " public final String BAR = \"42\";" + PMD.EOL +
    "}";

    private static final String TEST5 =
    "public class Foo {" + PMD.EOL +
    " public final Thread BAR = new Thread();" + PMD.EOL +
    "}";

    private static final String TEST6 =
    "public interface Foo {" + PMD.EOL +
    " public final int BAR = 42;" + PMD.EOL +
    "}";

}
