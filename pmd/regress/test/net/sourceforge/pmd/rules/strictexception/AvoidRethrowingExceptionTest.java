package test.net.sourceforge.pmd.rules.strictexception;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

/**
 * Tests the <code>AvoidRethrowingException</code> rule.
 *
 * @author George Thomas
 */
public class AvoidRethrowingExceptionTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("strictexception", "AvoidRethrowingException");
    }

    public void testAll() {
        runTests(new TestDescriptor[] {
            new TestDescriptor(FAILURE_TEST, "failure case", 1, rule),
            new TestDescriptor(OK_TEST, "doing something else before throwing it, ok", 0, rule),
            new TestDescriptor(OK_GET_CAUSE, "throwing the return value of a method call on the exception, ok", 0, rule),
        });
    }

    private static final String FAILURE_TEST =
            "public class Foo {" + PMD.EOL
            + "  void bar() {" + PMD.EOL
            + "   try {" + PMD.EOL
            + "   } catch (SomeException se) {" + PMD.EOL
            + "    throw se;" + PMD.EOL
            + "   }" + PMD.EOL
            + "  }" + PMD.EOL
            + "} " + PMD.EOL;

    private static final String OK_TEST =
            "public class Foo {" + PMD.EOL
            + "  void bar() {" + PMD.EOL
            + "   try {" + PMD.EOL
            + "   } catch (SomeException se) {" + PMD.EOL
            + "    System.out.println(\"something interesting\");" + PMD.EOL
            + "    throw se;" + PMD.EOL
            + "   }" + PMD.EOL
            + "  }" + PMD.EOL
            + "} " + PMD.EOL;

    private static final String OK_GET_CAUSE =
            "public class Foo {" + PMD.EOL
            + "  void bar() {" + PMD.EOL
            + "   try {" + PMD.EOL
            + "   } catch (SomeException se) {" + PMD.EOL
            + "    throw se.getCause();" + PMD.EOL
            + "   }" + PMD.EOL
            + "  }" + PMD.EOL
            + "} " + PMD.EOL;
}
