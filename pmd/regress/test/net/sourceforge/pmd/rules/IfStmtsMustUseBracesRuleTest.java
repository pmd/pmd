/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class IfStmtsMustUseBracesRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("rulesets/braces.xml", "IfStmtsMustUseBraces");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "simple failure case", 1, rule),
           new TestDescriptor(TEST2, "ok", 0, rule),
           new TestDescriptor(TEST3, "nested ifs", 1, rule),
       });
    }

    private static final String TEST1 =
    "public class IfStmtsMustUseBraces1 {" + PMD.EOL +
    " public void foo() {" + PMD.EOL +
    "  int x = 0;   " + PMD.EOL +
    "  if (true) x=2;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class IfStmtsMustUseBraces2 {" + PMD.EOL +
    " public void foo() {   " + PMD.EOL +
    "  if (true) {" + PMD.EOL +
    "   int x=2;" + PMD.EOL +
    "  }" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class IfStmtsMustUseBraces3 {" + PMD.EOL +
    " public void foo() {   " + PMD.EOL +
    "  if (true) {" + PMD.EOL +
    "   if (true) bar();" + PMD.EOL +
    "  }" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

}
