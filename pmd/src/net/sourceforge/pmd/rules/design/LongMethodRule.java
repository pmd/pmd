package net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;

import net.sourceforge.pmd.ast.*;

/**
 * This rule detects when a method exceeds a certain
 * threshold.  i.e. if a method has more than x lines
 * of code.
 */
public class LongMethodRule
    extends AbstractRule
{
    public LongMethodRule() { }

    public Object visit( ASTMethodDeclaration decl, Object data ) {
	RuleContext ctx = (RuleContext) data;

	if ((decl.getEndLine() - decl.getBeginLine()) > getIntProperty("minimumLength")) {
	    ctx.getReport()
		.addRuleViolation( createRuleViolation( ctx,
							decl.getBeginLine(),
							getMessage() ));
	}
	return null;
    }
}
