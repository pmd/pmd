/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class CloseConnectionTest extends SimpleAggregatorTst  {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("design", "CloseConnection");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "connection is closed, ok", 0, rule),
           new TestDescriptor(TEST2, "connection not closed, should have failed", 1, rule),
           new TestDescriptor(TEST3, "java.sql.* not imported, ignore", 0, rule),
       });
    }

    private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    " void bar() {" + PMD.EOL +
    "  Connection c = pool.getConnection();" + PMD.EOL +
    "  try {" + PMD.EOL +
    "  } catch (Exception e) {" + PMD.EOL +
    "  } finally {" + PMD.EOL +
    "   c.close();" + PMD.EOL +
    "  }" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "import java.sql.*;" + PMD.EOL +
    "public class Foo {" + PMD.EOL +
    " void bar() {" + PMD.EOL +
    "  Connection c = pool.getConnection();" + PMD.EOL +
    "  try {" + PMD.EOL +
    "  } catch (Exception e) {" + PMD.EOL +
    "  }" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "import some.pckg.Connection;" + PMD.EOL +
    "public class Foo {" + PMD.EOL +
    " void bar() {" + PMD.EOL +
    "  Connection c = pool.getConnection();" + PMD.EOL +
    "  try {} catch (Exception e) {}" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

}
