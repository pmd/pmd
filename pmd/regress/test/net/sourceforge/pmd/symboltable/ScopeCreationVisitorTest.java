/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.symboltable;

import junit.framework.TestCase;
import net.sourceforge.pmd.ast.ASTClassBodyDeclaration;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTIfStatement;
import net.sourceforge.pmd.ast.ASTTryStatement;
import net.sourceforge.pmd.symboltable.BasicScopeCreationVisitor;
import net.sourceforge.pmd.symboltable.GlobalScope;
import net.sourceforge.pmd.symboltable.LocalScope;

import java.util.EmptyStackException;

public class ScopeCreationVisitorTest extends TestCase {

    private class MyCB extends ASTClassBodyDeclaration {
        public MyCB() {
            super(1);
        }
        public boolean isAnonymousInnerClass() {
            return true;
        }
    }

    public void testScopesAreCreated() {
        BasicScopeCreationVisitor sc = new BasicScopeCreationVisitor();

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

    public void testAnonymousInnerClassIsCreated() {
        BasicScopeCreationVisitor sc = new BasicScopeCreationVisitor();
        ASTClassBodyDeclaration cb = new MyCB();
        try {
            sc.visit(cb, null);
            fail();
        }
        catch (EmptyStackException ex) {
            // OK - this means, the method for scope creation has been called
        }
     }

    public void testAnonymousInnerClassIsNotCreated() {
        BasicScopeCreationVisitor sc = new BasicScopeCreationVisitor();
        ASTClassBodyDeclaration cb = new ASTClassBodyDeclaration(1);
        sc.visit(cb, null);
        try {
            cb.getScope();
            fail();
        }
        catch (NullPointerException ex) {
            // OK - this means no scope exists and there is no parent node the
            // scope of which might be referred to.
        }
    }

}
