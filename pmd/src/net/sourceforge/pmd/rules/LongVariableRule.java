package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;

import net.sourceforge.pmd.ast.*;

public class LongVariableRule 
    extends AbstractRule
{
    public LongVariableRule() { }

    public static final int LONG_VARIABLE_LIMIT = 12;

    public Object visit(ASTVariableDeclaratorId decl, Object data) {
	RuleContext ctx = (RuleContext) data;
	String image = decl.getImage();

	if (image.length() > LONG_VARIABLE_LIMIT) {
	    ctx.getReport().addRuleViolation( createRuleViolation( ctx, decl.getBeginLine() ));
	}
	
	return null;
    }
}
