/*
 * User: tom
 * Date: Jun 19, 2002
 * Time: 11:59:24 AM
 */
package test.net.sourceforge.pmd.symboltable;

import junit.framework.TestCase;
import net.sourceforge.pmd.symboltable.NameDeclaration;
import net.sourceforge.pmd.symboltable.Kind;
import net.sourceforge.pmd.ast.SimpleNode;

public class NameDeclarationTest extends TestCase {

    public void testBasic() {
        SimpleNode node = createNode("foo", 10);
        NameDeclaration decl = new NameDeclaration(node, Kind.LOCAL_VARIABLE);
        assertEquals(10, decl.getLine());
        assertEquals("foo", decl.getImage());
        assertEquals(decl, new NameDeclaration(node, Kind.LOCAL_VARIABLE));
    }

    public void testConstructor() {
        SimpleNode node = createNode("foo", 10);
        NameDeclaration decl = new NameDeclaration(node, Kind.LOCAL_VARIABLE);
        assertEquals(node.getBeginLine(), decl.getLine());
        assertEquals(node.getImage(), decl.getImage());
    }

    public static SimpleNode createNode(String image, int line) {
        SimpleNode node = new SimpleNode(1);
        node.setImage(image);
        node.testingOnly__setBeginLine(line);
        return node;
    }
}
