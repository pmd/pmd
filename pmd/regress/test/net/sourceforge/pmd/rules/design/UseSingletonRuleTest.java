package test.net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.ReportListener;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.rules.design.UseSingletonRule;
import net.sourceforge.pmd.stat.Metric;
import test.net.sourceforge.pmd.rules.RuleTst;

public class UseSingletonRuleTest extends RuleTst implements ReportListener
{

    public void testAllStaticsPublicConstructor() throws Throwable {
        runTest("UseSingleton1.java", 1, new UseSingletonRule());
    }

    public void testOKDueToNonStaticMethod()  throws Throwable {
        runTest("UseSingleton2.java", 0, new UseSingletonRule());
    }

    public void testNoConstructorCoupleOfStatics()  throws Throwable {
        runTest("UseSingleton3.java", 1, new UseSingletonRule());
    }

    public void testNoConstructorOneStatic()  throws Throwable {
        runTest("UseSingleton4.java", 0, new UseSingletonRule());
    }

    public void testClassicSingleton()  throws Throwable {
        runTest("UseSingleton5.java", 0, new UseSingletonRule());
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
