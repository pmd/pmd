/*
 * User: tom
 * Date: Jun 17, 2002
 * Time: 5:44:22 PM
 */
package net.sourceforge.pmd;

import net.sourceforge.pmd.ast.JavaParserVisitorAdapter;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.SimpleNode;

import java.util.List;
import java.util.Iterator;

public abstract class AbstractRule 
    extends JavaParserVisitorAdapter
    implements Rule
{

    public String getName() {
        return getClass().getName();
    }

    public boolean equals(Object o) {
        Rule r = (Rule)o;
        return r.getName().equals(getName());
    }

    public int hashCode() {
        return getName().hashCode();
    }

    protected void visitAll( List acus, RuleContext ctx ) {
        for (Iterator i = acus.iterator(); i.hasNext();) {
           ASTCompilationUnit node = (ASTCompilationUnit)i.next();
           visit( node, ctx );
        }
    }

    public void apply( List acus, RuleContext ctx ) {
        visitAll( acus, ctx );
    }

    public RuleViolation createRuleViolation(RuleContext ctx, int lineNumber) {
        return new RuleViolation(this, lineNumber, ctx.getFilename());
    }

    public RuleViolation createRuleViolation(RuleContext ctx, int lineNumber, String specificDescription) {
        return new RuleViolation(this, lineNumber, specificDescription, ctx.getFilename());
    }
}
