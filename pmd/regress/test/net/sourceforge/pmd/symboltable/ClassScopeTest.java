/*
 * User: tom
 * Date: Oct 16, 2002
 * Time: 3:56:34 PM
 */
package test.net.sourceforge.pmd.symboltable;

import junit.framework.TestCase;
import net.sourceforge.pmd.symboltable.ClassScope;
import net.sourceforge.pmd.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.symboltable.NameOccurrence;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;

public class ClassScopeTest extends TestCase {

    public void testContains() {
        ClassScope s = new ClassScope("Foo");
        ASTVariableDeclaratorId node = new ASTVariableDeclaratorId(1);
        node.setImage("bar");
        s.addDeclaration(new VariableNameDeclaration(node));
        assertTrue(s.getVariableDeclarations(false).keySet().iterator().hasNext());
    }

    public void testCantContainsSuperToString() {
        ClassScope s = new ClassScope("foo");
        SimpleNode node = new SimpleNode(1);
        node.setImage("super.toString");
        assertTrue(!s.contains(new NameOccurrence(node, node.getImage())));
    }

    public void testContainsStaticVariablePrefixedWithClassName() {
        ClassScope s = new ClassScope("Foo");
        ASTVariableDeclaratorId node = new ASTVariableDeclaratorId(1);
        node.setImage("X");
        s.addDeclaration(new VariableNameDeclaration(node));

        SimpleNode node2 = new SimpleNode(2);
        node2.setImage("Foo.X");
        assertTrue(s.contains(new NameOccurrence(node2, node2.getImage())));
    }
}
