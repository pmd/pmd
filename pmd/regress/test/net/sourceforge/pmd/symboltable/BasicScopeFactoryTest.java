package test.net.sourceforge.pmd.symboltable;

import junit.framework.TestCase;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.symboltable.BasicScopeFactory;
import net.sourceforge.pmd.symboltable.GlobalScope;
import net.sourceforge.pmd.symboltable.ScopeCreationVisitor;
import net.sourceforge.pmd.symboltable.ScopeFactory;

public class BasicScopeFactoryTest extends TestCase {

    public void testGlobalScope() {
        final ScopeFactory sf = new BasicScopeFactory();
        sf.openScope(new ScopeCreationVisitor() {
            public void cont(SimpleNode node) {
                assertTrue(sf.getCurrentScope() instanceof GlobalScope);
            }
        }, new ASTCompilationUnit(1));
    }

/*
    public void testClassScope() {
        ScopeFactory sf = new ScopeFactory();
        assertTrue(sf.createScope(new ASTUnmodifiedClassDeclaration(1)) instanceof ClassScope);
        assertTrue(sf.createScope(new ASTUnmodifiedInterfaceDeclaration(1)) instanceof ClassScope);
    }
*/

    /*
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

*/
}
