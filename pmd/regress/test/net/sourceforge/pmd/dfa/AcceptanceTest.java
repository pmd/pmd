/*
 * Created on 18.08.2004
 */
package test.net.sourceforge.pmd.dfa;

import net.sourceforge.pmd.Report;
import test.net.sourceforge.pmd.testframework.RuleTst;

public class AcceptanceTest extends RuleTst {

    public void testAll() throws Throwable {
        runTestFromString(AcceptanceTestRule.TEST, new AcceptanceTestRule(), new Report());
    }

}
