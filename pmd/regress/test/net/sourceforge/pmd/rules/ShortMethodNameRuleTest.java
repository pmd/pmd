package test.net.sourceforge.pmd.rules;

import test.net.sourceforge.pmd.*;

import net.sourceforge.pmd.*;
import net.sourceforge.pmd.rules.ShortMethodNameRule;
import net.sourceforge.pmd.reports.Report;

import junit.framework.*;

public class ShortMethodNameRuleTest
    extends RuleTst
{
    public ShortMethodNameRuleTest( String name ) {
	super( name );
    }

    public void testShortMethodName0() throws Throwable {
	Report report = process("ShortMethodName0.java",
				new ShortMethodNameRule() );
	assertEquals( 0, report.size() );
    }

    public void testShortMethodName1() throws Throwable {
	Report report = process("ShortMethodName1.java",
				new ShortMethodNameRule() );
	assertEquals( 1, report.size() );
    }

    public void testShortMethodName2() throws Throwable {
	Report report = process("ShortMethodName2.java",
				new ShortMethodNameRule() );
	assertEquals( 2, report.size() );
    }

    public void testShortMethodName3() throws Throwable {
	Report report = process("ShortMethodName3.java",
				new ShortMethodNameRule() );
	assertEquals( 1, report.size() );
    }
}
