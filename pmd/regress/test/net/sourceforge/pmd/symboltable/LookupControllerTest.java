/*
 * User: tom
 * Date: Oct 2, 2002
 * Time: 2:16:41 PM
 */
package test.net.sourceforge.pmd.symboltable;

import junit.framework.TestCase;
import net.sourceforge.pmd.symboltable.*;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.ASTName;

public class LookupControllerTest extends TestCase {

/*
    public void testLookupFound() {
        Scope scope = new LocalScope();
        scope.addDeclaration(NameDeclarationTest.FOO);
        LookupController lc = new LookupController();
        ASTName declNode = NameDeclarationTest.createNode("foo", 5);
        declNode.setScope(scope);
        NameOccurrence occ = new NameOccurrence(declNode);
        lc.lookup(occ);
        assertTrue(!scope.getUnusedDeclarations().hasNext());
    }

    public void testLookupNotFound() {
        Scope scope = new LocalScope();
        scope.addDeclaration(NameDeclarationTest.FOO);
        LookupController lc = new LookupController();
        ASTName declNode = NameDeclarationTest.createNode("bar", 5);
        declNode.setScope(scope);
        NameOccurrence occ = new NameOccurrence(declNode);
        lc.lookup(occ);
        assertTrue(scope.getUnusedDeclarations().hasNext());
    }
*/

    public void test1() {}
}
