package test.net.sourceforge.pmd.rules;

import test.net.sourceforge.pmd.*;

import net.sourceforge.pmd.*;
import net.sourceforge.pmd.rules.ShortMethodNameRule;

import junit.framework.*;

public class ShortMethodNameRuleTest
    extends RuleTst
{

    private ShortMethodNameRule rule;

    public void setUp() {
        rule = new ShortMethodNameRule();
        rule.setMessage("Avoid this stuff -> {0}");
        rule.addProperty("minimumLength", "3");
    }

    public void testShortMethodName0() throws Throwable {
	Report report = process("ShortMethodName0.java",
				rule );
	assertEquals( 0, report.size() );
    }

    public void testShortMethodName1() throws Throwable {
	Report report = process("ShortMethodName1.java",
				rule );
	assertEquals( 1, report.size() );
    }

    public void testShortMethodName2() throws Throwable {
	Report report = process("ShortMethodName2.java",
				rule );
	assertEquals( 2, report.size() );
    }

    public void testShortMethodName3() throws Throwable {
	Report report = process("ShortMethodName3.java",
				rule );
	assertEquals( 1, report.size() );
    }
}
