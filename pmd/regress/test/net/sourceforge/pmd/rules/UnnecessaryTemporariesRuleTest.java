package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.rules.UnnecessaryConversionTemporaryRule;
import net.sourceforge.pmd.cpd.CPD;

public class UnnecessaryTemporariesRuleTest extends RuleTst {

    private static final String TEST1 =
    " public class UnnecessaryTemporary1 {" + CPD.EOL +
    "     void method (int x) {" + CPD.EOL +
    "        new Integer(x).toString(); " + CPD.EOL +
    "        new Long(x).toString(); " + CPD.EOL +
    "        new Float(x).toString(); " + CPD.EOL +
    "        new Byte((byte)x).toString(); " + CPD.EOL +
    "        new Double(x).toString(); " + CPD.EOL +
    "        new Short((short)x).toString(); " + CPD.EOL +
    "     }" + CPD.EOL +
    " }";

    public void testSimple() throws Throwable {
        runTestFromString(TEST1, 6, new UnnecessaryConversionTemporaryRule());
    }
}
