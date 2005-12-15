package test.net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class AvoidConstantsInterfaceTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("design", "AvoidConstantsInterface");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
               new TestDescriptor(TEST1, "clear rule violation", 1, rule),
               new TestDescriptor(TEST2, "ok", 0, rule),
       });
    }

    private static final String TEST1 =
    "public interface Foo {" + PMD.EOL +
    " public static final int FOO = 2;" + PMD.EOL +
    " public static final String BAR = \"bar\";" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public interface Foo {" + PMD.EOL +
    " public static final int FOO = 2;" + PMD.EOL +
    " public void buz();" + PMD.EOL +
    "}";

}
