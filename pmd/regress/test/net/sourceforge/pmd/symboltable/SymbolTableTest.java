/*
 * User: tom
 * Date: Jun 19, 2002
 * Time: 11:09:06 AM
 */
package test.net.sourceforge.pmd.symboltable;

import junit.framework.TestCase;
import net.sourceforge.pmd.*;
import net.sourceforge.pmd.symboltable.SymbolTable;
import net.sourceforge.pmd.symboltable.NameDeclaration;
import net.sourceforge.pmd.symboltable.NameOccurrence;
import net.sourceforge.pmd.symboltable.Kind;
import net.sourceforge.pmd.ast.JavaParser;
import net.sourceforge.pmd.ast.ASTCompilationUnit;

import java.util.HashMap;
import java.io.InputStreamReader;
import java.io.Reader;

public class SymbolTableTest extends TestCase {

    private static final NameDeclaration FOO = new NameDeclaration(NameDeclarationTest.createNode("foo", 10), Kind.UNKNOWN);

    public void testAdd() {
        SymbolTable s = new SymbolTable();
        s.add(FOO);
        try {
            s.add(FOO);
        } catch (RuntimeException e) {
            return; // cool
        }
        throw new RuntimeException("Should have thrown RuntimeException");
    }

    public void testParentContains() {
        SymbolTable table = new SymbolTable();
        table.openScope();
        table.add(new NameDeclaration(NameDeclarationTest.createNode("bar", 12), Kind.UNKNOWN));
        table.add(new NameDeclaration(NameDeclarationTest.createNode("baz", 12), Kind.UNKNOWN));
        assertTrue(table.getUnusedNameDeclarations().hasNext());
        table.leaveScope();
        assertTrue(!table.getUnusedNameDeclarations().hasNext());
    }

    public void testRecordUsage() {
        SymbolTable s = new SymbolTable();
        s.add(FOO);
        assertTrue(s.getUnusedNameDeclarations().hasNext());
        s.recordOccurrence(new NameOccurrence(FOO.getImage(), FOO.getLine()));
        assertTrue(!s.getUnusedNameDeclarations().hasNext());
    }

    public void testRecordOccurrence() {
        SymbolTable table = new SymbolTable();
        table.openScope();
        table.recordOccurrence(new NameOccurrence("bar", 10));
        assertTrue(!table.getUnusedNameDeclarations().hasNext());
    }

    public void testRecordOccurrence2() {
        SymbolTable s = new SymbolTable();
        s.recordOccurrence(new NameOccurrence("bar", 10));
        assertTrue(!s.getUnusedNameDeclarations().hasNext());
    }

    public void testRecordUsageParent() {
        SymbolTable parent = new SymbolTable();
        parent.add(FOO);
        parent.openScope();
        parent.leaveScope();
        assertEquals(FOO, parent.getUnusedNameDeclarations().next());
    }

    public void testRecordUsageParent2() {
        SymbolTable parent = new SymbolTable();
        parent.add(FOO);
        parent.openScope();
        parent.recordOccurrence(new NameOccurrence(FOO.getImage(), FOO.getLine()));
        assertTrue(!parent.getUnusedNameDeclarations().hasNext());
    }
}
