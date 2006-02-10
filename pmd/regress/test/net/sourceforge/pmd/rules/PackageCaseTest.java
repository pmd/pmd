package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class PackageCaseTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("naming", "PackageCase");
    }

    public void testAll() {
        runTests(new TestDescriptor[]{
            new TestDescriptor(TEST1, "bad", 1, rule),
            new TestDescriptor(TEST2, "good", 0, rule),
        });
    }

    private static final String TEST1 =
            "package foo.BarBuz;" + PMD.EOL +
            "public class Foo {" + PMD.EOL +
            "}";

    private static final String TEST2 =
            "package foo.bar;" + PMD.EOL +
            "public class Foo {" + PMD.EOL +
            "}";


}
