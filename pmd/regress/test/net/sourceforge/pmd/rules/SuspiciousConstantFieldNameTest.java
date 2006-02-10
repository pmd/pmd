package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class SuspiciousConstantFieldNameTest extends SimpleAggregatorTst {
    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("naming", "SuspiciousConstantFieldName");
    }

    public void testAll() {
        runTests(new TestDescriptor[]{
            new TestDescriptor(TEST1, "ok", 0, rule),
            new TestDescriptor(TEST2, "PI not final", 1, rule),
            new TestDescriptor(TEST3, "PI and E not final", 2, rule),
            new TestDescriptor(TEST4, "ok", 0, rule),
            new TestDescriptor(TEST5, "ignore interfaces", 0, rule),
        });
    }

    private static final String TEST1 =
            "public class Foo {" + PMD.EOL +
            " public int e;" + PMD.EOL +
            " public final int PI;" + PMD.EOL +
            "}";

    private static final String TEST2 =
            "public class Foo {" + PMD.EOL +
            " public int e;" + PMD.EOL +
            " public int PI;" + PMD.EOL +
            "}";

    private static final String TEST3 =
            "public class Foo {" + PMD.EOL +
            " public int E;" + PMD.EOL +
            " public int PI;" + PMD.EOL +
            "}";

    private static final String TEST4 =
            "public class Foo {" + PMD.EOL +
            " public final int e;" + PMD.EOL +
            " public final int PI;" + PMD.EOL +
            "}";

    private static final String TEST5 =
            "public interface Foo {" + PMD.EOL +
            " public int E;" + PMD.EOL +
            "}";

}
