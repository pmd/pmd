package test.net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.ReportListener;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.rules.design.UseSingletonRule;
import net.sourceforge.pmd.stat.Metric;
import test.net.sourceforge.pmd.rules.RuleTst;

public class UseSingletonRuleTest
    extends RuleTst implements ReportListener
{

    public void testUseSingleton1()
	throws Throwable 
    {
	Report report = process("UseSingleton1.java",
				new UseSingletonRule());
	assertEquals( 1, report.size() );
    }

    public void testUseSingleton2() 
	throws Throwable 
    {
	Report report = process("UseSingleton2.java",
				new UseSingletonRule());
	assertEquals( 0, report.size() );
    }

    public void testUseSingleton3() 
	throws Throwable 
    {
	Report report = process("UseSingleton3.java",
				new UseSingletonRule());
	assertEquals( 1, report.size() );
    }


    public void testUseSingleton4()
	throws Throwable 
    {
	Report report = process("UseSingleton4.java",
				new UseSingletonRule());
	assertEquals( 0, report.size() );
    }

    public void testResetState() throws Throwable{
        callbacks = 0;
        Rule rule = new UseSingletonRule();
        Report report = new Report();
        report.addListener(this);
        process("UseSingleton3.java", rule, report);
        process("UseSingleton4.java", rule, report);
        assertEquals( 1, callbacks );
    }

    private int callbacks;
    public void ruleViolationAdded(RuleViolation ruleViolation) {
        callbacks++;
    }
    
    public void metricAdded(Metric metric) { }
}
