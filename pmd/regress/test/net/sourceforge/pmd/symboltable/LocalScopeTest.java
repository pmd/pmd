/*
 * User: tom
 * Date: Oct 1, 2002
 * Time: 3:24:28 PM
 */
package test.net.sourceforge.pmd.symboltable;

import junit.framework.TestCase;
import net.sourceforge.pmd.symboltable.*;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTPrimaryPrefix;

public class LocalScopeTest extends TestCase {

/*
    public void testAdd() {
        LocalScope scope = new LocalScope();
        scope.addDeclaration(new NameDeclaration(NameDeclarationTest.FOO_NODE));
        assertTrue(scope.contains(new NameOccurrence(NameDeclarationTest.createNode("foo", 12))));
    }

    public void testUnused() {
        LocalScope scope = new LocalScope();
        scope.addDeclaration(new NameDeclaration(NameDeclarationTest.FOO_NODE));
        assertTrue(scope.getUnusedDeclarations().hasNext());
    }

    public void testNameWithThisOrSuperIsNotFlaggedAsUnused() {
        LocalScope scope = new LocalScope();
        ASTName name = NameDeclarationTest.createNode("foo", 12);
        ASTPrimaryPrefix prefix = new ASTPrimaryPrefix(2);
        prefix.setUsesThisModifier();
        name.jjtAddChild(prefix, 1);
        NameOccurrence occ = new NameOccurrence(name);
        scope.addOccurrence(occ);
        assertTrue(!scope.getUnusedDeclarations().hasNext());
    }

    public void testNameWithSuperIsNotFlaggedAsUnused() {
        LocalScope scope = new LocalScope();
        ASTName name = NameDeclarationTest.createNode("foo", 12);
        ASTPrimaryPrefix prefix = new ASTPrimaryPrefix(2);
        prefix.setUsesSuperModifier();
        name.jjtAddChild(prefix, 1);
        NameOccurrence occ = new NameOccurrence(name);
        scope.addOccurrence(occ);
        assertTrue(!scope.getUnusedDeclarations().hasNext());
    }

    public void testUnused2() {
        LocalScope scope = new LocalScope();
        scope.addDeclaration(new NameDeclaration(NameDeclarationTest.FOO_NODE));
        scope.addOccurrence(new NameOccurrence(NameDeclarationTest.createNode("foo", 12)));
        assertTrue(!scope.getUnusedDeclarations().hasNext());
    }

    public void testParent() {
        Scope scope = new LocalScope();
        Scope parent = new LocalScope();
        scope.setParent(parent);
        assertEquals(parent, scope.getParent());
    }
*/

    public void test1(){}
}
