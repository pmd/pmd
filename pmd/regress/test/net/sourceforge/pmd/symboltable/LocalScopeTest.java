/*
 * User: tom
 * Date: Oct 1, 2002
 * Time: 3:24:28 PM
 */
package test.net.sourceforge.pmd.symboltable;

import junit.framework.TestCase;
import net.sourceforge.pmd.symboltable.LocalScope;
import net.sourceforge.pmd.symboltable.NameDeclaration;
import net.sourceforge.pmd.symboltable.Kind;
import net.sourceforge.pmd.symboltable.NameOccurrence;

public class LocalScopeTest extends TestCase {

    public void testAdd() {
        LocalScope scope = new LocalScope();
        scope.addDeclaration(new NameDeclaration(NameDeclarationTest.FOO_NODE, Kind.LOCAL_VARIABLE));
        assertTrue(scope.contains(new NameOccurrence(NameDeclarationTest.createNode("foo", 12))));
    }

    public void testUnused() {
        LocalScope scope = new LocalScope();
        scope.addDeclaration(new NameDeclaration(NameDeclarationTest.FOO_NODE, Kind.LOCAL_VARIABLE));
        assertTrue(scope.getUnusedDeclarations().hasNext());
    }

    public void testUnused2() {
        LocalScope scope = new LocalScope();
        scope.addDeclaration(new NameDeclaration(NameDeclarationTest.FOO_NODE, Kind.LOCAL_VARIABLE));
        scope.addOccurrence(new NameOccurrence(NameDeclarationTest.createNode("foo", 12)));
        assertTrue(!scope.getUnusedDeclarations().hasNext());
    }
}
