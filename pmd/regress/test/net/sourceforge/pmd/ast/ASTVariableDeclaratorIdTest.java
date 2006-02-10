/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.ast;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.ast.ASTBlock;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
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

    public void testTypeNameNode() throws Throwable {
        ASTCompilationUnit acu = (ASTCompilationUnit) (super.getNodes(ASTCompilationUnit.class, TYPE_NAME_NODE).iterator().next());
        ASTVariableDeclaratorId id = (ASTVariableDeclaratorId) acu.findChildrenOfType(ASTVariableDeclaratorId.class).get(0);

        ASTClassOrInterfaceType name = (ASTClassOrInterfaceType) id.getTypeNameNode().jjtGetChild(0);
        assertEquals("String", name.getImage());
    }

    private static final String TYPE_NAME_NODE =
            "public class Test {" + PMD.EOL +
            "  private String bar;" + PMD.EOL +
            "}";

}
