/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class EmptyFinallyBlockRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("rulesets/basic.xml", "EmptyFinallyBlock");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "", 1, rule),
           new TestDescriptor(TEST2, "", 1, rule),
           new TestDescriptor(TEST3, "", 0, rule),
           new TestDescriptor(TEST4, "multiple catch blocks with finally", 1, rule),
       });
    }

    private static final String TEST1 =
    "public class EmptyFinallyBlock1 {" + PMD.EOL +
    "    public void foo() {" + PMD.EOL +
    "       try {" + PMD.EOL +
    "       } catch (Exception e) {} finally {}" + PMD.EOL +
    "    }" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class EmptyFinallyBlock2 {" + PMD.EOL +
    "    public void foo() {" + PMD.EOL +
    "       try {" + PMD.EOL +
    "       } finally {}" + PMD.EOL +
    "    }" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class EmptyFinallyBlock3 {" + PMD.EOL +
    "    public void foo() {" + PMD.EOL +
    "       try {" + PMD.EOL +
    "       } finally {int x =2;}" + PMD.EOL +
    "    }" + PMD.EOL +
    "}";

    private static final String TEST4 =
    "public class EmptyFinallyBlock4 {" + PMD.EOL +
    " public void foo() {" + PMD.EOL +
    "  try {" + PMD.EOL +
    "  } catch (IOException e ){" + PMD.EOL +
    "  } catch (Exception e ) {" + PMD.EOL +
    "  } catch (Throwable t ) {" + PMD.EOL +
    "  } finally{" + PMD.EOL +
    "  }" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

}
