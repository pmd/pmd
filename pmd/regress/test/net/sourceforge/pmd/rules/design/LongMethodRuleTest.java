/* $Id$ */

package test.net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.rules.design.LongMethodRule;

import test.net.sourceforge.pmd.rules.RuleTst;

public class LongMethodRuleTest
    extends RuleTst
{
    public LongMethodRuleTest( String name ) {
	super( name );
    }

    public void testShortMethod() throws Throwable {
	Report report = process("LongMethod1.java", 
				new LongMethodRule());
	assertEquals( 0, report.size() );
    }

    public void testReallyLongMethod() throws Throwable {
	Report report = process("LongMethod2.java",
				new LongMethodRule() );
	assertEquals( 1, report.size() );
    }

    public void testNotQuiteLongMethod() throws Throwable {
	Report report = process("LongMethod3.java",
				new LongMethodRule() );
	assertEquals( 0, report.size() );
    }
    public void testLongMethod() throws Throwable {
	Report report = process("LongMethod4.java",
				new LongMethodRule() );
	assertEquals( 1, report.size() );
    }
}
