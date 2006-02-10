package test.net.sourceforge.pmd.rules.migrating;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class AvoidEnumAsIdentifierTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("migrating", "AvoidEnumAsIdentifier");
    }

    public void testAll() {
        runTests(new TestDescriptor[]{
            new TestDescriptor(TEST1, "variable and param named enum", 2, rule),
        });
    }

    private static final String TEST1 =
            "public class Foo {" + PMD.EOL +
            " void bar(String enum) {" + PMD.EOL +
            "  String enum = \"hi\";" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

}
