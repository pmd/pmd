/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.rules.design.LongParameterListRule;
import test.net.sourceforge.pmd.testframework.RuleTst;

public class LongParameterListRuleTest extends RuleTst {

    private static final String TEST1 =
    "public class LongParameterList0 {" + PMD.EOL +
    "    public void foo() {}" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class LongParameterList1 {" + PMD.EOL +
    "    public void foo(int p01, int p02, int p03, int p04, int p05," + PMD.EOL +
    "                   int p06, int p07, int p08, int p09, int p10 ) { }" + PMD.EOL +
    "    public void bar(int p01, int p02, int p03, int p04, int p05 ) { }" + PMD.EOL +
    "}";

    public LongParameterListRule getIUT() {
        LongParameterListRule IUT = new LongParameterListRule();
        IUT.addProperty("minimum", "9");
        return IUT;
    }

    public void testShortMethod() throws Throwable {
        runTestFromString(TEST1, 0, getIUT());
    }

    public void testOneLongMethod() throws Throwable {
        runTestFromString(TEST2, 1, getIUT());
    }
}
