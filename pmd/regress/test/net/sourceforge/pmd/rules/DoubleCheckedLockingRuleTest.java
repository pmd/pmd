package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.rules.DoubleCheckedLockingRule;

public class DoubleCheckedLockingRuleTest extends SimpleAggregatorTst {

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "simple ok", 0, new DoubleCheckedLockingRule()),
           new TestDescriptor(TEST2, "simple failure", 1, new DoubleCheckedLockingRule()),
           new TestDescriptor(TEST3, "skip interfaces", 0, new DoubleCheckedLockingRule()),
       });
    }

    private static final String TEST1 =
    "public class DoubleCheckedLockingRule1 {" + PMD.EOL +
    " public void foo() {}" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class DoubleCheckedLockingRule2 {" + PMD.EOL +
    "      Object baz;" + PMD.EOL +
    "      Object bar() {" + PMD.EOL +
    "        if(baz == null) { //baz may be non-null yet not fully created" + PMD.EOL +
    "          synchronized(this){" + PMD.EOL +
    "            if(baz == null){" + PMD.EOL +
    "              baz = new Object();" + PMD.EOL +
    "            }" + PMD.EOL +
    "          }" + PMD.EOL +
    "        }" + PMD.EOL +
    "        return baz;" + PMD.EOL +
    "      }" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public interface DoubleCheckedLockingRule3 {" + PMD.EOL +
    " void foo();" + PMD.EOL +
    "}";
}
