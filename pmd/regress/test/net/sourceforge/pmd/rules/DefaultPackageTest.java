package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class DefaultPackageTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("controversial", "DefaultPackage");
    }

    public void testAll() {
        runTests(new TestDescriptor[]{
            new TestDescriptor(TEST1, "ok", 0, rule),
            new TestDescriptor(TEST2, "bad", 1, rule),
            new TestDescriptor(TEST3, "interface methods are always public", 0, rule),
            new TestDescriptor(TEST4, "interface field are always public", 0, rule),
        });
    }

    private static final String TEST1 =
            "public class Foo {" + PMD.EOL +
            " private int x;" + PMD.EOL +
            "}";

    private static final String TEST2 =
            "public class Foo {" + PMD.EOL +
            " int x;" + PMD.EOL +
            "}";

    private static final String TEST3 =
            "public interface Foo {" + PMD.EOL +
            " void bar();" + PMD.EOL +
            "}";

    private static final String TEST4 =
            "public interface Foo {" + PMD.EOL +
            " int bar = 10;" + PMD.EOL +
            "}";

}

