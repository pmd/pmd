/* $Id$ */

package test.net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.rules.design.LongClassRule;

import test.net.sourceforge.pmd.rules.RuleTst;

public class LongClassRuleTest
    extends RuleTst
{
    public LongClassRuleTest( String name ) {
	super( name );
    }

    public void testShortClass() throws Throwable {
	Report report = process("LongClass0.java", 
				new LongClassRule());
	assertEquals( 0, report.size() );
    }

    public void testLongClass() throws Throwable {
	Report report = process("LongClass1.java",
				new LongClassRule() );
	assertEquals( 1, report.size() );
    }

    public void testNotQuiteLongClass() throws Throwable {
	Report report = process("LongClass2.java",
				new LongClassRule() );
	assertEquals( 0, report.size() );
    }
}
