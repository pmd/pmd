/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ASTVariableDeclaratorIdTest extends BaseParserTest {


    @Test
    public void testIsExceptionBlockParameter() {
        ASTCompilationUnit acu = java.parse(EXCEPTION_PARAMETER);
        ASTVariableDeclaratorId id = acu.getFirstDescendantOfType(ASTVariableDeclaratorId.class);
        assertTrue(id.isExceptionBlockParameter());
    }

    @Test
    public void testTypeNameNode() {
        ASTCompilationUnit acu = java.parse(TYPE_NAME_NODE);
        ASTVariableDeclaratorId id = acu.findDescendantsOfType(ASTVariableDeclaratorId.class).get(0);

        ASTClassOrInterfaceType name = (ASTClassOrInterfaceType) id.getTypeNameNode().getChild(0);
        assertEquals("String", name.getImage());
    }

    @Test
    public void testAnnotations() {
        ASTCompilationUnit acu = java.parse(TEST_ANNOTATIONS);
        ASTVariableDeclaratorId id = acu.findDescendantsOfType(ASTVariableDeclaratorId.class).get(0);

        ASTClassOrInterfaceType name = (ASTClassOrInterfaceType) id.getTypeNameNode().getChild(0);
        assertEquals("String", name.getImage());
    }

    @Test
    public void testLambdaWithType() throws Exception {
        ASTCompilationUnit acu = java8.parse(TEST_LAMBDA_WITH_TYPE);
        ASTLambdaExpression lambda = acu.getFirstDescendantOfType(ASTLambdaExpression.class);
        ASTVariableDeclaratorId f = lambda.getFirstDescendantOfType(ASTVariableDeclaratorId.class);
        assertEquals("File", f.getTypeNode().getTypeImage());
        assertEquals("File", f.getTypeNameNode().getChild(0).getImage());
    }

    @Test
    public void testLambdaWithoutType() throws Exception {
        ASTCompilationUnit acu = java8.parse(TEST_LAMBDA_WITHOUT_TYPE);
        ASTLambdaExpression lambda = acu.getFirstDescendantOfType(ASTLambdaExpression.class);
        ASTVariableDeclaratorId f = lambda.getFirstDescendantOfType(ASTVariableDeclaratorId.class);
        assertNull(f.getTypeNode());
        assertNull(f.getTypeNameNode());
    }

    private static final String TYPE_NAME_NODE = "public class Test {\n  private String bar;\n}";
    private static final String EXCEPTION_PARAMETER = "public class Test { { try {} catch(Exception ie) {} } }";
    private static final String TEST_ANNOTATIONS = "public class Foo {\n    public void bar(@A1 @A2 String s) {}\n}";
    private static final String TEST_LAMBDA_WITH_TYPE =
        "public class Foo {\n    public void bar() {\n        FileFilter java = (File f) -> f.getName().endsWith(\".java\");\n    }\n}\n";
    private static final String TEST_LAMBDA_WITHOUT_TYPE =
        "public class Foo {\n    public void bar() {\n        FileFilter java2 = f -> f.getName().endsWith(\".java\");\n    }\n}\n";

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(ASTVariableDeclaratorIdTest.class);
    }
}
