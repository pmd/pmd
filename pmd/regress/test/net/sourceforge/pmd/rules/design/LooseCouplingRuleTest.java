package test.net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.rules.design.LooseCouplingRule;
import net.sourceforge.pmd.cpd.CPD;
import test.net.sourceforge.pmd.rules.RuleTst;

public class LooseCouplingRuleTest extends RuleTst {

    private static final String TEST1 =
    "import java.util.*;" + CPD.EOL +
    "public class LooseCoupling1 {" + CPD.EOL +
    " public HashSet getFoo() {" + CPD.EOL +
    "  return new HashSet();" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST2 =
    "import java.util.*;" + CPD.EOL +
    "public class LooseCoupling2 {" + CPD.EOL +
    " public Map getFoo() {" + CPD.EOL +
    "  return new HashMap();" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST3 =
    "public class LooseCoupling3 {" + CPD.EOL +
    " public void foo() {}" + CPD.EOL +
    "}";

    private static final String TEST4 =
    "import java.util.*;" + CPD.EOL +
    "public class LooseCoupling1 {" + CPD.EOL +
    " private Set fooSet = new HashSet(); // OK" + CPD.EOL +
    "" + CPD.EOL +
    " public Set getFoo() {" + CPD.EOL +
    "  return fooSet;" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST5 =
    "import java.util.*;" + CPD.EOL +
    "public class LooseCoupling5 {" + CPD.EOL +
    " private HashSet fooSet = new HashSet(); // NOT OK" + CPD.EOL +
    "" + CPD.EOL +
    " public Set getFoo() {" + CPD.EOL +
    "  return fooSet;" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST6 =
    "import java.util.*;" + CPD.EOL +
    "public class LooseCoupling1 {" + CPD.EOL +
    " private HashSet fooSet = new HashSet(); // NOT OK" + CPD.EOL +
    "" + CPD.EOL +
    " public HashSet getFoo() { // NOT OK" + CPD.EOL +
    "  return fooSet;" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST7 =
    "import java.util.*;" + CPD.EOL +
    "public class LooseCoupling7 {" + CPD.EOL +
    " private HashSet fooSet = new HashSet();" + CPD.EOL +
    " private HashMap fooMap = new HashMap();" + CPD.EOL +
    "}";

    private static final String TEST8 =
    "import java.util.*;" + CPD.EOL +
    "public class LooseCoupling8 {" + CPD.EOL +
    " public void foo(HashMap bar) {}" + CPD.EOL +
    "}";

    private static final String TEST9 =
    "import java.util.*;" + CPD.EOL +
    "public class LooseCoupling9 {" + CPD.EOL +
    " public void foo(Vector bar) {}" + CPD.EOL +
    "}";

    private LooseCouplingRule rule;

    public void setUp() {
        rule = new LooseCouplingRule();
        rule.setMessage("Avoid this stuff -> ''{0}''");
    }

    public void test1() throws Throwable {
        runTestFromString(TEST1, 1, rule);
    }
    public void test2() throws Throwable {
        runTestFromString(TEST2, 0, rule);
    }
    public void test3() throws Throwable {
        runTestFromString(TEST3, 0, rule);
    }
    public void test4() throws Throwable {
        runTestFromString(TEST4, 0, rule);
    }
    public void test5() throws Throwable {
        runTestFromString(TEST5, 1, rule);
    }
    public void test6() throws Throwable {
        runTestFromString(TEST6, 2, rule);
    }
    public void test7() throws Throwable {
        runTestFromString(TEST7, 2, rule);
    }
    public void test8() throws Throwable {
        runTestFromString(TEST8, 1, rule);
    }
    public void testVector() throws Throwable {
        runTestFromString(TEST9, 1, rule);
    }
}
