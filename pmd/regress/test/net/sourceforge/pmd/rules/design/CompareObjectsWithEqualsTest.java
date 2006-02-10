package test.net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class CompareObjectsWithEqualsTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("design", "CompareObjectsWithEquals");
    }

    public void testAll() {
        runTests(new TestDescriptor[]{
            new TestDescriptor(TEST1, "simple failure with method params", 1, rule),
            new TestDescriptor(TEST2, "primitives are ok", 0, rule),
            new TestDescriptor(TEST3, "skip nulls", 0, rule),
            new TestDescriptor(TEST4, "missed hit - qualified names.  that's ok, we can't resolve the types yet, so better to skip this for now", 0, rule),
            new TestDescriptor(TEST5, "more qualified name skippage", 0, rule),
            new TestDescriptor(TEST6, "locals", 1, rule),
            new TestDescriptor(TEST7, "2 locals declared on one line", 1, rule),
            new TestDescriptor(TEST8, "array element comparison", 0, rule),
        });
    }

    private static final String TEST1 =
            "public class Foo {" + PMD.EOL +
            " boolean bar(String a, String b) {" + PMD.EOL +
            "  return a == b;" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST2 =
            "public class Foo {" + PMD.EOL +
            " boolean bar(int a, int b) {" + PMD.EOL +
            "  return a == b;" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST3 =
            "public class Foo {" + PMD.EOL +
            " boolean bar(int a, int b) {" + PMD.EOL +
            "  return a == null || null == b;" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST4 =
            "public class Foo {" + PMD.EOL +
            " boolean bar(Foo b) {" + PMD.EOL +
            "  return this.b == b.foo;" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST5 =
            "public class Foo {" + PMD.EOL +
            " boolean bar(String a, String b) {" + PMD.EOL +
            "  return a.charAt(0) == b.charAt(0);" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST6 =
            "public class Foo {" + PMD.EOL +
            " boolean bar() {" + PMD.EOL +
            "  String a = \"foo\";" + PMD.EOL +
            "  String b = \"bar\";" + PMD.EOL +
            "  return a == b;" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST7 =
            "public class Foo {" + PMD.EOL +
            " void bar() {" + PMD.EOL +
            "  String a,b;" + PMD.EOL +
            "  a = \"foo\";" + PMD.EOL +
            "  b = \"bar\";" + PMD.EOL +
            "  if (a == b) {}" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST8 =
            "public class Foo {" + PMD.EOL +
            " void bar(int[] a, String[] b) {" + PMD.EOL +
            "  if (a[1] == b[1]) {}" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

}
