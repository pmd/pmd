package test.net.sourceforge.pmd.rules.design;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.stat.Metric;
import net.sourceforge.pmd.rules.design.UseSingleton;

public class UnnecessaryLocalBeforeReturnRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() {
        rule = findRule("rulesets/design.xml", "UnnecessaryLocalBeforeReturn");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "skip void/native/abstract methods", 0, rule),
           new TestDescriptor(TEST2, "skip literal returns", 0, rule),
           new TestDescriptor(TEST3, "simple failure case", 1, rule),
           new TestDescriptor(TEST4, "skip complicated returns", 0, rule),
       });
    }

    private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    " public void bar() {}" + PMD.EOL +
    " public native int buz();" + PMD.EOL +
    " public abstract int baz();" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    " public int bar() { return 5; }" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class Foo {" + PMD.EOL +
    " public int bar() {" + PMD.EOL +
    "  int x = doSomething();" + PMD.EOL +
    "  return x;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST4 =
    "public class Foo {" + PMD.EOL +
    " public int bar() {" + PMD.EOL +
    "  int x = doSomething();" + PMD.EOL +
    "  return x == null ? foo : bar;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

}
