package net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;

import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;

public class UseSingletonRule
    extends AbstractRule
{
    public boolean isOK = false;

    public UseSingletonRule() { }

    public String getDescription() { 
	return "All methods are static.  Consider using Singleton instead.";
    }

    public Object visit( ASTMethodDeclaration decl, Object data ) {
	if (isOK) return data;
	
	if (!decl.isStatic()) {
	    isOK = true;
	    return data;
	}
	return data;
    }

    public Object visit( ASTCompilationUnit cu, Object data ) {
	Object RC = super.visit( cu, data );

	if (!isOK) 
	    (((RuleContext) data).getReport()).
		addRuleViolation( new RuleViolation( this, cu.getBeginLine() ));
	return data;
    }
}
