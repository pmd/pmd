/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.symboltable;

import junit.framework.TestCase;
import net.sourceforge.pmd.ast.ASTFormalParameter;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTTryStatement;
import net.sourceforge.pmd.ast.ASTType;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.symboltable.LocalScope;
import net.sourceforge.pmd.symboltable.VariableNameDeclaration;

public class VariableNameDeclarationTest extends TestCase {

    public void testConstructor() {
        ASTVariableDeclaratorId exp = createNode("foo", 10);
        LocalScope scope = new LocalScope();
        exp.setScope(scope);
        VariableNameDeclaration decl = new VariableNameDeclaration(exp);
        assertEquals("foo", decl.getImage());
        assertEquals(10, decl.getLine());
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

    public void testMethodParam() {
        ASTVariableDeclaratorId id = new ASTVariableDeclaratorId(3);
        id.testingOnly__setBeginLine(10);
        id.setImage("foo");

        ASTFormalParameter param = new ASTFormalParameter(2);
        id.jjtSetParent(param);

        ASTType type = new ASTType(4);
        param.jjtAddChild(type, 0);

        ASTName name = new ASTName(5);
        type.jjtAddChild(name, 0);

        assertEquals(name, id.getTypeNameNode());
    }

    private static ASTVariableDeclaratorId createNode(String image, int line) {
        ASTVariableDeclaratorId node = new ASTVariableDeclaratorId(1);
        node.setImage(image);
        node.testingOnly__setBeginLine(line);
        return node;
    }
}
