package net.sourceforge.pmd.stat;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;

/**
 * @author David Dixon-Peugh
 * Aug 8, 2002 StatisticalRule.java
 */
public abstract class StatisticalRule extends AbstractRule {
    private SortedSet dataPoints =
	new TreeSet();

    private int count = 0;
    private double total = 0.0;

    public void addDataPoint( DataPoint point )
    {
	count++;
	total += point.getScore();
	dataPoints.add( point );
    }
    
    public void apply( List acus, RuleContext ctx ) {
	visitAll( acus, ctx );
	
	if (hasProperty("minimum")) {
	    applyMinimumValue( ctx, getDoubleProperty("minimum") );
	} 
	
	if (hasProperty("sigma")) {
	    applySigma( ctx, getDoubleProperty("sigma"));
	}
	
	if (hasProperty("topscore")) {
	    applyTopScore(ctx, getIntProperty("topscore"));
	}
    }

    protected double getMean() {
	return total / count;
    }

    protected void applyMinimumValue( RuleContext ctx,
				      double minValue ) 
    {
	Iterator points = dataPoints.iterator();
	
	while (points.hasNext()) {
	    DataPoint point = (DataPoint) points.next();
	    
	    if (point.getScore() > minValue) {
		ctx.getReport()
		   .addRuleViolation(this.createRuleViolation(ctx, 
						      point.getLineNumber(),
						      point.getMessage()));
	    }
	}    										
    }
    
    protected void applySigma( RuleContext ctx,
			       double sigma ) 
    {
    	Iterator points = dataPoints.iterator();
    	double varTotal = 0.0;
    	while (points.hasNext()) {
	    DataPoint point = (DataPoint) points.next();
	    varTotal += ((point.getScore() - getMean()) *
			 (point.getScore() - getMean()));	
    	}                             	
    	double variance = varTotal / count;
	double stdDev =
	    Math.sqrt( variance );
	applyMinimumValue( ctx, getMean() + (sigma * stdDev)); 
    }
    
    protected void applyTopScore( RuleContext ctx,
				  int topScore ) 
    {
	SortedSet pointsCopy =
	    new TreeSet( dataPoints );

	for (int i = 0; i < topScore; i++) {
	    DataPoint point = (DataPoint) pointsCopy.last();
	    pointsCopy.remove( point );

	    ctx.getReport()
		.addRuleViolation(this.createRuleViolation(ctx, 
						   point.getLineNumber(),
						   point.getMessage()));
	}
    }
}
