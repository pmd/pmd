/*
 * User: tom
 * Date: Oct 8, 2002
 * Time: 8:55:25 AM
 */
package test.net.sourceforge.pmd.ast;

import junit.framework.TestCase;
import net.sourceforge.pmd.ast.ASTBlock;
import net.sourceforge.pmd.ast.ASTTryStatement;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;

public class ASTVariableDeclaratorIdTest extends TestCase {

    public void testIsExceptionBlockParameter() {
        ASTTryStatement tryNode = new ASTTryStatement(1);
        ASTBlock block = new ASTBlock(2);

        ASTVariableDeclaratorId v = new ASTVariableDeclaratorId(3);
        v.jjtSetParent(block);
        block.jjtSetParent(tryNode);

        assertTrue(v.isExceptionBlockParameter());
    }
}
