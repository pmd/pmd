package test.net.sourceforge.pmd.ast;

import junit.framework.TestCase;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTEqualityExpression;
import net.sourceforge.pmd.ast.ASTInstanceOfExpression;
import net.sourceforge.pmd.ast.ASTRelationalExpression;
import net.sourceforge.pmd.ast.DiscardableNodeCleaner;

public class DiscardableNodeCleanerTest extends TestCase {

    public void testRemoveDiscardNodes() {
        ASTCompilationUnit cu = new ASTCompilationUnit(1);
        ASTEqualityExpression ee = new ASTEqualityExpression(2);
        ee.jjtSetParent(cu);
        cu.jjtAddChild(ee, 0);

        ASTInstanceOfExpression io1 = new ASTInstanceOfExpression(3);
        io1.setDiscardable();
        io1.jjtSetParent(ee);
        ASTRelationalExpression re = new ASTRelationalExpression(4);
        re.jjtSetParent(ee);
        ee.jjtAddChild(io1, 0);
        io1.jjtAddChild(re, 0);
        assertEquals(cu.findChildrenOfType(ASTInstanceOfExpression.class).size(), 1);
        DiscardableNodeCleaner c = new DiscardableNodeCleaner();
        c.clean(cu);
        assertEquals(cu.findChildrenOfType(ASTInstanceOfExpression.class).size(), 0);
    }
}
