package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.PMD;
import test.net.sourceforge.pmd.testframework.TestDescriptor;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class OptimizableToArrayCallRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("rulesets/design.xml", "OptimizableToArrayCallRule");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "failure case", 1, rule),
           new TestDescriptor(TEST2, "Array dimensioner uses method call, ok", 0, rule),
           new TestDescriptor(TEST3, "Array dimensioner uses variable, ok", 0, rule),
       });
    }

    private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    " {x.toArray(new Foo[0]);}" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    " {x.toArray(new Foo[x.size()]);}" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class Foo {" + PMD.EOL +
    " {x.toArray(new Foo[y]);}" + PMD.EOL +
    "}";
}
