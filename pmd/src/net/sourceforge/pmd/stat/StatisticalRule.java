/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.stat;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.properties.DoubleProperty;
import net.sourceforge.pmd.properties.IntegerProperty;

/**
 * @author David Dixon-Peugh
 *         Aug 8, 2002 StatisticalRule.java
 */
public abstract class StatisticalRule extends AbstractRule {

    public static double DELTA = 0.000005; // Within this range. . .

    private SortedSet dataPoints = new TreeSet();

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
    
    private static final Map propertyDescriptorsByName = asFixedMap( new PropertyDescriptor[] {
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

        SortedSet newPoints = applyMinimumValue(dataPoints, minimum);

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
            low = ((DataPoint) dataPoints.first()).getScore();
            high = ((DataPoint) dataPoints.last()).getScore();
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

        Iterator points = dataPoints.iterator();
        double mean = getMean();
        double deltaSq = 0.0;
        double scoreMinusMean;

        while (points.hasNext()) {
            scoreMinusMean = ((DataPoint) points.next()).getScore() - mean;
            deltaSq += (scoreMinusMean * scoreMinusMean);
        }

        return Math.sqrt(deltaSq / (dataPoints.size() - 1));
    }

    protected SortedSet applyMinimumValue(SortedSet pointSet, double minValue) {
        Iterator points = pointSet.iterator();
        SortedSet RC = new TreeSet();
        double threshold = minValue - DELTA;

        while (points.hasNext()) {
            DataPoint point = (DataPoint) points.next();

            if (point.getScore() > threshold) {
                RC.add(point);
            }
        }
        return RC;
    }

    protected SortedSet applyTopScore(SortedSet points, int topScore) {
        SortedSet s = new TreeSet();
        Object[] arr = points.toArray();
        for (int i = arr.length - 1; i >= (arr.length - topScore); i--) {
            s.add(arr[i]);
        }
        return s;
    }

    protected void makeViolations(RuleContext ctx, Set p) {
        Iterator points = p.iterator();
        while (points.hasNext()) {
            DataPoint point = (DataPoint) points.next();
            addViolationWithMessage(ctx, point.getNode(), point.getMessage());
        }
    }
    
    protected Map propertiesByName() {
    	return propertyDescriptorsByName;
    }
}
