package test.net.sourceforge.pmd.rules;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.PMD;

public class SuspiciousEqualsMethodNameRuleTest extends SimpleAggregatorTst {
    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("rulesets/scratchpad.xml", "SuspiciousEqualsMethodName");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "bad, equals(Foo foo)", 1, rule),
           new TestDescriptor(TEST2, "ok, equals(Object foo)", 0, rule),
       });
    }

    private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    " public boolean equals(Foo foo) {return true;}" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    " public boolean equals(Object foo) {return true;}" + PMD.EOL +
    "}";

}
