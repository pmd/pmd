/*
 * User: tom
 * Date: Oct 10, 2002
 * Time: 8:27:50 AM
 */
package test.net.sourceforge.pmd.symboltable;

import junit.framework.TestCase;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTIfStatement;
import net.sourceforge.pmd.ast.ASTTryStatement;
import net.sourceforge.pmd.symboltable.GlobalScope;
import net.sourceforge.pmd.symboltable.LocalScope;
import net.sourceforge.pmd.symboltable.ScopeCreator;

public class ScopeCreatorTest extends TestCase {
    public void testScopesAreCreated() {
        ScopeCreator sc = new ScopeCreator();

        ASTCompilationUnit acu = new ASTCompilationUnit(1);
        acu.setScope(new GlobalScope());

        ASTTryStatement tryNode = new ASTTryStatement(2);
        tryNode.setScope(new LocalScope());
        tryNode.jjtSetParent(acu);

        ASTIfStatement ifNode = new ASTIfStatement(3);
        ifNode.jjtSetParent(tryNode);

        sc.visit(acu, null);

        assertTrue(ifNode.getScope() instanceof LocalScope);
    }

/*
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
*/
}
