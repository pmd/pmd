package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class AvoidDollarSignsRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("rulesets/naming.xml", "AvoidDollarSigns");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "class Fo$o", 1, rule),
           new TestDescriptor(TEST2, "variable fo$oo", 1, rule),
           new TestDescriptor(TEST3, "method foo$oo", 1, rule),
           new TestDescriptor(TEST4, "interface fo$oo", 1, rule),
           new TestDescriptor(TEST5, "ok", 0, rule),
       });
    }

    private static final String TEST1 =
    "public class F$oo {}";

    private static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    " int fo$o;" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class Foo {" + PMD.EOL +
    " void fo$o() {}" + PMD.EOL +
    "}";

    private static final String TEST4 =
    "public interface Foo$oo {}";

    private static final String TEST5 =
    "public class Foo {" + PMD.EOL +
    " void foo() {}" + PMD.EOL +
    " int buz;" + PMD.EOL +
    "}" + PMD.EOL +
    "public interface Baz {} ";

}
