package net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;

import net.sourceforge.pmd.ast.*;

/**
 * This rule detects when a class exceeds a certain
 * threshold.  i.e. if a class has more than 1000 lines
 * of code.
 */
public class LongClassRule
    extends AbstractRule
{
    public LongClassRule() { }

    public Object visit( ASTClassDeclaration decl, Object data ) {
		RuleContext ctx = (RuleContext) data;

		if ((decl.getEndLine() - decl.getBeginLine()) > getIntProperty("minimumLength")) {
		    ctx.getReport().addRuleViolation( createRuleViolation( ctx,
								decl.getBeginLine(),
								getMessage() ));
		}
		return null;
    }
}
