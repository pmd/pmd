package test.net.sourceforge.pmd.rules;

import test.net.sourceforge.pmd.*;

import net.sourceforge.pmd.*;
import net.sourceforge.pmd.rules.ShortVariableRule;
import net.sourceforge.pmd.renderers.Renderer;

import junit.framework.*;

public class ShortVariableRuleTest
    extends RuleTst
{

    private ShortVariableRule rule;

    public ShortVariableRuleTest( String name ) {
	super( name );
    }

    public void setUp() {
        rule = new ShortVariableRule();
        rule.setMessage("Avoid stuff like -> ''{0}''");
    }

    public void testShortVariableField() throws Throwable {
	Report report = process("ShortVariableField.java",
				rule );
	assertEquals( 1, report.size() );
    }

    public void testShortVariableLocal() throws Throwable {
	Report report = process("ShortVariableLocal.java",
				rule );
	assertEquals( 1, report.size() );
    }

    public void testShortVariableFor() throws Throwable {
	Report report = process("ShortVariableFor.java",
				rule );
	assertEquals( 0, report.size() );
    }

    public void testShortVariableParam() throws Throwable {
	Report report = process("ShortVariableParam.java",
				rule );
	assertEquals( 1, report.size() );
    }

    public void testShortVariableNone() throws Throwable {
	Report report = process("ShortVariableNone.java",
				rule );
	assertEquals( 0, report.size() );
    }
}
