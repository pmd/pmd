/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
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
