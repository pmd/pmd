/*
 * User: tom
 * Date: Jun 19, 2002
 * Time: 11:59:24 AM
 */
package test.net.sourceforge.pmd.symboltable;

import junit.framework.TestCase;
import net.sourceforge.pmd.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.symboltable.LocalScope;
import net.sourceforge.pmd.ast.*;

public class VariableNameDeclarationTest extends TestCase {

    public void testConstructor() {
        ASTVariableDeclaratorId exp = createNode("foo", 10);
        LocalScope scope = new LocalScope();
        exp.setScope(scope);
        VariableNameDeclaration decl = new VariableNameDeclaration(exp);
        assertEquals("foo", decl.getImage());
        assertEquals(10, decl.getLine());
        assertEquals(scope, decl.getScope());
    }

    public void testExceptionBlkParam() {
        ASTVariableDeclaratorId id = new ASTVariableDeclaratorId(3);
        id.testingOnly__setBeginLine(10);
        id.setImage("foo");

        ASTFormalParameter param = new ASTFormalParameter(2);
        id.jjtSetParent(param);

        ASTTryStatement tryStmt = new ASTTryStatement(1);
        param.jjtSetParent(tryStmt);

        VariableNameDeclaration decl = new VariableNameDeclaration(id);
        assertTrue(decl.isExceptionBlockParameter());
    }

    private static ASTVariableDeclaratorId createNode(String image, int line) {
        ASTVariableDeclaratorId node = new ASTVariableDeclaratorId(1);
        node.setImage(image);
        node.testingOnly__setBeginLine(line);
        return node;
    }
}
