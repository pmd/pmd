/**
 * Created on Sep 4, 2002
 */
package test.net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.rules.design.SwitchDensityRule;
import test.net.sourceforge.pmd.rules.RuleTst;

/**
 * @author dpeugh
 *
 * This tests the new SwitchDensity rule to see if it really
 * does work.
 */
public class SwitchDensityTest extends RuleTst {

    /**
     * Constructor for SwitchDensityTest.
     * @param arg0
     */
    public SwitchDensityTest() {
        super();
    }

	public SwitchDensityRule getIUT() {
		SwitchDensityRule RC = new SwitchDensityRule();
		RC.addProperty("minimum", "4");	
		
		return RC;
	}
	
	public void testSD1() throws Throwable {
		runTest("SwitchDensity1.java", 1);	
	}
	
	public void testSD2() throws Throwable {
		runTest("SwitchDensity2.java", 0);
	}
	
	public void testSD3() throws Throwable {
		runTest("SwitchDensity3.java", 0);
	}
	
	public void runTest(String fileName, int expected ) 
		throws Throwable 
	{
		SwitchDensityRule sdr = getIUT();
		Report report = process(fileName, sdr);
		assertEquals(expected, report.size());
	}
}
