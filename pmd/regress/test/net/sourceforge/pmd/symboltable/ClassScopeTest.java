/*
 * User: tom
 * Date: Oct 16, 2002
 * Time: 3:56:34 PM
 */
package test.net.sourceforge.pmd.symboltable;

import junit.framework.TestCase;
import net.sourceforge.pmd.symboltable.ClassScope;
import net.sourceforge.pmd.symboltable.NameDeclaration;
import net.sourceforge.pmd.symboltable.NameOccurrence;
import net.sourceforge.pmd.ast.ASTName;

public class ClassScopeTest extends TestCase {

/*
    public void testContains() {
        ClassScope s = new ClassScope("foo");
        s.addDeclaration(NameDeclarationTest.FOO);
        assertTrue(s.getUnusedDeclarations().hasNext());
    }

    public void testContainsQualified() {
        ClassScope s = new ClassScope("foo");
        s.addDeclaration(new NameDeclaration(NameDeclarationTest.createNode("x", 10)));
        s.addOccurrence(new NameOccurrence(NameDeclarationTest.createNode("x.length", 15)));
        assertTrue(!s.getUnusedDeclarations().hasNext());
    }

    public void testStaticFinal() {
        ClassScope s = new ClassScope("foo");
        s.addDeclaration(new NameDeclaration(NameDeclarationTest.createNode("x", 10)));
        s.addOccurrence(new NameOccurrence(NameDeclarationTest.createNode("foo.x", 15)));
        assertTrue(!s.getUnusedDeclarations().hasNext());
    }
*/

    public void test1() {}
}
