package test.net.sourceforge.pmd.ast;

import net.sourceforge.pmd.ast.ASTAllocationExpression;
import net.sourceforge.pmd.ast.ASTAssertStatement;
import net.sourceforge.pmd.ast.ASTBlockStatement;
import test.net.sourceforge.pmd.testframework.ParserTst;

public class ASTBlockStatementTest extends ParserTst {


    public void testIsAllocation() {
        ASTBlockStatement bs = new ASTBlockStatement(0);
        bs.jjtAddChild(new ASTAllocationExpression(1), 0);
        assertTrue(bs.isAllocation());
    }

    public void testIsAllocation2() {
        ASTBlockStatement bs = new ASTBlockStatement(0);
        bs.jjtAddChild(new ASTAssertStatement(1), 0);
        assertFalse(bs.isAllocation());
    }

}
