/**
 * <copyright>
 *  Copyright 1997-2002 BBNT Solutions, LLC
 *  under sponsorship of the Defense Advanced Research Projects Agency (DARPA).
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the Cougaar Open Source License as published by
 *  DARPA on the Cougaar Open Source Website (www.cougaar.org).
 *
 *  THE COUGAAR SOFTWARE AND ANY DERIVATIVE SUPPLIED BY LICENSOR IS
 *  PROVIDED 'AS IS' WITHOUT WARRANTIES OF ANY KIND, WHETHER EXPRESS OR
 *  IMPLIED, INCLUDING (BUT NOT LIMITED TO) ALL IMPLIED WARRANTIES OF
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE, AND WITHOUT
 *  ANY WARRANTIES AS TO NON-INFRINGEMENT.  IN NO EVENT SHALL COPYRIGHT
 *  HOLDER BE LIABLE FOR ANY DIRECT, SPECIAL, INDIRECT OR CONSEQUENTIAL
 *  DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE OF DATA OR PROFITS,
 *  TORTIOUS CONDUCT, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *  PERFORMANCE OF THE COUGAAR SOFTWARE.
 * </copyright>
 *
 * Created on Aug 26, 2002
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
     *
     * @param arg0
     */
    public MetricTest(String arg0) {
        super(arg0);
        this.testName = arg0;
    }

    public void testGetMetricName() {
        Metric IUT = new Metric(testName, 0, 0.0, 0.0, 0.0, 0.0, 0.0);

        assertEquals(testName, IUT.getMetricName());
    }

    public void testGetCount() {
        int count = random.nextInt();
        Metric IUT = new Metric(testName, count, 0.0, 0.0, 0.0, 0.0, 0.0);
        assertEquals(count, IUT.getCount());
    }

    public void testGetTotal() {
        double total = random.nextDouble();
        Metric IUT = new Metric(testName, 0, total, 0.0, 0.0, 0.0, 0.0);
        assertEquals(total, IUT.getTotal(), 0.05);
    }

    public void testGetLowValue() {
        double low = random.nextDouble();
        Metric IUT = new Metric(testName, 0, 0.0, low, 0.0, 0.0, 0.0);
        assertEquals(low, IUT.getLowValue(), 0.05);
    }

    public void testGetHighValue() {
        double high = random.nextDouble();
        Metric IUT = new Metric(testName, 0, 0.0, 0.0, high, 0.0, 0.0);
        assertEquals(high, IUT.getHighValue(), 0.05);
    }

    public void testGetAverage() {
        double mean = random.nextDouble();
        Metric IUT = new Metric(testName, 0, 0.0, 0.0, 0.0, mean, 0.0);
        assertEquals(mean, IUT.getAverage(), 0.05);
    }

    public void testGetStandardDeviation() {
        double stdev = random.nextDouble();
        Metric IUT = new Metric(testName, 0, 0.0, 0.0, 0.0, 0.0, stdev);
        assertEquals(stdev, IUT.getStandardDeviation(), 0.05);
    }

}
