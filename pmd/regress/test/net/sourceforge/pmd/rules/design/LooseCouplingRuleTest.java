package test.net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.rules.design.LooseCouplingRule;
import test.net.sourceforge.pmd.rules.SimpleAggregatorTst;
import test.net.sourceforge.pmd.rules.TestDescriptor;

public class LooseCouplingRuleTest extends SimpleAggregatorTst {

    private LooseCouplingRule rule;

    public void setUp() {
        rule = new LooseCouplingRule();
        rule.setMessage("Avoid this stuff -> ''{0}''");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "", 1, rule),
           new TestDescriptor(TEST2, "", 0, rule),
           new TestDescriptor(TEST3, "", 0, rule),
           new TestDescriptor(TEST4, "", 0, rule),
           new TestDescriptor(TEST5, "", 1, rule),
           new TestDescriptor(TEST6, "", 2, rule),
           new TestDescriptor(TEST7, "", 2, rule),
           new TestDescriptor(TEST8, "", 1, rule),
           new TestDescriptor(TEST9, "Vector could be List", 1, rule),
       });
    }

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
    " public Set getFoo() {" + CPD.EOL +
    "  return fooSet;" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST5 =
    "import java.util.*;" + CPD.EOL +
    "public class LooseCoupling5 {" + CPD.EOL +
    " private HashSet fooSet = new HashSet(); // NOT OK" + CPD.EOL +
    " public Set getFoo() {" + CPD.EOL +
    "  return fooSet;" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST6 =
    "import java.util.*;" + CPD.EOL +
    "public class LooseCoupling1 {" + CPD.EOL +
    " private HashSet fooSet = new HashSet(); // NOT OK" + CPD.EOL +
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

}
