/**
 * Created on Aug 28, 2002
 */
package test.net.sourceforge.pmd.stat;

import junit.framework.TestCase;
import net.sourceforge.pmd.stat.Metric;

import java.util.Random;

/**
 * @author David Dixon-Peugh
 */
public class MetricTest extends TestCase {
	private String testName = null;
	private Random random = new Random();
		
    /**
     * Constructor for MetricTest.
     * @param arg0
     */
    public MetricTest(String arg0) {
        super(arg0);
		this.testName = arg0;
    }

    public void testGetMetricName() {
    	Metric IUT = new Metric(testName, 0.0, 0.0, 0.0, 0.0);
    	
    	assertEquals( testName, IUT.getMetricName() );
    }

    public void testGetLowValue() {
		double low = random.nextDouble();
		Metric IUT = new Metric( testName, low, 0.0, 0.0, 0.0);
		assertEquals( low, IUT.getLowValue(), 0.05 );    	
    }

    public void testGetHighValue() {
    	double high = random.nextDouble();
    	Metric IUT = new Metric( testName, 0.0, high, 0.0, 0.0);
    	assertEquals( high, IUT.getHighValue(), 0.05 );
    }

    public void testGetAverage() {
    	double mean = random.nextDouble();
    	Metric IUT = new Metric( testName, 0.0, 0.0, mean, 0.0);
    	assertEquals( mean, IUT.getAverage(), 0.05);
    }

    public void testGetStandardDeviation() {
    	double stdev = random.nextDouble();
    	Metric IUT = new Metric( testName, 0.0, 0.0, 0.0, stdev);
    	assertEquals( stdev, IUT.getStandardDeviation(), 0.05);
    }

}
