package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.rules.UnusedImportsRule;

public class UnusedImportsRuleTest extends SimpleAggregatorTst {

    private UnusedImportsRule rule;

    public void setUp() {
        rule = new UnusedImportsRule();
        rule.setMessage("Avoid this stuff -> ''{0}''");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "simple unused single type import", 1, rule),
           new TestDescriptor(TEST2, "one used single type import", 0, rule),
           new TestDescriptor(TEST3, "2 unused single-type imports", 2, rule),
           new TestDescriptor(TEST4, "1 used single type import", 0, rule)
       });
    }

    private static final String TEST1 =
    "import java.io.File;" + CPD.EOL +
    "public class UnusedImports1 {}";

    private static final String TEST2 =
    "import java.io.File;" + CPD.EOL +
    "public class UnusedImports2 {" + CPD.EOL +
    " private File file;" + CPD.EOL +
    "}";

    private static final String TEST3 =
    "import java.io.File;" + CPD.EOL +
    "import java.util.List;" + CPD.EOL +
    "public class UnusedImports3 {" + CPD.EOL +
    "}";

    private static final String TEST4 =
    "import java.security.AccessController;" + CPD.EOL +
    "public class UnusedImports4 {" + CPD.EOL +
    " public void foo() {" + CPD.EOL +
    "  AccessController.doPrivileged(null);" + CPD.EOL +
    " }" + CPD.EOL +
    "}";


}
