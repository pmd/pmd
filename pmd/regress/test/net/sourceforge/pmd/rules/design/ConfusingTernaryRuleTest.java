package test.net.sourceforge.pmd.rules.design;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.rules.design.ConfusingTernary;

public class ConfusingTernaryRuleTest extends SimpleAggregatorTst {

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "!=, bad", 1, new ConfusingTernary()),
           new TestDescriptor(TEST2, "==, good", 0, new ConfusingTernary()),
       });
    }

    private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    " void bar() {" + PMD.EOL +
    "  x = a != b ? c : d;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    " void bar() {" + PMD.EOL +
    "  x = a == b ? c : d;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";


}
