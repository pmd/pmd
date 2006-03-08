package test.net.sourceforge.pmd.rules;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.PMD;

public class AvoidThreadGroupTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("basic", "AvoidThreadGroup");
    }

    public void testAll() {
        runTests(new TestDescriptor[]{
            new TestDescriptor(TEST1, "bad, using new ThreadGroup()", 1, rule),
            new TestDescriptor(TEST2, "bad, using Thread.getThreadGroup()", 1, rule),
            new TestDescriptor(TEST3, "bad, using System.getSecurityManager().getThreadGroup()", 1, rule),
        });
    }

    private static final String TEST1 =
            "public class Foo {" + PMD.EOL +
            " void bar() {" + PMD.EOL +
            "  ThreadGroup t = new ThreadGroup(\"my tg\");" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST2 =
            "public class Foo {" + PMD.EOL +
            " void bar() {" + PMD.EOL +
            "  ThreadGroup t = Thread.currentThread().getThreadGroup();" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST3 =
            "public class Foo {" + PMD.EOL +
            " void bar() {" + PMD.EOL +
            "  ThreadGroup t = System.getSecurityManager().getThreadGroup();" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

}
