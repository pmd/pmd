/*
 * User: tom
 * Date: Jun 19, 2002
 * Time: 11:59:24 AM
 */
package test.net.sourceforge.pmd.symboltable;

import junit.framework.TestCase;
import net.sourceforge.pmd.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.ast.*;

public class VariableNameDeclarationTest extends TestCase {

    public void testConstructor() {
        ASTPrimaryExpression exp = createNode("foo", 10);
        VariableNameDeclaration decl = new VariableNameDeclaration(exp);
        assertEquals("foo", decl.getImage());
        assertEquals(10, decl.getLine());
        assertEquals(exp, decl.getNode());
        assertTrue(!decl.isExceptionBlockParameter());
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

    private static ASTPrimaryExpression createNode(String image, int line) {
        ASTPrimaryExpression node = new ASTPrimaryExpression(1);
        node.setImage(image);
        node.testingOnly__setBeginLine(line);
        return node;
    }
/*
    public static final ASTPrimaryExpression FOO_NODE = VariableNameDeclarationTest.createNode("foo", 10);
    public static final VariableNameDeclaration FOO = new VariableNameDeclaration(FOO_NODE);

    public static ASTPrimaryExpression createNode(String image, int line) {
        ASTPrimaryExpression node = new ASTPrimaryExpression(1);
        node.setImage(image);
        node.testingOnly__setBeginLine(line);
        return node;
    }

    public void testBasic() {
        SimpleNode node = FOO_NODE;
        VariableNameDeclaration decl = new VariableNameDeclaration(node);
        assertEquals(10, decl.getLine());
        assertEquals("foo", decl.getImage());
        assertEquals(decl, new VariableNameDeclaration(node));
        assertTrue(!decl.isExceptionBlockParameter());
    }

    public void testConstructor() {
        SimpleNode node = FOO_NODE;
        VariableNameDeclaration decl = new VariableNameDeclaration(node);
        assertEquals(node.getBeginLine(), decl.getLine());
        assertEquals(node.getImage(), decl.getImage());
    }

*/
}
