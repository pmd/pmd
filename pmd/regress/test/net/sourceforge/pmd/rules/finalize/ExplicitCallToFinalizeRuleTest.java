package test.net.sourceforge.pmd.rules.finalize;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class ExplicitCallToFinalizeRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("rulesets/finalizers.xml", "ExplicitCallToFinalize");
    }

    // todo: how do we flag this.finalize() ?
    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "call to own finalizer", 1, rule),
           new TestDescriptor(TEST2, "call to other object's finalizer", 1, rule),
           new TestDescriptor(TEST3, "call to this.finalize, TODO", 0, rule),
           new TestDescriptor(TEST4, "ok, call to super.finalize", 0, rule),
           new TestDescriptor(TEST5, "ok, call to overloaded finalizer", 0, rule),
           new TestDescriptor(TEST6, "ok, call to overloaded this.finalizer", 0, rule),
       });
    }

    private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    " void foo() {finalize();}" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    " void foo(Bar b) {b.finalize();}" + PMD.EOL +
    "}";

    // TODO this should be a rule violation
    private static final String TEST3 =
    "public class Foo {" + PMD.EOL +
    " void foo() {this.finalize();}" + PMD.EOL +
    "}";

    private static final String TEST4 =
    "public class Foo {" + PMD.EOL +
    " void foo() {super.finalize();}" + PMD.EOL +
    "}";

    private static final String TEST5 =
    "public class Foo {" + PMD.EOL +
    " void foo() {finalize(5);}" + PMD.EOL +
    " void finalize(int x) {}" + PMD.EOL +
    "}";

    private static final String TEST6 =
    "public class Foo {" + PMD.EOL +
    " void foo() {this.finalize(5);}" + PMD.EOL +
    " void finalize(int x) {}" + PMD.EOL +
    "}";
}
