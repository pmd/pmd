package net.sourceforge.pmd.dfa;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.ast.SimpleNode;

/**
 * The RuleViolation is extended by the VariableName. The VariableName 
 * is required for showing what variable produces the UR DD or DU anomaly.
 * The superclass RuleViolation only returns an empty string.
 *  
 * @author Sven Jacob
 *
 */
public class DaaRuleViolation extends RuleViolation {
	private String variableName;

	public DaaRuleViolation(Rule rule, RuleContext ctx, SimpleNode node, String specificMsg, String variableName) {
		super(rule, ctx, node, specificMsg);
		this.variableName = variableName; 
	}
	
	//@Override
	public String getVariableName() {
		return variableName;
	}
}
