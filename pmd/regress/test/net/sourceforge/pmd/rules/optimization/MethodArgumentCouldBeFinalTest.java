/*
 * Created on Jan 10, 2005 
 *
 * $Id$
 */
package test.net.sourceforge.pmd.rules.optimization;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class MethodArgumentCouldBeFinalTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("optimizations", "MethodArgumentCouldBeFinal");
    }

    public void testAll() {
        runTests(new TestDescriptor[]{
            new TestDescriptor(TEST1, "TEST1", 1, rule),
            new TestDescriptor(TEST2, "TEST2", 2, rule),
            new TestDescriptor(TEST3, "TEST3", 2, rule),
            new TestDescriptor(TEST4, "TEST4", 1, rule),
            new TestDescriptor(TEST5, "TEST5", 1, rule),
            new TestDescriptor(TEST6, "TEST6", 0, rule),
            new TestDescriptor(TEST7, "Shouldn't trigger on try blocks", 0, rule),
            new TestDescriptor(TEST8, "Skip native methods", 0, rule),
            new TestDescriptor(TEST9, "Skip abstract methods", 0, rule),
            new TestDescriptor(TEST10, "self assignment of a method param means it can't be final", 0, rule),
            new TestDescriptor(TEST11, "same as above but prefix vs postfix", 0, rule),
        });
    }

    private static final String TEST1 =
            "public class Foo {" + PMD.EOL +
            " public void bar(int a) {}" + PMD.EOL +
            "}";

    private static final String TEST2 =
            "public class Foo {" + PMD.EOL +
            " public void bar(int a, Object o) {}" + PMD.EOL +
            "}";

    private static final String TEST3 =
            "public class Foo {" + PMD.EOL +
            " public void bar(int a, Object o) {" + PMD.EOL +
            "  int z = a;" + PMD.EOL +
            "  Object x = o.clone();" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST4 =
            "public class Foo {" + PMD.EOL +
            " public void bar(final int a, Object o) {" + PMD.EOL +
            "  int z = a;" + PMD.EOL +
            "  Object x = o.clone();" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST5 =
            "public class Foo {" + PMD.EOL +
            " public void bar(int a, final Object o) {" + PMD.EOL +
            "  int z = a;" + PMD.EOL +
            "  Object x = o.clone();" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST6 =
            "public class Foo {" + PMD.EOL +
            " public void bar(final int a, final Object o) {" + PMD.EOL +
            "  int z = a;" + PMD.EOL +
            "  Object x = o.clone();" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST7 =
            "public class Foo {" + PMD.EOL +
            " public void bar(final List batch) {" + PMD.EOL +
            "   try {} catch (Exception e) {} " + PMD.EOL +
            "   try {} catch (Exception ee) {} " + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST8 =
            "public class Foo {" + PMD.EOL +
            " public native void bar(Object x);" + PMD.EOL +
            "}";

    private static final String TEST9 =
            "public class Foo {" + PMD.EOL +
            " public abstract void bar(Object x);" + PMD.EOL +
            "}";

    private static final String TEST10 =
            "public class Foo {" + PMD.EOL +
            " public void bar(int a) {" + PMD.EOL +
            "  x[a++] = 1;" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST11 =
            "public class Foo {" + PMD.EOL +
            " public void bar(int a) {" + PMD.EOL +
            "  x[--a] = 1;" + PMD.EOL +
            " }" + PMD.EOL +
            "}";
}
