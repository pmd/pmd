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
        SymbolTable symbolTable = new SymbolTable();
        Scope scope = new LocalScope();
        scope.addDeclaration(NameDeclarationTest.FOO);
        symbolTable.add(scope);
        LookupController lc = new LookupController(symbolTable);
        lc.lookup(new NameOccurrence(NameDeclarationTest.FOO_NODE));
        assertTrue(!scope.getUnusedDeclarations().hasNext());
    }

    public void testLookupNotFound() {
        SymbolTable symbolTable = new SymbolTable();
        Scope scope = new LocalScope();
        scope.addDeclaration(NameDeclarationTest.FOO);
        symbolTable.add(scope);
        LookupController lc = new LookupController(symbolTable);
        lc.lookup(new NameOccurrence(NameDeclarationTest.createNode("bar", 10)));
        assertTrue(scope.getUnusedDeclarations().hasNext());
    }
}
