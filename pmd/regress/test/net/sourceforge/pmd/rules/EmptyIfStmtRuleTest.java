/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class EmptyIfStmtRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("rulesets/basic.xml", "EmptyIfStmt");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "one empty, one not empty", 1, rule),
       });
    }

    private static final String TEST1 =
    "public class EmptyIfStmtRule {" + PMD.EOL +
    "    public EmptyIfStmtRule() {" + PMD.EOL +
    "       if (null == null) {" + PMD.EOL +
    "       }" + PMD.EOL +
    "       if (null != null) {" + PMD.EOL +
    "               this.toString();" + PMD.EOL +
    "       }" + PMD.EOL +
    "    }" + PMD.EOL +
    "}";


}
