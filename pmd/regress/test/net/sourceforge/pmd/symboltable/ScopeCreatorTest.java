/*
 * User: tom
 * Date: Oct 10, 2002
 * Time: 8:27:50 AM
 */
package test.net.sourceforge.pmd.symboltable;

import junit.framework.TestCase;
import net.sourceforge.pmd.symboltable.ScopeCreator;
import net.sourceforge.pmd.symboltable.LocalScope;
import net.sourceforge.pmd.ast.ASTTryStatement;
import net.sourceforge.pmd.ast.ASTIfStatement;

public class ScopeCreatorTest extends TestCase {
    public void testScopesAreCreated() {
        ScopeCreator sc = new ScopeCreator();

        ASTTryStatement tryNode = new ASTTryStatement(1);
        tryNode.setScope(new LocalScope());

        ASTIfStatement ifNode = new ASTIfStatement(2);
        ifNode.jjtSetParent(tryNode);

        sc.visit(ifNode, null);

        assertTrue(tryNode.getScope() instanceof LocalScope);
    }
}
