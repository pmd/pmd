/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class ShortMethodNameRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("rulesets/naming.xml", "ShortMethodNameRule");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "ok", 0, rule),
           new TestDescriptor(TEST2, "bad", 1, rule),
           new TestDescriptor(TEST3, "2 violations", 2, rule),
           new TestDescriptor(TEST4, "2 methods, 1 violation", 1, rule),
       });
    }

    private static final String TEST1 =
    "public class ShortMethodName0 {" + PMD.EOL +
    "    public int abcd( int i ) {" + PMD.EOL +
    "       // Should not violate." + PMD.EOL +
    "    }" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class ShortMethodName1 {" + PMD.EOL +
    "    public int a( int i ) {" + PMD.EOL +
    "       // Should violate." + PMD.EOL +
    "    }" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class ShortMethodName2 {" + PMD.EOL +
    "    public int a( int i ) {" + PMD.EOL +
    "       // Should violate" + PMD.EOL +
    "    }" + PMD.EOL +
    "" + PMD.EOL +
    "    public int b( int i ) {" + PMD.EOL +
    "       // Should violate" + PMD.EOL +
    "    }" + PMD.EOL +
    "}";

    private static final String TEST4 =
    "public class ShortMethodName3 {" + PMD.EOL +
    "    public int a( int i ) {" + PMD.EOL +
    "       // Should violate" + PMD.EOL +
    "    }" + PMD.EOL +
    "" + PMD.EOL +
    "    public int bcde( int i ) {" + PMD.EOL +
    "       // Should not violate" + PMD.EOL +
    "    }" + PMD.EOL +
    "}";

}
