package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class SimplifyConditionalTest extends SimpleAggregatorTst {
    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("design", "SimplifyConditional");
    }

    public void testAll() {
        runTests(new TestDescriptor[]{
            new TestDescriptor(TEST1, "failure case", 1, rule),
            new TestDescriptor(TEST2, "ok", 0, rule),
            new TestDescriptor(TEST3, "transpose x and null, still bad", 1, rule),
            new TestDescriptor(TEST4, "conditional or and !(instanceof)", 1, rule),
            new TestDescriptor(TEST5, "indexing into array is ok", 0, rule),
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

    private static final String TEST5 =
            "public class Foo {" + PMD.EOL +
            " void bar(Object x) {" + PMD.EOL +
            "  if (x != null && x[0] instanceof String) {}" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

}
