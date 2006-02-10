package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class UnusedNullCheckInEqualsTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("basic", "UnusedNullCheckInEquals");
    }

    public void testAll() {
        runTests(new TestDescriptor[]{
            new TestDescriptor(TEST1, "failure case", 1, rule),
            new TestDescriptor(TEST2, "different var, 'tis ok", 0, rule),
            new TestDescriptor(TEST3, "proper usage", 0, rule),
            new TestDescriptor(TEST4, "variation of correct usage", 0, rule),
            //new TestDescriptor(TESTN, "shouldn't this fail?", 1, rule),
        });
    }

    private static final String TEST1 =
            "public class Foo {" + PMD.EOL +
            " public void bar() {" + PMD.EOL +
            "  if (x != null && foo.getBar().equals(x)) {} " + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST2 =
            "public class Foo {" + PMD.EOL +
            " public void bar() {" + PMD.EOL +
            "  if (x != null && foo.equals(y)) {} " + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST3 =
            "public class Foo {" + PMD.EOL +
            " public void bar() {" + PMD.EOL +
            "  if (x != null && x.equals(y)) {} " + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST4 =
            "public class Foo {" + PMD.EOL +
            " public void bar() {" + PMD.EOL +
            "  if (x != null && \"Foo\".equals(y)) {} " + PMD.EOL +
            "  if (y.equals(x)) {} " + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TESTN =
            "public class Foo {" + PMD.EOL +
            " public void bar() {" + PMD.EOL +
            "  if (x != null && y.equals(x)) {} " + PMD.EOL +
            " }" + PMD.EOL +
            "}";

}
