/*
 * User: tom
 * Date: Oct 1, 2002
 * Time: 3:24:28 PM
 */
package test.net.sourceforge.pmd.symboltable;

import junit.framework.TestCase;
import net.sourceforge.pmd.symboltable.*;
import net.sourceforge.pmd.ast.*;

public class LocalScopeTest extends TestCase {

    private class MyASTVariableDeclaratorId extends ASTVariableDeclaratorId {
        public MyASTVariableDeclaratorId(int x) {
            super(x);
        }
        public boolean isExceptionBlockParameter() {
            return true;
        }
    }

    public void testNameWithThisOrSuperIsNotFlaggedAsUnused() {
        LocalScope scope = new LocalScope();
        ASTName name = new ASTName(1);
        name.setImage("foo");
        ASTPrimaryPrefix prefix = new ASTPrimaryPrefix(2);
        prefix.setUsesThisModifier();
        name.jjtAddChild(prefix, 1);
        NameOccurrence occ = new NameOccurrence(name, "foo");
        scope.addOccurrence(occ);
        assertTrue(!scope.getUnusedDeclarations().hasNext());
    }

    public void testNameWithSuperIsNotFlaggedAsUnused() {
        LocalScope scope = new LocalScope();
        ASTName name = new ASTName(1);
        name.setImage("foo");
        ASTPrimaryPrefix prefix = new ASTPrimaryPrefix(2);
        prefix.setUsesSuperModifier();
        name.jjtAddChild(prefix, 1);
        NameOccurrence occ = new NameOccurrence(name, "foo");
        scope.addOccurrence(occ);
        assertTrue(!scope.getUnusedDeclarations().hasNext());
    }

    public void testExceptionParamNameIsDiscarded() {
        ASTVariableDeclaratorId node = new MyASTVariableDeclaratorId(1);
        NameDeclaration decl = new NameDeclaration(node);
        LocalScope scope = new LocalScope();
        scope.addVariableDeclaration(decl);
        assertTrue(!scope.getUnusedDeclarations().hasNext());
    }

}
