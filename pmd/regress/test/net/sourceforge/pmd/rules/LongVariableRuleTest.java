package test.net.sourceforge.pmd.rules;

import test.net.sourceforge.pmd.*;

import net.sourceforge.pmd.*;
import net.sourceforge.pmd.rules.LongVariableRule;
import net.sourceforge.pmd.renderers.Renderer;

import junit.framework.*;

public class LongVariableRuleTest
    extends RuleTst
{
    public LongVariableRuleTest( String name ) {
	super( name );
    }

    public void testLongVariableField() throws Throwable {
	Report report = process("LongVariableField.java",
				new LongVariableRule() );
	assertEquals( 1, report.size() );
    }

    public void testLongVariableLocal() throws Throwable {
	Report report = process("LongVariableLocal.java",
				new LongVariableRule() );
	assertEquals( 1, report.size() );
    }

    public void testLongVariableFor() throws Throwable {
	Report report = process("LongVariableFor.java",
				new LongVariableRule() );
	assertEquals( 1, report.size() );
    }

    public void testLongVariableParam() throws Throwable {
	Report report = process("LongVariableParam.java",
				new LongVariableRule() );
	assertEquals( 1, report.size() );
    }

    public void testLongVariableNone() throws Throwable {
	Report report = process("LongVariableNone.java",
				new LongVariableRule() );
	assertEquals( 0, report.size() );
    }
}
