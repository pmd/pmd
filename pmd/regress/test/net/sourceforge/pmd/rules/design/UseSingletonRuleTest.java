package test.net.sourceforge.pmd.rules.design;

import test.net.sourceforge.pmd.rules.*;

import java.util.List;
import java.util.ArrayList;

import java.io.InputStream;

import junit.framework.TestCase;

import net.sourceforge.pmd.*;
import net.sourceforge.pmd.ast.*;
import net.sourceforge.pmd.rules.design.*;

public class UseSingletonRuleTest
    extends RuleTst
{
    public UseSingletonRuleTest( String name ) {
	super( name );
    }

    public void testUseSingleton1() 
	throws Throwable 
    {
	Report report = process("design/UseSingleton1.java",
				new UseSingletonRule());
	assertEquals( 1, report.countViolationsInCurrentFile() );
    }

    public void testUseSingleton2() 
	throws Throwable 
    {
	Report report = process("design/UseSingleton2.java",
				new UseSingletonRule());
	assertEquals( 0, report.countViolationsInCurrentFile() );
    }

    public void testUseSingleton3() 
	throws Throwable 
    {
	Report report = process("design/UseSingleton3.java",
				new UseSingletonRule());
	assertEquals( 1, report.countViolationsInCurrentFile() );
    }
}
