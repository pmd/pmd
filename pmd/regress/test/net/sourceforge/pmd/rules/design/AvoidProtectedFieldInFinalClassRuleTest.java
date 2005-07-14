package test.net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class AvoidProtectedFieldInFinalClassRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("design", "AvoidProtectedFieldInFinalClass");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "ok, protected field in non final class", 0, rule),
           new TestDescriptor(TEST2, "bad, protected field in final class", 1, rule),
           new TestDescriptor(TEST3, "ok, private field in final class", 0, rule),
       });
    }

    private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    " protected int x;" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public final class Foo {" + PMD.EOL +
    " protected int x;" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public final class Foo {" + PMD.EOL +
    " private int x;" + PMD.EOL +
    "}";

}
