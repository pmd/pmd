package test.net.sourceforge.pmd.rules;

import test.net.sourceforge.pmd.*;

import net.sourceforge.pmd.*;
import net.sourceforge.pmd.rules.ShortVariableRule;
import net.sourceforge.pmd.reports.Report;

import junit.framework.*;

public class ShortVariableRuleTest
    extends RuleTst
{
    public ShortVariableRuleTest( String name ) {
	super( name );
    }

    public void testShortVariableField() throws Throwable {
	Report report = process("ShortVariableField.java",
				new ShortVariableRule() );
	assertEquals( 1, report.size() );
    }

    public void testShortVariableLocal() throws Throwable {
	Report report = process("ShortVariableLocal.java",
				new ShortVariableRule() );
	assertEquals( 1, report.size() );
    }

    public void testShortVariableFor() throws Throwable {
	Report report = process("ShortVariableFor.java",
				new ShortVariableRule() );
	assertEquals( 0, report.size() );
    }

    public void testShortVariableParam() throws Throwable {
	Report report = process("ShortVariableParam.java",
				new ShortVariableRule() );
	assertEquals( 1, report.size() );
    }

    public void testShortVariableNone() throws Throwable {
	Report report = process("ShortVariableNone.java",
				new ShortVariableRule() );
	assertEquals( 0, report.size() );
    }
}
