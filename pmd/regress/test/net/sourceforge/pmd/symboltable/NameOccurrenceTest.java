/*
 * User: tom
 * Date: Oct 2, 2002
 * Time: 9:15:27 AM
 */
package test.net.sourceforge.pmd.symboltable;

import junit.framework.TestCase;
import net.sourceforge.pmd.symboltable.NameOccurrence;
import net.sourceforge.pmd.symboltable.Qualifier;
import net.sourceforge.pmd.symboltable.NameDeclaration;
import net.sourceforge.pmd.symboltable.LocalScope;

import java.util.List;

public class NameOccurrenceTest extends TestCase {

    public void testConstructor() {
        NameOccurrence occ = new NameOccurrence(NameDeclarationTest.FOO_NODE);
        assertEquals("foo", occ.getObjectName());
        occ = new NameOccurrence(NameDeclarationTest.createNode("foo.bar",10));
        assertEquals("foo", occ.getObjectName());
    }

    public void testQualifiedWithDot() {
        NameOccurrence occ = new NameOccurrence(NameDeclarationTest.createNode("x.length",5));
        assertTrue(occ.isQualified());
    }

    public void testQualifiedWithThisOrSuper() {
        NameOccurrence occ = new NameOccurrence(NameDeclarationTest.FOO_NODE);
        occ.setQualifier(Qualifier.SUPER);
        assertTrue(occ.usesThisOrSuper());
    }

    public void testObjectName() {
        NameOccurrence occ = new NameOccurrence(NameDeclarationTest.createNode("x.length",5));
        assertEquals("x", occ.getObjectName());
    }

    public void testQualifiersBrokenDown() {
        NameOccurrence occ = new NameOccurrence(NameDeclarationTest.createNode("x.length",5));
        List names = occ.getQualifiers();
        assertEquals(2, names.size());
        assertEquals("x", names.get(0));

        occ = new NameOccurrence(NameDeclarationTest.createNode("MyClass.this.x.length",5));
        names = occ.getQualifiers();
        assertEquals(4, names.size());
    }

    public void testScope() {
        NameDeclaration decl = NameDeclarationTest.FOO;
        LocalScope lclScope = new LocalScope();
        decl.getNode().setScope(lclScope);
        NameOccurrence occ = new NameOccurrence(decl.getNode());
        assertEquals(lclScope, occ.getScope());
    }

}
