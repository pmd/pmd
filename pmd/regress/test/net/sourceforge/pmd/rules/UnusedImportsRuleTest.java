package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.rules.UnusedImportsRule;

public class UnusedImportsRuleTest extends RuleTst {

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


    private UnusedImportsRule rule;

    public void setUp() {
        rule = new UnusedImportsRule();
        rule.setMessage("Avoid this stuff -> ''{0}''");
    }

    public void test1() throws Throwable {
        runTestFromString(TEST1, 1, rule);
    }

    public void test2() throws Throwable {
        runTestFromString(TEST2, 0, rule);
    }

    public void test3() throws Throwable {
        runTestFromString(TEST3, 2, rule);
    }

    public void test4() throws Throwable {
        runTestFromString(TEST4, 0, rule);
    }
}
