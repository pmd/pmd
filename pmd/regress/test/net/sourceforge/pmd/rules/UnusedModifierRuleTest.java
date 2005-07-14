/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class UnusedModifierRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("controversial", "UnusedModifier");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "Unneeded 'public' in interface method", 1, rule),
           new TestDescriptor(TEST2, "class, no problem", 0, rule),
           new TestDescriptor(TEST3, "Unneeded 'abstract' in interface method", 1, rule),
           new TestDescriptor(TEST4, "all is well in interface method", 0, rule),
           new TestDescriptor(TEST5, "Unneeded 'public' in interface field", 1, rule),
           new TestDescriptor(TEST6, "Unneeded 'static' in interface field", 1, rule),
           new TestDescriptor(TEST7, "Unneeded 'final' in interface field", 1, rule),
           new TestDescriptor(TEST8, "Unneeded 'public static final' in interface field", 1, rule),
           new TestDescriptor(TEST9, "OK in interface field", 0, rule),
           new TestDescriptor(TEST10, "Unneeded 'public' in class nested in interface", 1, rule),
           new TestDescriptor(TEST11, "Unneeded 'static' in class nested in interface", 1, rule),
           new TestDescriptor(TEST12, "OK in class nested in interface", 0, rule),
           new TestDescriptor(TEST13, "Unneeded 'public' in interface nested in interface", 1, rule),
           new TestDescriptor(TEST14, "Unneeded 'static' in interface nested in interface", 1, rule),
           new TestDescriptor(TEST15, "OK in interface nested in interface", 0, rule),
           new TestDescriptor(TEST16, "Unneeded 'static' in interface nested in class", 1, rule),
           new TestDescriptor(TEST17, "OK in interface nested in class", 0, rule),
           new TestDescriptor(TEST18, "Unneeded 'public static final' in interface field inside another interface", 2, rule),
           new TestDescriptor(TEST19, "OK in interface field inside another interface", 0, rule),
           new TestDescriptor(TEST20, "Don't check methods in nested classes", 0, rule),
           new TestDescriptor(TEST21, "Don't check fields in nested classes", 0, rule),
           new TestDescriptor(TEST22, "Don't check fields that are anonymous inner classes", 0, rule),
       });
    }

    private static final String TEST1 =
    "public interface Foo {" + PMD.EOL +
    " public void bar();" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public abstract class Foo {" + PMD.EOL +
    " public abstract void bar();" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public interface Foo {" + PMD.EOL +
    " abstract void bar();" + PMD.EOL +
    "}";

    private static final String TEST4 =
    "public interface Foo {" + PMD.EOL +
    " void bar();" + PMD.EOL +
    "}";

    private static final String TEST5 =
    "public interface Foo {" + PMD.EOL +
    " public int X = 0;" + PMD.EOL +
    "}";

    private static final String TEST6 =
    "public interface Foo {" + PMD.EOL +
    " static int X = 0;" + PMD.EOL +
    "}";

    private static final String TEST7 =
    "public interface Foo {" + PMD.EOL +
    " final int X = 0;" + PMD.EOL +
    "}";

    private static final String TEST8 =
    "public interface Foo {" + PMD.EOL +
    " public static final int X = 0;" + PMD.EOL +
    "}";

    private static final String TEST9 =
    "public interface Foo {" + PMD.EOL +
    " int X = 0;" + PMD.EOL +
    "}";

    private static final String TEST10 =
    "public interface Foo {" + PMD.EOL +
    " public class Bar {}" + PMD.EOL +
    "}";

    private static final String TEST11 =
    "public interface Foo {" + PMD.EOL +
    " static class Bar {}" + PMD.EOL +
    "}";

    private static final String TEST12 =
    "public interface Foo {" + PMD.EOL +
    " class Bar {}" + PMD.EOL +
    "}";

    private static final String TEST13 =
    "public interface Foo {" + PMD.EOL +
    " public interface Baz {}" + PMD.EOL +
    "}";

    private static final String TEST14 =
    "public interface Foo {" + PMD.EOL +
    " static interface Baz {}" + PMD.EOL +
    "}";

    private static final String TEST15 =
    "public interface Foo {" + PMD.EOL +
    " interface Baz {}" + PMD.EOL +
    "}";

    private static final String TEST16 =
    "public class Foo {" + PMD.EOL +
    " public static interface Bar {}" + PMD.EOL +
    "}";

    private static final String TEST17 =
    "public class Foo {" + PMD.EOL +
    " public interface Bar {}" + PMD.EOL +
    "}";

    private static final String TEST18 =
    "public interface Foo {" + PMD.EOL +
    " public interface Bar {" + PMD.EOL +
    "  public static final int X = 0;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST19 =
    "interface Foo {" + PMD.EOL +
    " interface Bar {" + PMD.EOL +
    "  int X = 0;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST20 =
    "interface Foo {" + PMD.EOL +
    " class Bar {" + PMD.EOL +
    "  public void foo() {}" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST21 =
    "interface Foo {" + PMD.EOL +
    " class Bar {" + PMD.EOL +
    "  public int buz;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST22 =
    "interface Foo {" + PMD.EOL +
    "   Test DFLT_IMPL = new Test() {" + PMD.EOL +
    "   public int size() { return 0;}" + PMD.EOL +
    " };" + PMD.EOL +
    "}";
}
