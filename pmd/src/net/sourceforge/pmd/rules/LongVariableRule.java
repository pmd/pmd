package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;

import net.sourceforge.pmd.ast.*;

import java.text.MessageFormat;

public class LongVariableRule 
    extends AbstractRule
{
    public static final int LONG_VARIABLE_LIMIT = 12;

    public Object visit(ASTVariableDeclaratorId decl, Object data) {
	RuleContext ctx = (RuleContext) data;
	String image = decl.getImage();

	if (image.length() > LONG_VARIABLE_LIMIT) {
        String msg = MessageFormat.format(getMessage(), new Object[] {image});
        ctx.getReport().addRuleViolation(createRuleViolation(ctx, decl.getBeginLine(), msg));
	}
	
	return null;
    }
}
