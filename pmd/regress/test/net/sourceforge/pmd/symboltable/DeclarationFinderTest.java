/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.symboltable;

import junit.framework.TestCase;
import net.sourceforge.pmd.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.symboltable.DeclarationFinder;
import net.sourceforge.pmd.symboltable.LocalScope;
import net.sourceforge.pmd.symboltable.NameOccurrence;

public class DeclarationFinderTest extends TestCase {

    public void testDeclarationsAreFound() {
        DeclarationFinder df = new DeclarationFinder();

        ASTVariableDeclaratorId node = new ASTVariableDeclaratorId(1);
        node.setImage("foo");

        ASTVariableDeclarator parent = new ASTVariableDeclarator(2);
        node.jjtSetParent(parent);

        ASTLocalVariableDeclaration gparent = new ASTLocalVariableDeclaration(3);
        parent.jjtSetParent(gparent);

        LocalScope scope = new LocalScope();
        node.setScope(scope);
        df.visit(node, null);

        assertTrue(scope.contains(new NameOccurrence(new SimpleNode(4), "foo")));
    }

    public void test1() {
    }
}
