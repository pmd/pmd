package test.net.sourceforge.pmd.ast;

import net.sourceforge.pmd.ast.ASTClassDeclaration;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTEqualityExpression;
import net.sourceforge.pmd.ast.ASTInstanceOfExpression;
import net.sourceforge.pmd.ast.ASTModifiers;
import net.sourceforge.pmd.ast.ASTRelationalExpression;
import net.sourceforge.pmd.ast.ASTTypeDeclaration;
import net.sourceforge.pmd.ast.DiscardableNodeCleaner;
import test.net.sourceforge.pmd.testframework.ParserTst;

public class DiscardableNodeCleanerTest extends ParserTst {

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

    public void testRemoveModifierNodes() throws Throwable {
        ASTCompilationUnit cu = new ASTCompilationUnit(1);
        ASTTypeDeclaration td = new ASTTypeDeclaration(2);
        td.jjtSetParent(cu);
        cu.jjtAddChild(td, 0);

        ASTModifiers m = new ASTModifiers(3);
        m.jjtSetParent(td);
        td.jjtAddChild(m, 0);
        ASTClassDeclaration cd = new ASTClassDeclaration(4);
        cd.jjtSetParent(td);
        td.jjtAddChild(cd, 1);

        assertEquals(cu.findChildrenOfType(ASTModifiers.class).size(), 1);
        DiscardableNodeCleaner c = new DiscardableNodeCleaner();
        c.clean(cu);
        assertTrue(cu.findChildrenOfType(ASTModifiers.class).isEmpty());
    }
}
