/*
 * User: tom
 * Date: Jun 19, 2002
 * Time: 11:09:06 AM
 */
package test.net.sourceforge.pmd.symboltable;

import junit.framework.TestCase;
import net.sourceforge.pmd.*;
import net.sourceforge.pmd.symboltable.*;

public class SymbolTableTest extends TestCase {

    public void testPush() {
        SymbolTable s = new SymbolTable();
        s.push(new GlobalScope());
        assertEquals(1,s.depth());
    }

    public void testPop() {
        SymbolTable s = new SymbolTable();
        s.push(new GlobalScope());
        s.pop();
        assertEquals(0,s.depth());
    }

    public void testPeek() {
        SymbolTable s = new SymbolTable();
        Scope scope = new GlobalScope();
        s.push(scope);
        assertEquals(scope, s.peek());
    }

    public void testParentLinkage() {
        SymbolTable s = new SymbolTable();
        Scope scope = new GlobalScope();
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
