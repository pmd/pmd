package net.sourceforge.pmd.dfa;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.ast.SimpleNode;

/**
 * The RuleViolation is extended by the VariableName. The VariableName 
 * is required for showing what variable produces the UR DD or DU anomaly.
 *  
 * @author Sven Jacob
 *
 */
public class DaaRuleViolation extends RuleViolation {
    private String variableName;
    private int beginLine;
    private int endLine;
    private String type;
    
    public DaaRuleViolation(Rule rule, RuleContext ctx, SimpleNode node, String type, String msg, String var, int beginLine, int endLine) {
        super(rule, ctx, node, msg);
        this.variableName = var;
        this.beginLine = beginLine;
        this.endLine = endLine;
        this.type = type;
    }
	
    public String getVariableName() {
        return variableName;
    }
	
    public int getBeginLine() {
        return beginLine;
    }
	
    public int getEndLine() {
        return endLine;
    }
    
    public String getType() {
        return type;
    }
}
