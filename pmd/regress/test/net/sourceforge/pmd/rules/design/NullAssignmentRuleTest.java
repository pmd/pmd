/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.rules.design.NullAssignmentRule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class NullAssignmentRuleTest extends SimpleAggregatorTst {

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "initial assignment", 0, new NullAssignmentRule()),
           new TestDescriptor(TEST2, "bad assignment", 1, new NullAssignmentRule()),
           new TestDescriptor(TEST3, "check test", 0, new NullAssignmentRule()),
           new TestDescriptor(TEST3, "null param on right hand sidel", 0, new NullAssignmentRule()),
       });
    }

    private static final String TEST1 =
    "public class NullAssignment1 {" + PMD.EOL +
    " public Object foo() {" + PMD.EOL +
    "  Object x = null; // OK" + PMD.EOL +
    "  return x;" + PMD.EOL +
    " }       " + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class NullAssignment2 {" + PMD.EOL +
    " public void foo() {" + PMD.EOL +
    "  Object x;" + PMD.EOL +
    "  x = new Object();" + PMD.EOL +
    "  for (int y = 0; y < 10; y++) {" + PMD.EOL +
    "   System.err.println(y);  " + PMD.EOL +
    "  }" + PMD.EOL +
    "  x = null; // This is bad" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class NullAssignment3 {" + PMD.EOL +
    "       public void foo() {" + PMD.EOL +
    "               Object x;" + PMD.EOL +
    "               if (x == null) { // This is OK" + PMD.EOL +
    "                       return;" + PMD.EOL +
    "               }" + PMD.EOL +
    "       }" + PMD.EOL +
    "}";

    private static final String TEST4 =
    "public class NullAssignment4 {" + PMD.EOL +
    " public void foo() {" + PMD.EOL +
    "  String x = null;" + PMD.EOL +
    "  x = new String(null);" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

}
