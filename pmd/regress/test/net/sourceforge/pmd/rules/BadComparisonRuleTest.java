package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.PMD;
import test.net.sourceforge.pmd.testframework.TestDescriptor;
import test.net.sourceforge.pmd.testframework.RuleTst;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class BadComparisonRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("rulesets/design.xml", "BadComparisonRule");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "comparison to Double.NaN", 1, rule),
           new TestDescriptor(TEST2, "ok equality comparison", 0, rule),
           new TestDescriptor(TEST3, "comparison to Float.NaN", 1, rule),
       });
    }

    private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    " boolean x = (y == Double.NaN);" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    " boolean x = (y == z);" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class Foo {" + PMD.EOL +
    " boolean x = (y == Float.NaN);" + PMD.EOL +
    "}";

}
