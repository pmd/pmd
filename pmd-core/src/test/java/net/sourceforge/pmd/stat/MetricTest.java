/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
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

package net.sourceforge.pmd.stat;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.Test;

/**
 * @author David Dixon-Peugh
 */
public class MetricTest {
    private String testName = "";
    private Random random = new Random();

    @Test
    public void testGetMetricName() {
        Metric metric = new Metric(testName, 0, 0.0, 0.0, 0.0, 0.0, 0.0);
        assertEquals(testName, metric.getMetricName());
    }

    @Test
    public void testGetCount() {
        int count = random.nextInt();
        Metric metric = new Metric(testName, count, 0.0, 0.0, 0.0, 0.0, 0.0);
        assertEquals(count, metric.getCount());
    }

    @Test
    public void testGetTotal() {
        double total = random.nextDouble();
        Metric metric = new Metric(testName, 0, total, 0.0, 0.0, 0.0, 0.0);
        assertEquals(total, metric.getTotal(), 0.05);
    }

    @Test
    public void testGetLowValue() {
        double low = random.nextDouble();
        Metric metric = new Metric(testName, 0, 0.0, low, 0.0, 0.0, 0.0);
        assertEquals(low, metric.getLowValue(), 0.05);
    }

    @Test
    public void testGetHighValue() {
        double high = random.nextDouble();
        Metric metric = new Metric(testName, 0, 0.0, 0.0, high, 0.0, 0.0);
        assertEquals(high, metric.getHighValue(), 0.05);
    }

    @Test
    public void testGetAverage() {
        double mean = random.nextDouble();
        Metric metric = new Metric(testName, 0, 0.0, 0.0, 0.0, mean, 0.0);
        assertEquals(mean, metric.getAverage(), 0.05);
    }

    @Test
    public void testGetStandardDeviation() {
        double stdev = random.nextDouble();
        Metric metric = new Metric(testName, 0, 0.0, 0.0, 0.0, 0.0, stdev);
        assertEquals(stdev, metric.getStandardDeviation(), 0.05);
    }
}
