/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class WhileLoopsMustUseBracesRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("rulesets/braces.xml", "WhileLoopsMustUseBracesRule");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "no braces", 1, rule),
           new TestDescriptor(TEST2, "with braces", 0, rule),
       });
    }

    private static final String TEST1 =
    "public class WhileLoopsNeedBraces1 {" + PMD.EOL +
    " void foo() {   " + PMD.EOL +
    "  int x =0;" + PMD.EOL +
    "  while (true)" + PMD.EOL +
    "   x++;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class WhileLoopsNeedBraces2 {" + PMD.EOL +
    " void foo() {   " + PMD.EOL +
    "  int x =0;" + PMD.EOL +
    "  while (true) {" + PMD.EOL +
    "   x++;" + PMD.EOL +
    "  }" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

}
