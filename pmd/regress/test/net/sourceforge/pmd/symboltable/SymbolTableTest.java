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

    public void testAdd() {
        SymbolTable s = new SymbolTable();
        s.add(new LocalScope());
        assertEquals(2,s.depth());
    }

    public void testRemove() {
        SymbolTable s = new SymbolTable();
        s.add(new LocalScope());
        s.removeTail();
        assertEquals(1,s.depth());
    }

    public void testGet() {
        SymbolTable s = new SymbolTable();
        Scope scope = new LocalScope();
        s.add(scope);
        assertEquals(scope, s.getTail());
    }

}
