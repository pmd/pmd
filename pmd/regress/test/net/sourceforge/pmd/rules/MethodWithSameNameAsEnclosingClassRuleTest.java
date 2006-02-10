package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class MethodWithSameNameAsEnclosingClassRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("naming", "MethodWithSameNameAsEnclosingClass");
    }

    public void testAll() {
        runTests(new TestDescriptor[]{
            new TestDescriptor(TEST1, "bad", 1, rule),
            new TestDescriptor(TEST2, "ok", 0, rule),
            new TestDescriptor(TEST3, "doesn't crash on interfaces", 0, rule),
        });
    }

    private static final String TEST1 =
            "public class Foo {" + PMD.EOL +
            " int Foo(double x) {}" + PMD.EOL +
            "}";

    private static final String TEST2 =
            "public class Foo {" + PMD.EOL +
            " public Foo() {}" + PMD.EOL +
            "}";

    private static final String TEST3 =
            "public interface Foo {" + PMD.EOL +
            " void voo();" + PMD.EOL +
            "}";
}
