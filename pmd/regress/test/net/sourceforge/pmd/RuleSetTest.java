package test.net.sourceforge.pmd;

import net.sourceforge.pmd.*;
import net.sourceforge.pmd.reports.Report;
import net.sourceforge.pmd.reports.ReportFactory;
import net.sourceforge.pmd.ast.*;

import java.io.StringReader;

import java.util.Set;
import java.util.List;
import java.util.Random;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collections;

import junit.framework.*;

public class RuleSetTest extends TestCase
{
    private String javaCode =
	"public class Test { }";

    public RuleSetTest( String name ) {
	super( name );
    }

    public void testConstructor() {
	RuleSet IUT = new RuleSet();
    }

    public void testRuleList() {
	RuleSet IUT = new RuleSet();

	assertEquals( "Size of RuleSet isn't zero.",
		      0, IUT.size() );

	MockRule rule = new MockRule();
	IUT.addRule( rule );

	assertEquals( "Size of RuleSet isn't one.",
		      1, IUT.size() );

	Set rules = IUT.getRules();
	
	Iterator i = rules.iterator();
	assertTrue( "Empty Set",
		    i.hasNext());
	assertEquals( "Returned set of wrong size.",
		      1, rules.size() );

	assertEquals( "Rule isn't in ruleset.",
		      rule, i.next() );

    }

    public void testAddRuleSet() {
        RuleSet set1 = new RuleSet();
        set1.addRule(new MockRule());
        RuleSet set2 = new RuleSet();
        set2.addRule(new MockRule());
        set1.addRuleSet(set2);
        assertEquals(2, set1.size());
    }

    public void testApply0Rules()
	throws Throwable
    {
	RuleSet IUT = new RuleSet();
	verifyRuleSet( IUT, 0, new HashSet() );
    }

    public void testApply1Rule()
	throws Throwable
    {
	RuleSet IUT = new RuleSet();

	MockRule rule = new MockRule();
	RuleViolation violation = new RuleViolation( rule, 1, "filename" );
	rule.addViolation( violation );

	IUT.addRule( rule );

	verifyRuleSet( IUT, 1, Collections.singleton( violation ) );
    }

    public void testApplyNRule() 
	throws Throwable
    {
	RuleSet IUT = new RuleSet();

	Random rand = new Random();
	int numRules = rand.nextInt( 10 ) + 1;
	Set ruleViolations = new HashSet();

	for (int i = 0; i < numRules; i++) {
	    MockRule rule = new MockRule();
	    RuleViolation violation = new RuleViolation( rule, i, "filename");

	    ruleViolations.add( violation );
	    rule.addViolation( violation );

	    IUT.addRule( rule );
	}

	verifyRuleSet( IUT, numRules, ruleViolations );
    }

    protected void verifyRuleSet( RuleSet IUT, 
				  int size,
				  Set values )
	throws Throwable
    {

	RuleContext context = new RuleContext();
	Set reportedValues = new HashSet();
    ReportFactory rf = new ReportFactory();
    Report report = rf.createReport("xml");

	context.setReport( report );
	IUT.apply( makeCompilationUnits(),
		   context );

	assertEquals("Invalid number of Violations Reported",
		     size, report.size() );

	Iterator violations =
	    report.iterator();
	while (violations.hasNext()) {
	    RuleViolation violation =
		(RuleViolation) violations.next();

	    reportedValues.add( violation );
	    assertTrue( "Unexpected Violation Returned: " + 
			violation,
			values.contains( violation ) );
	}

	Iterator expected = values.iterator();
	while (expected.hasNext()) {
	    RuleViolation violation =
		(RuleViolation) expected.next();
	    assertTrue( "Expected Violation not Returned: " +
			violation,
			reportedValues.contains( violation ));
	}    
    }

    
    protected List makeCompilationUnits()
	throws Throwable
    {
	List RC = new ArrayList();

	JavaParser parser = new JavaParser( new StringReader( javaCode ));
	RC.add( parser.CompilationUnit() );

	return RC;
    }
}
