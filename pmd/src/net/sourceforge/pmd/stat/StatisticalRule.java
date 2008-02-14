/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.stat;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import net.sourceforge.pmd.AbstractJavaRule;
import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.properties.DoubleProperty;
import net.sourceforge.pmd.properties.IntegerProperty;

/**
 * @author David Dixon-Peugh
 *         Aug 8, 2002 StatisticalRule.java
 */
public abstract class StatisticalRule extends AbstractJavaRule {

    public static final double DELTA = 0.000005; // Within this range. . .

    private SortedSet<DataPoint> dataPoints = new TreeSet<DataPoint>();

    private int count = 0;
    private double total = 0.0;

    private static final PropertyDescriptor sigmaDescriptor = new DoubleProperty(
    	"sigma", "Sigma value",	0,	1.0f
    	);

    private static final PropertyDescriptor minimumDescriptor = new DoubleProperty(
        "minimum", "Minimum value",	0,	1.0f
        );

    private static final PropertyDescriptor topScoreDescriptor = new IntegerProperty(
        "topscore", "Top score value",	0,	1.0f
        );

    private static final Map<String, PropertyDescriptor> propertyDescriptorsByName = asFixedMap( new PropertyDescriptor[] {
    	sigmaDescriptor, minimumDescriptor, topScoreDescriptor
    	});


    public void addDataPoint(DataPoint point) {
        count++;
        total += point.getScore();
        dataPoints.add(point);
    }

    public void apply(List acus, RuleContext ctx) {
        visitAll(acus, ctx);

        double deviation;
        double minimum = 0.0;

        if (hasProperty("sigma")) {	// TODO - need to come up with a good default value
            deviation = getStdDev();
            double sigma = getDoubleProperty(sigmaDescriptor);
            minimum = getMean() + (sigma * deviation);
        }

        if (hasProperty("minimum")) {	// TODO - need to come up with a good default value
            double mMin = getDoubleProperty(minimumDescriptor);
            if (mMin > minimum) {
                minimum = mMin;
            }
        }

        SortedSet<DataPoint> newPoints = applyMinimumValue(dataPoints, minimum);

        if (hasProperty("topscore")) { // TODO - need to come up with a good default value
            int topScore = getIntProperty(topScoreDescriptor);
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

        ctx.getReport().addMetric(new Metric(this.getName(), count, total, low, high, getMean(), getStdDev()));

        dataPoints.clear();
    }

    protected double getMean() {
        return total / count;
    }

    protected double getStdDev() {
        if (dataPoints.size() < 2) {
            return Double.NaN;
        }

        double mean = getMean();
        double deltaSq = 0.0;
        double scoreMinusMean;

        for (DataPoint point: dataPoints) {
            scoreMinusMean = point.getScore() - mean;
            deltaSq += (scoreMinusMean * scoreMinusMean);
        }

        return Math.sqrt(deltaSq / (dataPoints.size() - 1));
    }

    protected SortedSet<DataPoint> applyMinimumValue(SortedSet<DataPoint> pointSet, double minValue) {
        SortedSet<DataPoint> RC = new TreeSet<DataPoint>();
        double threshold = minValue - DELTA;

        for (DataPoint point: pointSet) {
            if (point.getScore() > threshold) {
                RC.add(point);
            }
        }
        return RC;
    }

    protected SortedSet<DataPoint> applyTopScore(SortedSet<DataPoint> points, int topScore) {
        SortedSet<DataPoint> s = new TreeSet<DataPoint>();
        DataPoint[] arr = points.toArray(new DataPoint[]{});
        for (int i = arr.length - 1; i >= (arr.length - topScore); i--) {
            s.add(arr[i]);
        }
        return s;
    }

    protected void makeViolations(RuleContext ctx, Set<DataPoint> p) {
        for (DataPoint point: p) {
            addViolationWithMessage(ctx, point.getNode(), point.getMessage());
        }
    }

    protected Map<String, PropertyDescriptor> propertiesByName() {
    	return propertyDescriptorsByName;
    }
}
