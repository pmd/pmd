package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class UseLocaleWithCaseConversionsRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("design", "UseLocaleWithCaseConversions");
    }

    public void testAll() {
        runTests(new TestDescriptor[]{
            new TestDescriptor(TEST1, "toLowerCase() with no args", 1, rule),
            new TestDescriptor(TEST2, "toUpperCase() with no args", 1, rule),
            new TestDescriptor(TEST3, "both ok", 0, rule),
            new TestDescriptor(TEST4, "toHexString OK", 0, rule),
        });
    }

    private static final String TEST1 =
            "public class Foo {" + PMD.EOL +
            " String x = y.toLowerCase();" + PMD.EOL +
            "}";

    private static final String TEST2 =
            "public class Foo {" + PMD.EOL +
            " String x = y.toUpperCase();" + PMD.EOL +
            "}";

    private static final String TEST3 =
            "public class Foo {" + PMD.EOL +
            " String x = y.toUpperCase(Locale.EN);" + PMD.EOL +
            " String z = y.toLowerCase(Locale.EN);" + PMD.EOL +
            "}";

    private static final String TEST4 =
            "public class Foo {" + PMD.EOL +
            " String x = y.toHexString().toUpperCase();" + PMD.EOL +
            "}";
}
