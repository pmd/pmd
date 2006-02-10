package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class UselessOverridingMethodTest extends SimpleAggregatorTst {
    private Rule rule;

    public void setUp() {
        rule = findRule("basic", "UselessOverridingMethod");
    }

    public void testAll() {
        runTests(new TestDescriptor[]{
            new TestDescriptor(TEST1, "call super", 1, rule),
            new TestDescriptor(TEST2, "call super with same argument", 1, rule),
            new TestDescriptor(TEST3, "call super with different argument", 0, rule),
            new TestDescriptor(TEST4, "call super with different argument 2", 0, rule),
            new TestDescriptor(TEST5, "call super with different argument 3", 0, rule),
            new TestDescriptor(TEST6, "call super with inverted arguments", 0, rule),
            new TestDescriptor(TEST7, "return value of super", 1, rule),
            new TestDescriptor(TEST8, "return value of super with argument", 1, rule),
            new TestDescriptor(TEST9, "return value of super after adding a string", 0, rule),
            new TestDescriptor(TEST10, "do not crash on abstract methods", 0, rule),
            new TestDescriptor(TEST11, "do not crash on interfaces", 0, rule),
            new TestDescriptor(TEST12, "do not crash on empty returns", 0, rule),
            new TestDescriptor(TEST13, "do not crash on super", 0, rule)
        });
    }

    private static final String TEST1 =
            "public class Foo extends Bar {" + PMD.EOL +
            "public void foo() {" + PMD.EOL +
            "    super.foo();" + PMD.EOL +
            "}" + PMD.EOL +
            "}";

    private static final String TEST2 =
            "public class Foo extends Bar {" + PMD.EOL +
            "public void foo(String bar) {" + PMD.EOL +
            "    super.foo(bar);" + PMD.EOL +
            "}" + PMD.EOL +
            "}";

    private static final String TEST3 =
            "public class Foo extends Bar {" + PMD.EOL +
            "String text = \"text\";" + PMD.EOL +
            "public void foo(String bar) {" + PMD.EOL +
            "    super.foo(text);" + PMD.EOL +
            "}" + PMD.EOL +
            "}";

    private static final String TEST4 =
            "public class Foo extends Bar {" + PMD.EOL +
            "public void foo(String bar) {" + PMD.EOL +
            "    super.foo(boe(bar));" + PMD.EOL +
            "}" + PMD.EOL +
            "}";

    private static final String TEST5 =
            "public class Buz{" + PMD.EOL +
            " public String foo(String bar) {" + PMD.EOL +
            "    super.foo(\"\" + bar); " + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST6 =
            "public class Foo extends Bar {" + PMD.EOL +
            "public void foo(String bar, String stool) {" + PMD.EOL +
            "    super.foo(stool, bar);" + PMD.EOL +
            "}" + PMD.EOL +
            "}";

    private static final String TEST7 =
            "public class Foo extends Bar {" + PMD.EOL +
            "public String foo() {" + PMD.EOL +
            "    return super.foo();" + PMD.EOL +
            "}" + PMD.EOL +
            "}";

    private static final String TEST8 =
            "public class Foo extends Bar {" + PMD.EOL +
            "public String foo(String bar) {" + PMD.EOL +
            "    return super.foo(bar);" + PMD.EOL +
            "}" + PMD.EOL +
            "}";

    private static final String TEST9 =
            "public class Foo extends Bar {" + PMD.EOL +
            "public String foo(String bar) {" + PMD.EOL +
            "    return super.foo(bar) + \"is foo\";" + PMD.EOL +
            "}" + PMD.EOL +
            "}";

    private static final String TEST10 =
            "public class Foo extends Bar {" + PMD.EOL +
            "public abstract String foo();" + PMD.EOL +
            "}";

    private static final String TEST11 =
            "public interface Foo extends Bar {" + PMD.EOL +
            "public String foo();" + PMD.EOL +
            "}";

    private static final String TEST12 =
            "public class Foo {" + PMD.EOL +
            "public void foo(String bar) {" + PMD.EOL +
            "    return;" + PMD.EOL +
            "}" + PMD.EOL +
            "}";

    private static final String TEST13 =
            "public class Foo {" + PMD.EOL +
            "public void init(String[] args) {" + PMD.EOL +
            "    super.init( args, Application.NO_MODULES );" + PMD.EOL +
            "}" + PMD.EOL +
            "}";

}
