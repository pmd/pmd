/* $Id$ */

package test.net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.rules.design.LongClassRule;
import test.net.sourceforge.pmd.rules.RuleTst;

public class LongClassRuleTest extends RuleTst {


    public LongClassRule getIUT() {
        LongClassRule IUT = new LongClassRule();
        IUT.addProperty("minimum", "1000");
        return IUT;
    }

    public void testShortClass() throws Throwable {
        Report report = process("LongClass0.java", getIUT());
        assertEquals(0, report.size());
    }

    public void testLongClass() throws Throwable {
        Report report = process("LongClass1.java", getIUT());
        assertEquals(1, report.size());
    }

    public void testLongClassWithLongerTest() throws Throwable {
        LongClassRule IUT = getIUT();
        IUT.addProperty("minimum", "2000");

        Report report = process("LongClass1.java", IUT);
        assertEquals(0, report.size());
    }

    public void testNotQuiteLongClass() throws Throwable {
        Report report = process("LongClass2.java", getIUT());
        assertEquals(0, report.size());
    }
}
