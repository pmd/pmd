package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;

import net.sourceforge.pmd.ast.*;

public class ShortMethodNameRule
    extends AbstractRule
{
    public ShortMethodNameRule() { }

    public Object visit(ASTMethodDeclarator decl, Object data) {
	RuleContext ctx = (RuleContext) data;
	String image = decl.getImage();

	if (image.length() <= 3) {
	    RuleViolation violation =
		createRuleViolation( ctx, decl.getBeginLine(),
				     "Avoid short method names like " +
				     decl.getImage() );
	    ctx.getReport().addRuleViolation( violation );
	}

	return null;
    }
}
