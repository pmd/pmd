package test.net.sourceforge.pmd.rules.logging.jakartacommons;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.PMD;

public class ProperLoggerTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("rulesets/logging-jakarta-commons.xml", "ProperLogger");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "ok", 0, rule),
           new TestDescriptor(TEST2, "wrong class name", 1, rule),
           new TestDescriptor(TEST3, "ok, special case", 0, rule),
       });
    }

    private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    " private static final Log LOG = LogFactory.getLog(Foo.class);" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    " private static final Log LOG = LogFactory.getLog(Bar.class);" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class Foo {" + PMD.EOL +
    " private final Log log;" + PMD.EOL +
    " Foo(Log log) {" + PMD.EOL +
    "  this.log = log;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";
}
