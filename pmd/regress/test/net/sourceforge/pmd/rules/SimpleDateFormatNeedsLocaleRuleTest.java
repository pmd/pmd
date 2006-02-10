package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class SimpleDateFormatNeedsLocaleRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("design", "SimpleDateFormatNeedsLocale");
    }

    public void testAll() {
        runTests(new TestDescriptor[]{
            new TestDescriptor(TEST1, "ok, uses a two arg constructor", 0, rule),
            new TestDescriptor(TEST2, "bad, using the single-arg contructor", 1, rule),
            new TestDescriptor(TEST3, "all quiet", 0, rule),
        });
    }

    private static final String TEST1 =
            "class Foo {" + PMD.EOL +
            "  private SimpleDateFormat sdf = new SimpleDateFormat(\"pattern\", Locale.US);" + PMD.EOL +
            "}";

    private static final String TEST2 =
            "class Foo {" + PMD.EOL +
            "  private SimpleDateFormat sdf = new SimpleDateFormat(\"pattern\");" + PMD.EOL +
            "}";

    private static final String TEST3 =
            "class Foo {}";
}
