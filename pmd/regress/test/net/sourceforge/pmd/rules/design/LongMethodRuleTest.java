/* $Id$ */

package test.net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.rules.design.LongMethodRule;

import test.net.sourceforge.pmd.rules.RuleTst;

public class LongMethodRuleTest
    extends RuleTst
{

    public LongMethodRule getIUT() {
        LongMethodRule IUT = new LongMethodRule();
        IUT.addProperty("minimum", "200");
        return IUT;
    }
    public void testShortMethod() throws Throwable {
	Report report = process("LongMethod1.java", getIUT() );
	assertEquals( 0, report.size() );
    }

    public void testReallyLongMethod() throws Throwable {
	Report report = process("LongMethod2.java", getIUT() );
	assertEquals( 1, report.size() );
    }

    public void testReallyLongMethodWithLongerRange() throws Throwable {
        LongMethodRule IUT = getIUT();
        IUT.addProperty("minimum", "1000");
	Report report = process("LongMethod2.java", IUT );
	assertEquals( 0, report.size() );
    }

    public void testNotQuiteLongMethod() throws Throwable {
	Report report = process("LongMethod3.java", getIUT() );
	assertEquals( 0, report.size() );
    }
    public void testLongMethod() throws Throwable {
	Report report = process("LongMethod4.java", getIUT() );
	assertEquals( 1, report.size() );
    }
}
