package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class UseNotifyAllInsteadOfNotifyTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("design", "UseNotifyAllInsteadOfNotify");
    }

    public void testAll() {
        runTests(new TestDescriptor[]{
            new TestDescriptor(TEST1, "TEST1", 1, rule),
            new TestDescriptor(TEST2, "TEST2", 2, rule),
            new TestDescriptor(TEST3, "TEST3", 1, rule),
            new TestDescriptor(TEST4, "TEST4", 1, rule),
            new TestDescriptor(TEST5, "TEST5", 1, rule),
        });
    }

    private static final String TEST1 =
            "public class Foo {" + PMD.EOL +
            " void foo () {" + PMD.EOL +
            " foo.notify();" + PMD.EOL +
            "}" + PMD.EOL +
            "}";

    private static final String TEST2 =
            "public class Foo {" + PMD.EOL +
            " void foo () {" + PMD.EOL +
            " foo.notify();" + PMD.EOL +
            " foo.notify();" + PMD.EOL +
            "}" + PMD.EOL +
            "}";

    private static final String TEST3 =
            "public class Foo {" + PMD.EOL +
            " void foo () {" + PMD.EOL +
            " notify();" + PMD.EOL +
            "}" + PMD.EOL +
            "}";

    private static final String TEST4 =
            "public class Foo {" + PMD.EOL +
            " void foo () {" + PMD.EOL +
            " super.notify();" + PMD.EOL +
            "}" + PMD.EOL +
            "}";

    private static final String TEST5 =
            "public class Foo {" + PMD.EOL +
            " void foo () {" + PMD.EOL +
            " new Object().notify();" + PMD.EOL +
            "}" + PMD.EOL +
            "}";

}
