/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.rules.UnnecessaryConversionTemporaryRule;
import test.net.sourceforge.pmd.testframework.RuleTst;

public class UnnecessaryTemporariesRuleTest extends RuleTst {

    private static final String TEST1 =
    " public class UnnecessaryTemporary1 {" + PMD.EOL +
    "     void method (int x) {" + PMD.EOL +
    "        new Integer(x).toString(); " + PMD.EOL +
    "        new Long(x).toString(); " + PMD.EOL +
    "        new Float(x).toString(); " + PMD.EOL +
    "        new Byte((byte)x).toString(); " + PMD.EOL +
    "        new Double(x).toString(); " + PMD.EOL +
    "        new Short((short)x).toString(); " + PMD.EOL +
    "     }" + PMD.EOL +
    " }";

    public void testSimple() throws Throwable {
        runTestFromString(TEST1, 6, new UnnecessaryConversionTemporaryRule());
    }
}
