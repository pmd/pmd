package net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.stat.*;
import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;

import net.sourceforge.pmd.ast.*;

/**
 * This rule detects when a method exceeds a certain
 * threshold.  i.e. if a method has more than x lines
 * of code.
 */
public class LongMethodRule
    extends StatisticalRule
{
    public LongMethodRule() { }

    public Object visit( ASTMethodDeclaration decl, Object data ) {
	RuleContext ctx = (RuleContext) data;

	DataPoint point = new DataPoint();
	point.setLineNumber( decl.getBeginLine() );
	point.setScore( 1.0 * (decl.getEndLine() - decl.getBeginLine()));
	point.setRule( this );
	point.setMessage( getMessage() );

	addDataPoint( point );

	decl.childrenAccept( this, data ); 

	return null;
    }
}
