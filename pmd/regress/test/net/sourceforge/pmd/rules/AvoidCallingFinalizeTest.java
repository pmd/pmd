package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class AvoidCallingFinalizeTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("rulesets/design.xml", "AvoidCallingFinalize");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
               new TestDescriptor(TEST1, "TEST1", 1, rule),
               new TestDescriptor(TEST2, "TEST2", 1, rule),
               new TestDescriptor(TEST3, "TEST3", 1, rule),
               new TestDescriptor(TEST4, "TEST4", 0, rule),
       });
    }

    private static final String TEST1 =
        "public class Foo {" + PMD.EOL +
        " void foo () {" +
        "  finalize();" +
        " }" + PMD.EOL +
        "}";

    private static final String TEST2 =
        "public class Foo {" + PMD.EOL +
        " void foo () {" +
        "  Foo f;" +
        "  f.finalize();" +
        " }" + PMD.EOL +
        "}";


    private static final String TEST3 =
        "public class Foo {" + PMD.EOL +
        " void foo () {" +
        "  super.finalize();" +
        " }" + PMD.EOL +
        "}";


    private static final String TEST4 =
        "public class Foo {" + PMD.EOL +
        " void finalize () {" +
        " }" + PMD.EOL +
        "}";
}
