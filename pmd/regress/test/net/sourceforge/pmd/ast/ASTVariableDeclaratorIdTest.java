/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.ast;

import net.sourceforge.pmd.ast.ASTBlock;
import net.sourceforge.pmd.ast.ASTTryStatement;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import test.net.sourceforge.pmd.testframework.ParserTst;

public class ASTVariableDeclaratorIdTest extends ParserTst {

    public void testIsExceptionBlockParameter() {
        ASTTryStatement tryNode = new ASTTryStatement(1);
        ASTBlock block = new ASTBlock(2);
        ASTVariableDeclaratorId v = new ASTVariableDeclaratorId(3);
        v.jjtSetParent(block);
        block.jjtSetParent(tryNode);
        assertTrue(v.isExceptionBlockParameter());
    }
}
