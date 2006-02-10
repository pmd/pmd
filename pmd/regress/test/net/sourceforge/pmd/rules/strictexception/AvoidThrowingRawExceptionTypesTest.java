package test.net.sourceforge.pmd.rules.strictexception;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class AvoidThrowingRawExceptionTypesTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("strictexception", "AvoidThrowingRawExceptionTypes");
    }

    public void testAll() {
        runTests(new TestDescriptor[]{
            new TestDescriptor(TEST1, "throwing various types", 4, rule),
        });
    }

    private static final String TEST1 =
            "public class Foo {" + PMD.EOL +
            " void bar() {" + PMD.EOL +
            "  throw new Throwable();" + PMD.EOL +
            "  throw new Exception();" + PMD.EOL +
            "  throw new Error();" + PMD.EOL +
            "  throw new RuntimeException();" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

}
