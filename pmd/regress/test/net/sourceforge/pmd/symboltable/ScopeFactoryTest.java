/*
 * User: tom
 * Date: Oct 3, 2002
 * Time: 2:48:19 PM
 */
package test.net.sourceforge.pmd.symboltable;

import junit.framework.TestCase;
import net.sourceforge.pmd.ast.*;
import net.sourceforge.pmd.symboltable.ClassScope;
import net.sourceforge.pmd.symboltable.GlobalScope;
import net.sourceforge.pmd.symboltable.LocalScope;
import net.sourceforge.pmd.symboltable.MethodScope;
import net.sourceforge.pmd.symboltable.ScopeFactory;

public class ScopeFactoryTest extends TestCase {

    public void testGlobalScope() {
        ScopeFactory sf = new ScopeFactory();
        assertTrue(sf.createScope(new ASTCompilationUnit(1)) instanceof GlobalScope);
    }

    public void testClassScope() {
        ScopeFactory sf = new ScopeFactory();
        assertTrue(sf.createScope(new ASTUnmodifiedClassDeclaration(1)) instanceof ClassScope);
        assertTrue(sf.createScope(new ASTUnmodifiedInterfaceDeclaration(1)) instanceof ClassScope);
    }

    public void testfunctionScope() {
        ScopeFactory sf = new ScopeFactory();
        assertTrue(sf.createScope(new ASTMethodDeclaration(1)) instanceof MethodScope);
        assertTrue(sf.createScope(new ASTConstructorDeclaration(1)) instanceof MethodScope);
    }

    public void testLocalScope() {
        ScopeFactory sf = new ScopeFactory();
        assertTrue(sf.createScope(new ASTBlock(1)) instanceof LocalScope);
        assertTrue(sf.createScope(new ASTTryStatement(1)) instanceof LocalScope);
        assertTrue(sf.createScope(new ASTForStatement(1)) instanceof LocalScope);
        assertTrue(sf.createScope(new ASTIfStatement(1)) instanceof LocalScope);
    }

    public void testUnknownScope_ThisShouldNeverHappen() throws Throwable {
        ScopeFactory sf = new ScopeFactory();
        try {
            sf.createScope(new ASTClassBody(1));
            throw new Throwable("Should have failed!");
        } catch (RuntimeException re) {
            // cool
        }
    }
}
