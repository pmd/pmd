/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.rules.DuplicateImportsRule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class DuplicateImportsRuleTest extends SimpleAggregatorTst {

    private DuplicateImportsRule rule;

    public void setUp() {
        rule = new DuplicateImportsRule();
        rule.setMessage("Avoid this stuff -> ''{0}''");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "duplicate single type imports", 1, rule),
           new TestDescriptor(TEST2, "duplicate wildcard imports", 1, rule),
           new TestDescriptor(TEST3, "single type import after wildcard import", 1, rule),
           new TestDescriptor(TEST4, "subpackage import, ok", 0, rule),
       });
    }

    private static final String TEST1 =
    "import java.io.File;" + PMD.EOL +
    "import java.util.*;" + PMD.EOL +
    "import java.io.File;" + PMD.EOL +
    "public class Foo {}";

    private static final String TEST2 =
    "import java.io.*;" + PMD.EOL +
    "import java.io.*;" + PMD.EOL +
    "public class Foo {}";

    private static final String TEST3 =
    "import java.util.*;" + PMD.EOL +
    "import java.net.*;" + PMD.EOL +
    "import java.io.*;" + PMD.EOL +
    "import java.io.File;" + PMD.EOL +
    "public class Foo {}";

    private static final String TEST4 =
    "import javax.servlet.*;" + PMD.EOL +
    "import javax.servlet.http.*;" + PMD.EOL +
    "public class Foo {}";

}
