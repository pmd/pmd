package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class AbstractClassDoesNotContainAbstractMethodTest extends SimpleAggregatorTst {
    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("rulesets/design.xml", "AbstractClassDoesNotContainAbstractMethod");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "", 0, rule),
           new TestDescriptor(TEST2, "", 1, rule),
           new TestDescriptor(TEST3, "", 1, rule),
           new TestDescriptor(TEST4, "", 0, rule),
       });
    }

    private static final String TEST1 =
        "public class Foo {" + PMD.EOL +
        " public void foo() { }" + PMD.EOL +
        "}";
    
    private static final String TEST2 =
        "public abstract class Foo {" + PMD.EOL +
        " public void foo() { }" + PMD.EOL +
        "}";
    
    private static final String TEST3 =
        "public abstract class Foo {" + PMD.EOL +
        " public void foo() { }" + PMD.EOL +
        " public void foo2() { }" + PMD.EOL +
        "}";


    private static final String TEST4 =
        "public abstract class Foo {" + PMD.EOL +
        " public void foo() { }" + PMD.EOL +
        " public abstract void foo2() ;" + PMD.EOL +
        "}";
    
}
