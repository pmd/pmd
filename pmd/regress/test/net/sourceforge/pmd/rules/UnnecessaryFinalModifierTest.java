package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class UnnecessaryFinalModifierTest extends SimpleAggregatorTst {
    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("rulesets/newrules.xml", "UnnecessaryFinalModifier");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "", 0, rule),
           new TestDescriptor(TEST2, "", 0, rule),
           new TestDescriptor(TEST3, "", 0, rule),
           new TestDescriptor(TEST4, "", 1, rule),
           new TestDescriptor(TEST5, "", 1, rule),
           new TestDescriptor(TEST6, "", 3, rule),
       });
    }

    private static final String TEST1 =
        "public class Foo {" + PMD.EOL +
        " public void foo() { }" + PMD.EOL +
        "}";
    
    private static final String TEST2 =
        "public class Foo {" + PMD.EOL +
        " public final void foo() { }" + PMD.EOL +
        "}";
    
    private static final String TEST3 =
        "public class Foo {" + PMD.EOL +
        " public final void foo() { }" + PMD.EOL +
        " public void foo2() { }" + PMD.EOL +
        "}";


    private static final String TEST4 =
        "public final class Foo {" + PMD.EOL +
        " public final void foo() { }" + PMD.EOL +
        "}";

    private static final String TEST5 =
        "public final class Foo {" + PMD.EOL +
        " public final void foo() { }" + PMD.EOL +
        " public void foo2() { }" + PMD.EOL +
        "}";

    private static final String TEST6 =
        "public final class Foo {" + PMD.EOL +
        " public final void fooA() { }" + PMD.EOL +
        " public final void fooS() { }" + PMD.EOL +
        " public final void fooD() { }" + PMD.EOL +
        " public void foo2() { }" + PMD.EOL +
        "}";
    
}
