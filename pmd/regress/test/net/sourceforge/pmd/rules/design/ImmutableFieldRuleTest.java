package test.net.sourceforge.pmd.rules.design;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;

public class ImmutableFieldRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("rulesets/newrules.xml", "ImmutableFieldRule");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "could be immutable, only assigned in constructor", 1, rule),
           new TestDescriptor(TEST2, "could be immutable, only assigned in decl", 1, rule),
           new TestDescriptor(TEST3, "ok, assigned twice", 0, rule),
       });
    }

    private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    " private int x;" + PMD.EOL +
    " public Foo() {" + PMD.EOL +
    "  x = 2;" + PMD.EOL +
    " }       " + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    " private int x = 42;" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class Foo {" + PMD.EOL +
    " private int x;" + PMD.EOL +
    " public Foo() {" + PMD.EOL +
    "  x = 41;" + PMD.EOL +
    " }       " + PMD.EOL +
    " public void bar() {" + PMD.EOL +
    "  x = 42;" + PMD.EOL +
    " }       " + PMD.EOL +
    "}";

}
