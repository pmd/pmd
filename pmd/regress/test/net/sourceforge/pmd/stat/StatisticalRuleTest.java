package test.net.sourceforge.pmd.stat;

import java.util.List;
import java.util.ArrayList;

import net.sourceforge.pmd.*;
import net.sourceforge.pmd.stat.*;

import junit.framework.TestCase;

public class StatisticalRuleTest
    extends TestCase
{
    private DataPoint points[] = new DataPoint[1000];
    private MockStatisticalRule IUT = null;
    private String testName = null;

    public StatisticalRuleTest(String name) 
    {
	super( name );
	this.testName = name;
    }

    public void setUp() {
	IUT = new MockStatisticalRule();
	for (int i = 0; i < 1000; i++) {
	    points[i] = new DataPoint();
	    points[i].setScore( 1.0 * i );
	    points[i].setLineNumber( i );
	    points[i].setMessage("DataPoint[" + Integer.toString(i) + "]");

	    IUT.addDataPoint( points[i] );
	}
    }

    public void testMinimumValue() {
	IUT.addProperty("minimum", "499.0");
	Report report = makeReport( IUT );

	assertEquals("Expecting 500 results.",
		     500, report.size() );
    }

    public void testSigma() {
	IUT.addProperty("sigma", "1.0");
	Report report = makeReport( IUT );

	assertEquals("Expecting 211 results.",
		     211, report.size() );
    }

    public void testTopScore() {
	IUT.addProperty("topscore", "10");
	Report report = makeReport( IUT );
	
	assertEquals("Expecting 10 results.",
		     10, report.size() );
    }

    public Report makeReport( Rule IUT ) {
	List list = new ArrayList();
	Report report = new Report();

	RuleContext ctx = new RuleContext();
	ctx.setReport( report );
	ctx.setSourceCodeFilename(testName);

	IUT.apply( list, ctx );
	
	return report;
    }
}
