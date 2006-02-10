package test.net.sourceforge.pmd.rules.clone;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class CloneMethodMustImplementCloneableTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("clone", "CloneMethodMustImplementCloneable");
    }

    public void testAll() {
        runTests(new TestDescriptor[]{
            new TestDescriptor(TEST1, "ok, implements Cloneable", 0, rule),
            new TestDescriptor(TEST2, "bad, doesn't implement Cloneable", 1, rule),
            new TestDescriptor(TEST3, "ok, not Object.clone since method has a param", 0, rule),
        });
    }

    private static final String TEST1 =
            "public class Foo implements Cloneable {" + PMD.EOL +
            " void clone() {}" + PMD.EOL +
            "}";

    private static final String TEST2 =
            "public class Foo {" + PMD.EOL +
            " void clone() {}" + PMD.EOL +
            "}";

    private static final String TEST3 =
            "public class Foo {" + PMD.EOL +
            " void clone(int x) {}" + PMD.EOL +
            "}";
}
