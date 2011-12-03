/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.ast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTTryStatement;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.testframework.ParserTst;

import org.junit.Test;


public class ASTVariableDeclaratorIdTest extends ParserTst {

    @Test
    public void testIsExceptionBlockParameter() {
        ASTTryStatement tryNode = new ASTTryStatement(1);
        ASTBlock block = new ASTBlock(2);
        ASTVariableDeclaratorId v = new ASTVariableDeclaratorId(3);
        v.jjtSetParent(block);
        block.jjtSetParent(tryNode);
        assertTrue(v.isExceptionBlockParameter());
    }

    @Test
    public void testTypeNameNode() throws Throwable {
        ASTCompilationUnit acu = super.getNodes(ASTCompilationUnit.class, TYPE_NAME_NODE).iterator().next();
        ASTVariableDeclaratorId id = acu.findDescendantsOfType(ASTVariableDeclaratorId.class).get(0);

        ASTClassOrInterfaceType name = (ASTClassOrInterfaceType) id.getTypeNameNode().jjtGetChild(0);
        assertEquals("String", name.getImage());
    }

    @Test
    public void testAnnotations() throws Throwable {
        ASTCompilationUnit acu = super.getNodes(ASTCompilationUnit.class, TEST_ANNOTATIONS).iterator().next();
        ASTVariableDeclaratorId id = acu.findDescendantsOfType(ASTVariableDeclaratorId.class).get(0);

        ASTClassOrInterfaceType name = (ASTClassOrInterfaceType) id.getTypeNameNode().jjtGetChild(0);
        assertEquals("String", name.getImage());
    }

    private static final String TYPE_NAME_NODE =
            "public class Test {" + PMD.EOL +
            "  private String bar;" + PMD.EOL +
            "}";

    private static final String TEST_ANNOTATIONS =
            "public class Foo {" + PMD.EOL +
            "    public void bar(@A1 @A2 String s) {}" + PMD.EOL +
            "}";

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(ASTVariableDeclaratorIdTest.class);
    }
}
