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
 * 					Aug 8, 2002 StatisticalRule.java
 */
public abstract class StatisticalRule extends AbstractRule {
	private SortedSet dataPoints =
		new TreeSet();
		
	public void addDataPoint( DataPoint point )
	{
	}
	
	public void apply( List acus, RuleContext ctx ) {
        visitAll( acus, ctx );

		if (hasProperty("minimum")) {
			applyMinimumValue( ctx, getDoubleProperty("minimumValue") );	
		}        
		
		if (hasProperty("sigma")) {
			applySigma( ctx, getDoubleProperty("sigma"));
		}
		
		if (hasProperty("topscore")) {
			applyTopScore(ctx, getIntProperty("topscore"));
		}
    }
    
    protected void applyMinimumValue( RuleContext ctx,
    									double minValue ) 
    {
		Iterator points = dataPoints.iterator();
		
		while (points.hasNext()) {
			DataPoint point = (DataPoint) points.next();
			
			if (point.getScore() > minValue) {
				ctx.getReport().addRuleViolation(
					this.createRuleViolation(
							ctx, 
							point.getLineNumber(),
							point.getMessage()));	
			}
		}    										
    }
    
    protected void applySigma( RuleContext ctx,
                                 double sigma ) 
    {
    	Iterator points = dataPoints.iterator();
    	
    	int count = 0;
    	double total = 0.0;
    	double mean = 0.0;
    	
    	while (points.hasNext()) {
    		count++;
    		DataPoint point = (DataPoint) points.next();		
    		total += point.getScore();	
    	}                             	
    	
    	mean = total / count;
		// TODO:  Calculate StdDev
		// TODO:  Identify things outside range.    	
    }
    
    protected void applyTopScore( RuleContext ctx,
                                   int topScore ) 
    {
    	// TODO:  Identify the top scorers                               	
    }
}
