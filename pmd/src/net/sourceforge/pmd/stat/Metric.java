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
 */
package net.sourceforge.pmd.stat;

/**
 * @author David Dixon-Peugh
 *
 * This class holds all sorts of statistical information.
 */
public class Metric {
    private String metricName = null;
    private int count = 0;
    private double total = 0.0;
    private double low = -1.0;
    private double high = -1.0;
    private double mean = -1.0;
    private double stddev = -1.0;

    public Metric(String name, int count, double total, double low, double high, double mean, double stddev) {
        this.metricName = name;
        this.low = low;
        this.high = high;
        this.mean = mean;
        this.stddev = stddev;
        this.count = count;
        this.total = total;
    }

    public String getMetricName() {
        return metricName;
    }

    public double getLowValue() {
        return low;
    }

    public double getHighValue() {
        return high;
    }

    public double getAverage() {
        return mean;
    }

    public double getStandardDeviation() {
        return stddev;
    }

    public int getCount() {
        return count;
    }

    public double getTotal() {
        return total;
    }
}
