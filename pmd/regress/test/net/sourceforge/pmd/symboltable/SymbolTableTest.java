/*
 * User: tom
 * Date: Jun 19, 2002
 * Time: 11:09:06 AM
 */
package test.net.sourceforge.pmd.symboltable;

import junit.framework.TestCase;
import net.sourceforge.pmd.*;
import net.sourceforge.pmd.symboltable.*;
import net.sourceforge.pmd.ast.JavaParser;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.SimpleNode;

import java.util.HashMap;
import java.util.TreeMap;
import java.io.InputStreamReader;
import java.io.Reader;

public class SymbolTableTest extends TestCase {

    public void testPush() {
        SymbolTable s = new SymbolTable();
        s.push(new LocalScope());
        assertEquals(2,s.depth());
    }

    public void testPop() {
        SymbolTable s = new SymbolTable();
        s.push(new LocalScope());
        s.pop();
        assertEquals(1,s.depth());
    }

    public void testPeek() {
        SymbolTable s = new SymbolTable();
        Scope scope = new LocalScope();
        s.push(scope);
        assertEquals(scope, s.peek());
    }

    public void testParentLinkage() {
        SymbolTable s = new SymbolTable();
        Scope scope = new LocalScope();
        s.push(scope);
        Scope scope2 = new LocalScope();
        s.push(scope2);
        Scope scope3 = new LocalScope();
        s.push(scope3);
        assertEquals(scope2.getParent(), scope);
        assertEquals(scope3.getParent(), scope2);
        s.pop();
        assertEquals(scope2.getParent(), scope);
        assertEquals(scope3.getParent(), scope2);
    }

}
