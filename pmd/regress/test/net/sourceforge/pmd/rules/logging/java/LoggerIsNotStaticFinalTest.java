/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.rules.logging.java;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class LoggerIsNotStaticFinalTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("logging-java", "LoggerIsNotStaticFinal");
    }

    public void testAll() {
        runTests(new TestDescriptor[]{
            new TestDescriptor(TEST1, "ok", 0, rule),
            new TestDescriptor(TEST2, "two bad loggers", 2, rule),
            new TestDescriptor(TEST3, "ok with internal class", 0, rule),
        });
    }

    private static final String TEST1 =
            "public class Foo {" + PMD.EOL +
            " static final Logger log;" + PMD.EOL +
            "}";

    private static final String TEST2 =
            "public class Foo {" + PMD.EOL +
            " static final Logger log;" + PMD.EOL +
            " Logger log1;" + PMD.EOL +
            " Logger log2;" + PMD.EOL +
            "}";

    private static final String TEST3 =
            "public class Foo {" + PMD.EOL +
            " static final Logger log;" + PMD.EOL +
            " static class c { " + PMD.EOL +
            "  static final Logger log;" + PMD.EOL +
            " } " + PMD.EOL +
            "}";

}
