package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTForInit;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.ast.Node;

import java.text.MessageFormat;

public class ShortVariableRule 
    extends AbstractRule
{
    public Object visit(ASTVariableDeclaratorId decl, Object data) {
	RuleContext ctx = (RuleContext) data;
	String image = decl.getImage();

	if ((image.length() <= getIntProperty("minimumLength")) && (!(isForInit( decl )))) {
        ctx.getReport().addRuleViolation(createRuleViolation(ctx, decl.getBeginLine(), MessageFormat.format(getMessage(), new Object[] {image})));
	}

	return data;
    }

    protected boolean isForInit( Node node ) {
	Node parent = node;
	for (int i = 0; i < 4; i++) {
	    parent = parent.jjtGetParent();
	    if (parent instanceof ASTForInit) {
		return true;
	    }
	}
	return false;
    }
}
