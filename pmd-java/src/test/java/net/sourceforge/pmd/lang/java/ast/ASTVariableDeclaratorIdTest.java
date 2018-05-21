/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static net.sourceforge.pmd.lang.java.ParserTstUtil.getNodes;
import static net.sourceforge.pmd.lang.java.ParserTstUtil.parseJava18;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.sourceforge.pmd.PMD;

public class ASTVariableDeclaratorIdTest {




    @Test
    public void testIsExceptionBlockParameter() {
        ASTCompilationUnit acu = getNodes(ASTCompilationUnit.class, EXCEPTION_PARAMETER).iterator().next();
        ASTVariableDeclaratorId id = acu.getFirstDescendantOfType(ASTVariableDeclaratorId.class);
        assertTrue(id.isExceptionBlockParameter());
    }

    @Test
    public void testTypeNameNode() {
        ASTCompilationUnit acu = getNodes(ASTCompilationUnit.class, TYPE_NAME_NODE).iterator().next();
        ASTVariableDeclaratorId id = acu.findDescendantsOfType(ASTVariableDeclaratorId.class).get(0);

        ASTClassOrInterfaceType name = (ASTClassOrInterfaceType) id.getTypeNameNode().jjtGetChild(0);
        assertEquals("String", name.getImage());
    }

    @Test
    public void testAnnotations() {
        ASTCompilationUnit acu = getNodes(ASTCompilationUnit.class, TEST_ANNOTATIONS).iterator().next();
        ASTVariableDeclaratorId id = acu.findDescendantsOfType(ASTVariableDeclaratorId.class).get(0);

        ASTClassOrInterfaceType name = (ASTClassOrInterfaceType) id.getTypeNameNode().jjtGetChild(0);
        assertEquals("String", name.getImage());
    }

    @Test
    public void testLambdaWithType() throws Exception {
        ASTCompilationUnit acu = parseJava18(TEST_LAMBDA_WITH_TYPE);
        ASTLambdaExpression lambda = acu.getFirstDescendantOfType(ASTLambdaExpression.class);
        ASTVariableDeclaratorId f = lambda.getFirstDescendantOfType(ASTVariableDeclaratorId.class);
        assertEquals("File", f.getTypeNode().getTypeImage());
        assertEquals("File", f.getTypeNameNode().jjtGetChild(0).getImage());
    }

    @Test
    public void testLambdaWithoutType() throws Exception {
        ASTCompilationUnit acu = parseJava18(TEST_LAMBDA_WITHOUT_TYPE);
        ASTLambdaExpression lambda = acu.getFirstDescendantOfType(ASTLambdaExpression.class);
        ASTVariableDeclaratorId f = lambda.getFirstDescendantOfType(ASTVariableDeclaratorId.class);
        assertNull(f.getTypeNode());
        assertNull(f.getTypeNameNode());
    }

    private static final String TYPE_NAME_NODE = "public class Test {" + PMD.EOL + "  private String bar;" + PMD.EOL
            + "}";
    private static final String EXCEPTION_PARAMETER = "public class Test { { try {} catch(Exception ie) {} } }";
    private static final String TEST_ANNOTATIONS = "public class Foo {" + PMD.EOL
            + "    public void bar(@A1 @A2 String s) {}" + PMD.EOL + "}";
    private static final String TEST_LAMBDA_WITH_TYPE = "public class Foo {\n" + "    public void bar() {\n"
            + "        FileFilter java = (File f) -> f.getName().endsWith(\".java\");\n" + "    }\n" + "}\n";
    private static final String TEST_LAMBDA_WITHOUT_TYPE = "public class Foo {\n" + "    public void bar() {\n"
            + "        FileFilter java2 = f -> f.getName().endsWith(\".java\");\n" + "    }\n" + "}\n";

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(ASTVariableDeclaratorIdTest.class);
    }
}
