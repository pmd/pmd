package test.net.sourceforge.pmd.rules.optimization;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class UnnecessaryWrapperObjectCreationTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("optimizations", "UnnecessaryWrapperObjectCreation");
    }

    public void testAll() {
        runTests(new TestDescriptor[]{
            new TestDescriptor(TEST1, "failure case", 1, rule),
            new TestDescriptor(TEST2, "calling valueOf is OK", 0, rule),
        });
    }

    private static final String TEST1 =
            "public class Bar {" + PMD.EOL +
            " void foo(float value) {" + PMD.EOL +
            "  float border = Float.valueOf(value).floatValue();" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST2 =
            "public class Bar {" + PMD.EOL +
            " void foo(float value) {" + PMD.EOL +
            "  Float f = Float.valueOf(value);" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

}

