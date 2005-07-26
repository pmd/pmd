package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class UselessOverridingMethodTest extends SimpleAggregatorTst {
    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("rulesets/basic.xml", "UselessOverridingMethod");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
               new TestDescriptor(TEST1, "return value returned by super()", 1, rule),
               new TestDescriptor(TEST2, "void method", 1, rule),
               new TestDescriptor(TEST3, "return concatenated strings expression", 0, rule),
               new TestDescriptor(TEST4, "return method call that uses param", 0, rule),
               new TestDescriptor(TEST5, "use superclass method with expression", 0, rule),
       });
    }


    private static final String TEST1 =
    "public class Bar {" + PMD.EOL +
    " String foo() {" + PMD.EOL +
    "  return super.foo(); " + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Buz{" + PMD.EOL +
    " public void foo(String bar) {" + PMD.EOL +
    "  super.foo(bar); " + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class Buz{" + PMD.EOL +
    " public String foo(String bar) {" + PMD.EOL +
    "   return \"\" + super.foo(bar); " + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST4 =
    "public class Buz{" + PMD.EOL +
    " public String foo(String bar) {" + PMD.EOL +
    "   return super.foo(buz(bar)); " + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST5 =
    "public class Buz{" + PMD.EOL +
    " public String foo(String bar) {" + PMD.EOL +
    "   return super.foo(\"\" + bar); " + PMD.EOL +
    " }" + PMD.EOL +
    "}";

}
