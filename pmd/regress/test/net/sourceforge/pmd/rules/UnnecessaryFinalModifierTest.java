package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class UnnecessaryFinalModifierTest extends SimpleAggregatorTst {
    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("basic", "UnnecessaryFinalModifier");
    }

    public void testAll() {
        runTests(new TestDescriptor[]{
            new TestDescriptor(TEST1, "TEST1", 0, rule),
            new TestDescriptor(TEST2, "TEST2", 0, rule),
            new TestDescriptor(TEST3, "TEST3", 0, rule),
            new TestDescriptor(TEST4, "TEST4", 1, rule),
            new TestDescriptor(TEST5, "TEST5", 1, rule),
            new TestDescriptor(TEST6, "TEST6", 3, rule),
            new TestDescriptor(TEST7, "final method in inner class of non-final outer class", 0, rule),
            new TestDescriptor(TEST8, "final method in inner final class ", 1, rule),
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

    private static final String TEST7 =
            "public final class Foo {" + PMD.EOL +
            " public static class Bar {" + PMD.EOL +
            "  public final void buz() {}" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST8 =
            "public final class Foo {" + PMD.EOL +
            " public final class Bar {" + PMD.EOL +
            "  public final void buz() {}" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

}
