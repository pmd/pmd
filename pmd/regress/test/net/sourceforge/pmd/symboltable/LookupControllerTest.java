/*
 * User: tom
 * Date: Oct 2, 2002
 * Time: 2:16:41 PM
 */
package test.net.sourceforge.pmd.symboltable;

import junit.framework.TestCase;
import net.sourceforge.pmd.symboltable.*;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.ast.SimpleNode;

import java.util.ArrayList;
import java.util.List;

public class LookupControllerTest extends TestCase {

    public void testLookupFound() {
        Scope scope = new LocalScope();
        scope.addDeclaration(NameDeclarationTest.FOO);
        LookupController lc = new LookupController();
        lc.lookup(new NameOccurrence(NameDeclarationTest.FOO_NODE), scope);
        assertTrue(!scope.getUnusedDeclarations().hasNext());
    }

    public void testLookupNotFound() {
        Scope scope = new LocalScope();
        scope.addDeclaration(NameDeclarationTest.FOO);
        LookupController lc = new LookupController();
        lc.lookup(new NameOccurrence(NameDeclarationTest.createNode("bar", 10)), scope);
        assertTrue(scope.getUnusedDeclarations().hasNext());
    }
}
