/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.rules.CouplingBetweenObjectsRule;
import test.net.sourceforge.pmd.testframework.RuleTst;

public class CouplingBetweenObjectsRuleTest extends RuleTst {

    private static final String TEST1 =
    "import java.util.*;" + PMD.EOL +
    "public class CouplingBetweenObjects1 {" + PMD.EOL +
    " public List foo() {return null;}" + PMD.EOL +
    " public ArrayList foo() {return null;}" + PMD.EOL +
    " public Vector foo() {return null;}" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class CouplingBetweenObjects2 {" + PMD.EOL +
    "}";


    private Rule rule;

    public void setUp() {
        rule = new CouplingBetweenObjectsRule();
        rule.addProperty("threshold", "2");
    }

    public void testSimpleBad() throws Throwable {
        runTestFromString(TEST1, 1, rule);
    }

    public void testSimpleOK() throws Throwable {
        runTestFromString(TEST2, 0, rule);
    }
}
