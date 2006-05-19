package test.net.sourceforge.pmd.rules.j2ee;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class UseProperClassLoaderTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("j2ee", "UseProperClassLoader");
    }

    public void testAll() {
        runTests(new TestDescriptor[]{
            new TestDescriptor(TEST1, "failure case", 1, rule),
            new TestDescriptor(TEST2, "correct way", 0, rule),
        });
    }

    private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    " void bar() { ClassLoader cl = SomeClass.class.getClassLoader(); }" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    " void bar() { ClassLoader cl = Thread.currentThread().getContextClassLoader(); }" + PMD.EOL +
    "}";

}
