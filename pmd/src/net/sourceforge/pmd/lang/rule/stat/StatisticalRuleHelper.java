/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule.stat;

import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.rule.AbstractRule;
import net.sourceforge.pmd.lang.rule.properties.DoubleProperty;
import net.sourceforge.pmd.lang.rule.properties.IntegerProperty;
import net.sourceforge.pmd.stat.DataPoint;
import net.sourceforge.pmd.stat.Metric;

/**
 * This class is used to implement the core logic of a StatisticalRule.
 * Concrete Rule implementations should delegate to an instance of this class.
 * 
 * @author David Dixon-Peugh
 *         Aug 8, 2002 StatisticalRule.java
 */
public class StatisticalRuleHelper {

    public static final double DELTA = 0.000005; // Within this range. . .
    
    private AbstractRule rule;

    private SortedSet<DataPoint> dataPoints = new TreeSet<DataPoint>();

    private int count = 0;
    private double total = 0.0;

    private static final PropertyDescriptor SIGMA_DESCRIPTOR = new DoubleProperty(
    	"sigma", "Sigma value",	0,	1.0f
    	);

    private static final PropertyDescriptor MINIMUM_DESCRIPTOR = new DoubleProperty(
        "minimum", "Minimum value",	0,	2.0f
        );

    private static final PropertyDescriptor TOP_SCORE_DESCRIPTOR = new IntegerProperty(
        "topscore", "Top score value",	0,	3.0f
        );

    private static final Map<String, PropertyDescriptor> PROPERTY_DESCRIPTORS_BY_NAME = AbstractRule.asFixedMap( new PropertyDescriptor[] {
    	SIGMA_DESCRIPTOR, MINIMUM_DESCRIPTOR, TOP_SCORE_DESCRIPTOR
    	});

    public StatisticalRuleHelper(AbstractRule rule)
    {
	this.rule = rule;
    }

    public void addDataPoint(DataPoint point) {
        count++;
        total += point.getScore();
        dataPoints.add(point);
    }

    public void apply(RuleContext ctx) {

        double deviation;
        double minimum = 0.0;

        if (rule.hasProperty("sigma")) {	// TODO - need to come up with a good default value
            deviation = getStdDev();
            double sigma = rule.getDoubleProperty(SIGMA_DESCRIPTOR);
            minimum = getMean() + (sigma * deviation);
        }

        if (rule.hasProperty("minimum")) {	// TODO - need to come up with a good default value
            double mMin = rule.getDoubleProperty(MINIMUM_DESCRIPTOR);
            if (mMin > minimum) {
                minimum = mMin;
            }
        }

        SortedSet<DataPoint> newPoints = applyMinimumValue(dataPoints, minimum);

        if (rule.hasProperty("topscore")) { // TODO - need to come up with a good default value
            int topScore = rule.getIntProperty(TOP_SCORE_DESCRIPTOR);
            if (newPoints.size() >= topScore) {
                newPoints = applyTopScore(newPoints, topScore);
            }
        }

        makeViolations(ctx, newPoints);

        double low = 0.0d;
        double high = 0.0d;
        if (!dataPoints.isEmpty()) {
            low = dataPoints.first().getScore();
            high = dataPoints.last().getScore();
        }

        ctx.getReport().addMetric(new Metric(rule.getName(), count, total, low, high, getMean(), getStdDev()));

        dataPoints.clear();
    }

    private double getMean() {
        return total / count;
    }

    private double getStdDev() {
        if (dataPoints.size() < 2) {
            return Double.NaN;
        }

        double mean = getMean();
        double deltaSq = 0.0;
        double scoreMinusMean;

        for (DataPoint point: dataPoints) {
            scoreMinusMean = point.getScore() - mean;
            deltaSq += scoreMinusMean * scoreMinusMean;
        }

        return Math.sqrt(deltaSq / (dataPoints.size() - 1));
    }

    private SortedSet<DataPoint> applyMinimumValue(SortedSet<DataPoint> pointSet, double minValue) {
        SortedSet<DataPoint> rc = new TreeSet<DataPoint>();
        double threshold = minValue - DELTA;

        for (DataPoint point: pointSet) {
            if (point.getScore() > threshold) {
                rc.add(point);
            }
        }
        return rc;
    }

    private SortedSet<DataPoint> applyTopScore(SortedSet<DataPoint> points, int topScore) {
        SortedSet<DataPoint> s = new TreeSet<DataPoint>();
        DataPoint[] arr = points.toArray(new DataPoint[]{});
        for (int i = arr.length - 1; i >= (arr.length - topScore); i--) {
            s.add(arr[i]);
        }
        return s;
    }

    private void makeViolations(RuleContext ctx, Set<DataPoint> p) {
        for (DataPoint point: p) {
            rule.addViolationWithMessage(ctx, point.getNode(), point.getMessage(), ((StatisticalRule)rule).getViolationParameters(point));
        }
    }

    /**
     * Users of the helper should use this Map to describe their properties.
     */
    public Map<String, PropertyDescriptor> propertiesByName() {
    	return PROPERTY_DESCRIPTORS_BY_NAME;
    }
}
