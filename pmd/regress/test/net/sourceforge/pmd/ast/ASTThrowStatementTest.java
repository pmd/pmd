/*
 * Created on Jan 19, 2005 
 *
 * $Id$
 */
package test.net.sourceforge.pmd.ast;

import net.sourceforge.pmd.ast.ASTAllocationExpression;
import net.sourceforge.pmd.ast.ASTExpression;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTThrowStatement;
import junit.framework.TestCase;

/**
 * 
 * @author mgriffa
 */
public class ASTThrowStatementTest extends TestCase {

    public final void testGetFirstASTNameImageNull() {
        ASTThrowStatement ts = new ASTThrowStatement(0);
        
        assertNull(ts.getFirstASTNameImage());
    }

    public final void testGetFirstASTNameImageNew() {
        ASTThrowStatement ts = new ASTThrowStatement(0);
        ASTAllocationExpression ao = new ASTAllocationExpression(1);
        ts.jjtAddChild(ao, 0);
        ASTExpression e = new ASTExpression(2);
        ao.jjtAddChild(e, 0);
        ASTName n = new ASTName(3);
        n.setImage("MyName");
        ao.jjtAddChild(n, 0);
        
        assertEquals("MyName", ts.getFirstASTNameImage());
    }

}
