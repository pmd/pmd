/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.stat;

/**
 * This class holds all sorts of statistical information.
 *
 * @author David Dixon-Peugh
 */
public class Metric {
    private String metricName = null;
    private int count = 0;
    private double total = 0.0;
    private double low = -1.0;
    private double high = -1.0;
    private double mean = -1.0;
    private double stddev = -1.0;

    /**
     * Creates a new metric with the given information.
     * @param name the metric's name
     * @param count count of occurrences
     * @param total the total value of the metric
     * @param low the lowest value of the metric
     * @param high the highest value of the metric
     * @param mean the mean value
     * @param stddev the standard deviation
     */
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
