package test.net.sourceforge.pmd.rules.optimization;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class AvoidArrayLoopsTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("optimizations", "AvoidArrayLoops");
    }

    public void testAll() {
        runTests(new TestDescriptor[]{
            new TestDescriptor(TEST1, "copy index into array", 0, rule),
            new TestDescriptor(TEST2, "copy one array to another", 1, rule),
            new TestDescriptor(TEST3, "copy via while loop", 1, rule),
            new TestDescriptor(TEST4, "copy involving multiple arrays is ok", 0, rule),
            new TestDescriptor(TEST5, "copy involving method invocation on array element is ok", 0, rule),
            new TestDescriptor(TEST6, "using an offset, still bad", 1, rule),
            new TestDescriptor(TEST7, "nested arrays on LHS, ok", 0, rule),
        });
    }

    private static final String TEST1 =
            "public class Foo {" + PMD.EOL +
            " public void bar() {" + PMD.EOL +
            "   for (int i=0;i<10;i++) {" + PMD.EOL +
            "       a[i] = i;" + PMD.EOL +
            "   }" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST2 =
            "public class Foo {" + PMD.EOL +
            " public void bar() {" + PMD.EOL +
            "   for (int i=0;i<10;i++) {" + PMD.EOL +
            "       a[i] = b[i];" + PMD.EOL +
            "   }" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST3 =
            "public class Foo {" + PMD.EOL +
            " public void bar() {" + PMD.EOL +
            "  int i = 0;" + PMD.EOL +
            "  while (i < 10) {" + PMD.EOL +
            "   a[i] = b[i];" + PMD.EOL +
            "   i++;" + PMD.EOL +
            "  }" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST4 =
            "public class Foo {" + PMD.EOL +
            " public void bar() {" + PMD.EOL +
            "  for (int i=0;i<10;i++) {" + PMD.EOL +
            "   x[i] = b[i] + 1;" + PMD.EOL +
            "   y[i] = a[i] + 2;" + PMD.EOL +
            "  }" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST5 =
            "public class Foo {" + PMD.EOL +
            " public void bar() {" + PMD.EOL +
            "  for (int i=0;i<10;i++) {" + PMD.EOL +
            "   a[i] = b[i].size();" + PMD.EOL +
            "  }" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST6 =
            "public class Foo {" + PMD.EOL +
            " public void bar() {" + PMD.EOL +
            "  for (int i=0;i<10;i++) {" + PMD.EOL +
            "   b[i]=a[i+6];" + PMD.EOL +
            "  }" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST7 =
            "public class Foo {" + PMD.EOL +
            " public void bar() {" + PMD.EOL +
            "  for (int i=0;i<10;i++) {" + PMD.EOL +
            "   b[c[i]] = a[i];" + PMD.EOL +
            "  }" + PMD.EOL +
            " }" + PMD.EOL +
            "}";
}
