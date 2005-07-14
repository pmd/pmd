/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules.strictexception;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class ExceptionSignatureDeclarationRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("strictexception", "SignatureDeclareThrowsException");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "method throws Exception", 1, rule),
           new TestDescriptor(TEST2, "ok", 0, rule),
           new TestDescriptor(TEST3, "constructor throws Exception", 1, rule),
           new TestDescriptor(TEST4, "skip junit setUp method", 0, rule),
           new TestDescriptor(TEST5, "skip junit tearDown method", 0, rule),
       });
    }

    public void testGenerics() throws Throwable {
        Report rpt = new Report();
        runTestFromString15(TEST6, rule, rpt);
        assertEquals(0, rpt.size());
    }

    private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    " void foo() throws Exception {}" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    " void foo() {}" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class Foo {" + PMD.EOL +
    " Foo() throws Exception {}" + PMD.EOL +
    "}";

    private static final String TEST4 =
    "import junit.framework.*;" + PMD.EOL +
    "public class Foo {" + PMD.EOL +
    " void setUp() throws Exception {}" + PMD.EOL +
    "}";

    private static final String TEST5 =
    "import junit.framework.*;" + PMD.EOL +
    "public class Foo {" + PMD.EOL +
    " void tearDown() throws Exception {}" + PMD.EOL +
    "}";

    private static final String TEST6 =
    "public class Foo {" + PMD.EOL +
    " public <T> Bar<T> foo() { /* blah */}" + PMD.EOL +
    "}";

}
