/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class EmptyTryBlockRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("rulesets/basic.xml", "EmptyTryBlock");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "bad", 1, rule),
           new TestDescriptor(TEST2, "bad", 1, rule),
           new TestDescriptor(TEST3, "ok", 0, rule)
       });
    }

    private static final String TEST1 =
    "public class EmptyTryBlock1 {" + PMD.EOL +
    "       public void foo() {" + PMD.EOL +
    "               try {" + PMD.EOL +
    "               } catch (Exception e) {" + PMD.EOL +
    "                       e.printStackTrace();" + PMD.EOL +
    "               }" + PMD.EOL +
    "       }" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class EmptyTryBlock2 {" + PMD.EOL +
    "       public void foo() {" + PMD.EOL +
    "               try {" + PMD.EOL +
    "               } finally {" + PMD.EOL +
    "                       int x = 5;" + PMD.EOL +
    "               }" + PMD.EOL +
    "       }" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class EmptyTryBlock3 {" + PMD.EOL +
    "       public void foo() {" + PMD.EOL +
    "               try {" + PMD.EOL +
    "                       int f =2;" + PMD.EOL +
    "               } finally {" + PMD.EOL +
    "                       int x = 5;" + PMD.EOL +
    "               }" + PMD.EOL +
    "       }" + PMD.EOL +
    "}";

}
