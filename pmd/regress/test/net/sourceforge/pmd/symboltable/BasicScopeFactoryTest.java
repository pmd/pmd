/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.symboltable;

import junit.framework.TestCase;
import net.sourceforge.pmd.ast.*;
import net.sourceforge.pmd.symboltable.*;

import java.util.Stack;

public class BasicScopeFactoryTest extends TestCase {

    public void testGlobalScope() {
        ScopeFactory sf = new BasicScopeFactory();
        Stack s = new Stack();
        sf.openScope(s, new ASTCompilationUnit(1));
        assertEquals(1, s.size());
        assertTrue(s.get(0) instanceof GlobalScope);
    }

    public void testClassScope() {
        ScopeFactory sf = new BasicScopeFactory();
        Stack s = new Stack();
        sf.openScope(s, new ASTCompilationUnit(1));
        sf.openScope(s, new ASTUnmodifiedClassDeclaration(2));
        assertTrue(s.get(1) instanceof ClassScope);
        sf.openScope(s, new ASTUnmodifiedInterfaceDeclaration(1));
        assertTrue(s.get(2) instanceof ClassScope);
        sf.openScope(s, new ASTClassBodyDeclaration(1));
        assertTrue(s.get(3) instanceof ClassScope);
    }

    public void testMethodScope() {
        ScopeFactory sf = new BasicScopeFactory();
        Stack s = new Stack();
        sf.openScope(s, new ASTCompilationUnit(1));
        sf.openScope(s, new ASTMethodDeclaration(2));
        assertTrue(s.get(1) instanceof MethodScope);
        sf.openScope(s, new ASTConstructorDeclaration(1));
        assertTrue(s.get(2) instanceof MethodScope);
    }

    public void testLocalScope() {
        ScopeFactory sf = new BasicScopeFactory();
        Stack s = new Stack();
        sf.openScope(s, new ASTCompilationUnit(1));
        sf.openScope(s, new ASTBlock(2));
        assertTrue(s.get(1) instanceof LocalScope);
        sf.openScope(s, new ASTTryStatement(1));
        assertTrue(s.get(2) instanceof LocalScope);
        sf.openScope(s, new ASTForStatement(1));
        assertTrue(s.get(3) instanceof LocalScope);
        sf.openScope(s, new ASTIfStatement(1));
        assertTrue(s.get(4) instanceof LocalScope);
    }
}
