package test.net.sourceforge.pmd.rules;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.PMD;

public class SimplifyConditionalTest extends SimpleAggregatorTst{
    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("rulesets/design.xml", "SimplifyConditional");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "failure case", 1, rule),
           new TestDescriptor(TEST2, "ok", 0, rule),
           new TestDescriptor(TEST3, "transpose x and null, still bad", 1, rule),
           new TestDescriptor(TEST4, "conditional or and !(instanceof)", 1, rule),
       });
    }

    private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    " void bar(Object x) {" + PMD.EOL +
    "  if (x != null && x instanceof String) {}" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    " void bar(Object x) {" + PMD.EOL +
    "  if (x instanceof String) {}" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class Foo {" + PMD.EOL +
    " void bar(Object x) {" + PMD.EOL +
    "  if (null != x && x instanceof String) {}" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST4 =
    "public class Foo {" + PMD.EOL +
    " void bar(Object x) {" + PMD.EOL +
    "  if (x == null || !(x instanceof String)) {}" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

}
