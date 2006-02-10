package test.net.sourceforge.pmd.rules.clone;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class CloneThrowsCloneNotSupportedExceptionTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("clone", "CloneThrowsCloneNotSupportedException");
    }

    public void testAll() {
        runTests(new TestDescriptor[]{
            new TestDescriptor(TEST1, "ok, throws CloneNotSupportedException", 0, rule),
            new TestDescriptor(TEST2, "bad", 1, rule),
            new TestDescriptor(TEST3, "final class, rule does not apply", 0, rule),
            new TestDescriptor(TEST4, "testing with multiple methods", 1, rule),
        });
    }

    private static final String TEST1 =
            "public class Foo {" + PMD.EOL +
            " void clone() throws CloneNotSupportedException {" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST2 =
            "public class Foo {" + PMD.EOL +
            " void clone() {" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST3 =
            "public final class Foo {" + PMD.EOL +
            " void clone() {" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST4 =
            "public class Foo {" + PMD.EOL +
            " void clone() {}" + PMD.EOL +
            " void foo() {}" + PMD.EOL +
            " void bar() {}" + PMD.EOL +
            "}";
}
