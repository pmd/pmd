package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.rules.DoubleCheckedLockingRule;
import net.sourceforge.pmd.cpd.CPD;

public class DoubleCheckedLockingRuleTest extends RuleTst {

    private static final String TEST1 =
    "public class DoubleCheckedLockingRule1 {" + CPD.EOL +
    " public void foo() {}" + CPD.EOL +
    "}";

    private static final String TEST2 =
    "public class DoubleCheckedLockingRule2 {" + CPD.EOL +
    "      Object baz;" + CPD.EOL +
    "      Object bar() {" + CPD.EOL +
    "        if(baz == null) { //baz may be non-null yet not fully created" + CPD.EOL +
    "          synchronized(this){" + CPD.EOL +
    "            if(baz == null){" + CPD.EOL +
    "              baz = new Object();" + CPD.EOL +
    "            }" + CPD.EOL +
    "          }" + CPD.EOL +
    "        }" + CPD.EOL +
    "        return baz;" + CPD.EOL +
    "      }" + CPD.EOL +
    "}";

    private static final String TEST3 =
    "public interface DoubleCheckedLockingRule3 {" + CPD.EOL +
    " void foo();" + CPD.EOL +
    "}";

    public void testSimpleOK() throws Throwable {
        runTestFromString(TEST1, 0, new DoubleCheckedLockingRule());
    }

    public void testSimpleBad() throws Throwable {
        runTestFromString(TEST2, 1, new DoubleCheckedLockingRule());
    }

    public void testSkipInterfaces() throws Throwable {
        runTestFromString(TEST3, 0, new DoubleCheckedLockingRule());
    }
}
