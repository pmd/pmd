package test.net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.rules.design.NullAssignmentRule;
import net.sourceforge.pmd.cpd.CPD;
import test.net.sourceforge.pmd.rules.RuleTst;

public class NullAssignmentRuleTest extends RuleTst {

    private static final String TEST1 =
    "public class NullAssignment1 {" + CPD.EOL +
    "       public Object foo() {" + CPD.EOL +
    "               Object x = null; // OK" + CPD.EOL +
    "               return x;" + CPD.EOL +
    "       }       " + CPD.EOL +
    "}";

    private static final String TEST2 =
    "public class NullAssignment2 {" + CPD.EOL +
    "       public void foo() {" + CPD.EOL +
    "               Object x;" + CPD.EOL +
    "               x = new Object();" + CPD.EOL +
    "               for (int y = 0; y < 10; y++) {" + CPD.EOL +
    "                       System.err.println(y);  " + CPD.EOL +
    "               }" + CPD.EOL +
    "               x = null; // This is bad" + CPD.EOL +
    "       }" + CPD.EOL +
    "}";

    private static final String TEST3 =
    "public class NullAssignment3 {" + CPD.EOL +
    "       public void foo() {" + CPD.EOL +
    "               Object x;" + CPD.EOL +
    "               if (x == null) { // This is OK" + CPD.EOL +
    "                       return;" + CPD.EOL +
    "               }" + CPD.EOL +
    "       }" + CPD.EOL +
    "}";

    private static final String TEST4 =
    "public class NullAssignment4 {" + CPD.EOL +
    " public void foo() {" + CPD.EOL +
    "  String x = null;" + CPD.EOL +
    "  x = new String(null);" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    public void testInitAssignment() throws Throwable {
        runTestFromString(TEST1, 0, new NullAssignmentRule());
    }

    public void testBadAssignment() throws Throwable {
        runTestFromString(TEST2, 1, new NullAssignmentRule());
    }

    public void testCheckTest() throws Throwable {
        runTestFromString(TEST3, 0, new NullAssignmentRule());
    }

    public void testNullParamOnRHS() throws Throwable {
        runTestFromString(TEST4, 0, new NullAssignmentRule());
    }
}
