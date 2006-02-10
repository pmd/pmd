package test.net.sourceforge.pmd.rules.logging.jakartacommons;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class UseCorrectExceptionLoggingTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("rulesets/logging-jakarta-commons.xml", "UseCorrectExceptionLogging");
    }

    public void testAll() {
        runTests(new TestDescriptor[]{
            new TestDescriptor(TEST1, "ok", 0, rule),
            new TestDescriptor(TEST2, "failure case - two calls", 2, rule),
            new TestDescriptor(TEST3, "must be in a catch block", 0, rule),
        });
    }

    private static final String TEST1 =
            "public class Foo {" + PMD.EOL +
            " static final Log _LOG = LogFactory.getLog( Main.class );" + PMD.EOL +
            " void foo() {" + PMD.EOL +
            "  try {} catch (OtherException oe) {" + PMD.EOL +
            "   _LOG.error(oe.getMessage(), oe);" + PMD.EOL +
            "  }" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST2 =
            "public class Foo {" + PMD.EOL +
            " static final Log _LOG = LogFactory.getLog( Main.class );" + PMD.EOL +
            " void foo() {" + PMD.EOL +
            "  try {} catch (Exception e) {" + PMD.EOL +
            "   _LOG.error(e);" + PMD.EOL +
            "   _LOG.info(e);" + PMD.EOL +
            "  } " + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST3 =
            "public class Foo {" + PMD.EOL +
            " static final Log _LOG = LogFactory.getLog( Main.class );" + PMD.EOL +
            " void foo(int e) {" + PMD.EOL +
            "  _LOG.error(e);" + PMD.EOL +
            " }" + PMD.EOL +
            "}";
}
