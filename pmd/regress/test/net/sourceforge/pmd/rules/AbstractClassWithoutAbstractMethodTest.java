package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class AbstractClassWithoutAbstractMethodTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("rulesets/design.xml", "AbstractClassWithoutAbstractMethod");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
       		new TestDescriptor(TEST1, "TEST1", 0, rule),
			new TestDescriptor(TEST2, "TEST2", 1, rule),
			new TestDescriptor(TEST3, "TEST3", 1, rule),
			new TestDescriptor(TEST4, "TEST4", 0, rule),
       });
    }

    private static final String TEST1 =
    "public class Foo {}";

    private static final String TEST2 =
        "public abstract class Foo {}";

    private static final String TEST3 =
        "public abstract class Foo {" + PMD.EOL +
        "   int bar() {} " + PMD.EOL + 
        "}";

    private static final String TEST4 =
        "public abstract class Foo {" + PMD.EOL +
        "   abstract int bar(); " + PMD.EOL + 
        "}";

}
