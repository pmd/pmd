package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.rules.DuplicateImportsRule;

public class DuplicateImportsRuleTest extends SimpleAggregatorTst {

    private DuplicateImportsRule rule;

    public void setUp() {
        rule = new DuplicateImportsRule();
        rule.setMessage("Avoid this stuff -> ''{0}''");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "simple failure", 1, rule),
           new TestDescriptor(TEST2, "", 1, rule),
           new TestDescriptor(TEST3, "", 1, rule),
           new TestDescriptor(TEST4, "", 0, rule),
       });
    }

    private static final String TEST1 =
    "import java.io.File;" + CPD.EOL +
    "import java.util.*;" + CPD.EOL +
    "import java.io.File;" + CPD.EOL +
    "public class DuplicateImports {}";

    private static final String TEST2 =
    "import java.io.*;" + CPD.EOL +
    "import java.io.*;" + CPD.EOL +
    "public class DuplicateImports2 {}";

    private static final String TEST3 =
    "import java.util.*;" + CPD.EOL +
    "import java.net.*;" + CPD.EOL +
    "import java.io.*;" + CPD.EOL +
    "import java.io.File;" + CPD.EOL +
    "public class DuplicateImports3 {}";

    private static final String TEST4 =
    "import javax.servlet.*;" + CPD.EOL +
    "import javax.servlet.http.*;" + CPD.EOL +
    "public class DuplicateImports4 {}";

}
