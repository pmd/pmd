package test.net.sourceforge.pmd.rules.design;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.PMD;

public class PositionLiteralsFirstInComparisonsTest extends SimpleAggregatorTst{

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("rulesets/design.xml", "PositionLiteralsFirstInComparisons");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "ok, literal comes first", 0, rule),
           new TestDescriptor(TEST2, "bad, literal comes last", 1, rule),
       });
    }

    private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    " boolean bar(String x) {" + PMD.EOL +
    "  return \"2\".equals(x);" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    " boolean bar(String x) {" + PMD.EOL +
    "  return x.equals(\"2\");" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

}
