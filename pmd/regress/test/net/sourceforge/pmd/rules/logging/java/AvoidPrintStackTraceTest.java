package test.net.sourceforge.pmd.rules.logging.java;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.PMD;

public class AvoidPrintStackTraceTest extends SimpleAggregatorTst{

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("rulesets/logging-java.xml", "AvoidPrintStackTrace");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "simple failure", 1, rule),
           new TestDescriptor(TEST2, "ok", 0, rule),
       });
    }

    private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    " void bar() {" + PMD.EOL +
    "  try {} catch (Exception e) {" + PMD.EOL +
    "   e.printStackTrace();" + PMD.EOL +
    "  }" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    " void bar() {" + PMD.EOL +
    "  try {} catch (Exception e) {" + PMD.EOL +
    "   LOG.error(e, \"Whoa!\");" + PMD.EOL +
    "  }" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

}
