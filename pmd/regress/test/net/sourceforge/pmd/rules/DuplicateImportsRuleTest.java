package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.rules.DuplicateImportsRule;
import net.sourceforge.pmd.cpd.CPD;

public class DuplicateImportsRuleTest extends RuleTst {

    private static final String TEST1 =
    "import java.io.File;" + CPD.EOL +
    "import java.util.*;" + CPD.EOL +
    "import java.io.File;" + CPD.EOL +
    "" + CPD.EOL +
    "public class DuplicateImports {}";

    private static final String TEST2 =
    "import java.io.*;" + CPD.EOL +
    "import java.io.*;" + CPD.EOL +
    "" + CPD.EOL +
    "public class DuplicateImports2 {}";

    private static final String TEST3 =
    "import java.util.*;" + CPD.EOL +
    "import java.net.*;" + CPD.EOL +
    "import java.io.*;" + CPD.EOL +
    "import java.io.File;" + CPD.EOL +
    "" + CPD.EOL +
    "public class DuplicateImports3 {}";

    private static final String TEST4 =
    "import javax.servlet.*;" + CPD.EOL +
    "import javax.servlet.http.*;" + CPD.EOL +
    "" + CPD.EOL +
    "public class DuplicateImports4 {}";

    private DuplicateImportsRule rule;

    public void setUp() {
        rule = new DuplicateImportsRule();
        rule.setMessage("Avoid this stuff -> ''{0}''");
    }

    public void test1() throws Throwable {
        runTestFromString(TEST1, 1, rule);
    }

    public void test2() throws Throwable {
        runTestFromString(TEST2, 1, rule);
    }

    public void test3() throws Throwable {
        runTestFromString(TEST3, 1, rule);
    }

    public void test4() throws Throwable {
        runTestFromString(TEST4, 0, rule);
    }
}
