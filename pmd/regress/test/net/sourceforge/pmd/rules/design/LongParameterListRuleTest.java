/* $Id$ */

package test.net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.rules.design.LongParameterListRule;

import test.net.sourceforge.pmd.rules.RuleTst;

public class LongParameterListRuleTest
    extends RuleTst
{
    public LongParameterListRule getIUT() {
	LongParameterListRule IUT = new LongParameterListRule();
	IUT.addProperty("minimum", "9");
	return IUT;
    }

    public void testShortMethod() throws Throwable {
	Report report = process("LongParameterList0.java", getIUT() );
	assertEquals( 0, report.size() );
    }

    public void testOneLongMethod() throws Throwable {
	Report report = process("LongParameterList1.java", getIUT() );
	assertEquals( 1, report.size() );
    }

//      public void testTwoLongMethods() throws Throwable {
//  	Report report = process("LongParameterList2.java", getIUT() );
//  	assertEquals( 2, report.size() );
//      }
}
