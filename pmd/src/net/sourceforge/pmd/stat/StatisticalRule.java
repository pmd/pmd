/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd.stat;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author David Dixon-Peugh
 * Aug 8, 2002 StatisticalRule.java
 */
public abstract class StatisticalRule extends AbstractRule {
    public static double DELTA = 0.000005; // Within this range. . .

    private SortedSet dataPoints = new TreeSet();

    private int count = 0;
    private double total = 0.0;
    private double totalSquared = 0.0;

    public void addDataPoint(DataPoint point) {
        count++;
        total += point.getScore();
        totalSquared += point.getScore() * point.getScore();
        dataPoints.add(point);
    }

    public void apply(List acus, RuleContext ctx) {
        visitAll(acus, ctx);

        double deviation;
        double minimum = 0.0;

        if (hasProperty("sigma")) {
            deviation = getStdDev();
            double sigma = getDoubleProperty("sigma");

            minimum = getMean() + (sigma * deviation);
        }

        if (hasProperty("minimum")) {
            double mMin = getDoubleProperty("minimum");
            if (mMin > minimum) {
                minimum = mMin;
            }
        }

        SortedSet newPoints = applyMinimumValue(dataPoints, minimum);

        if (hasProperty("topscore")) {
            int topScore = getIntProperty("topscore");
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
    	Iterator points = dataPoints.iterator();
    	double mean = getMean();
    	double deltaSq = 0.0;
    	
    	if (dataPoints.size() < 2) {
    		return Double.NaN;
    	}
    	
    	while (points.hasNext()) {
    		DataPoint point = (DataPoint) points.next();	
    		deltaSq += ((point.getScore() - mean) * (point.getScore() - mean));
    	}
    	
    	return Math.sqrt( deltaSq / (dataPoints.size() - 1));
    }

    protected SortedSet applyMinimumValue(SortedSet pointSet, double minValue) {
        Iterator points = pointSet.iterator();
        SortedSet RC = new TreeSet();

        while (points.hasNext()) {
            DataPoint point = (DataPoint) points.next();

            if (point.getScore() > (minValue - DELTA)) {
                RC.add(point);
            }
        }
        return RC;
    }

    protected SortedSet applyTopScore(SortedSet points, int topScore) {
        SortedSet RC = new TreeSet();
        for (int i = 0; i < topScore; i++) {
            DataPoint point = (DataPoint) points.last();
            points.remove(point);

            RC.add(point);
        }

        return RC;
    }

    protected void makeViolations(RuleContext ctx, Set dataPoints) {
        Iterator points = dataPoints.iterator();
        while (points.hasNext()) {
            DataPoint point = (DataPoint) points.next();
            ctx.getReport().addRuleViolation(this.createRuleViolation(ctx, point.getLineNumber(), point.getMessage()));
        }
    }
}
