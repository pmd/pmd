/*
 * User: tom
 * Date: Oct 1, 2002
 * Time: 3:24:28 PM
 */
package test.net.sourceforge.pmd.symboltable;

import junit.framework.TestCase;
import net.sourceforge.pmd.symboltable.Scope;
import net.sourceforge.pmd.symboltable.NameDeclaration;
import net.sourceforge.pmd.symboltable.Kind;
import net.sourceforge.pmd.symboltable.NameOccurrence;

public class ScopeTest extends TestCase {

    public void testAdd() {
        Scope scope = new Scope();
        scope.addDeclaration(new NameDeclaration(NameDeclarationTest.FOO_NODE, Kind.LOCAL_VARIABLE));
        assertTrue(scope.contains(new NameOccurrence(NameDeclarationTest.createNode("foo", 12))));
    }

    public void testUnused() {
        Scope scope = new Scope();
        scope.addDeclaration(new NameDeclaration(NameDeclarationTest.FOO_NODE, Kind.LOCAL_VARIABLE));
        assertTrue(scope.getUnusedDeclarations().hasNext());
    }

    public void testUnused2() {
        Scope scope = new Scope();
        scope.addDeclaration(new NameDeclaration(NameDeclarationTest.FOO_NODE, Kind.LOCAL_VARIABLE));
        scope.addOccurrence(new NameOccurrence(NameDeclarationTest.createNode("foo", 12)));
        assertTrue(!scope.getUnusedDeclarations().hasNext());
    }

    public void testUnused3() {
        Scope scope = new Scope();
        scope.addDeclaration(new NameDeclaration(NameDeclarationTest.FOO_NODE, Kind.LOCAL_VARIABLE));
        scope.addOccurrence(new NameOccurrence(NameDeclarationTest.createNode("foo.toString()", 12)));
        assertTrue(!scope.getUnusedDeclarations().hasNext());
    }
}
