/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.java.BaseParserTest;
import net.sourceforge.pmd.lang.java.ast.internal.PrettyPrintingUtil;

class ASTVariableIdTest extends BaseParserTest {


    @Test
    void testIsExceptionBlockParameter() {
        ASTCompilationUnit acu = java.parse(EXCEPTION_PARAMETER);
        ASTVariableId id = acu.descendants(ASTVariableId.class).first();
        assertTrue(id.isExceptionBlockParameter());
    }

    @Test
    void testTypeNameNode() {
        ASTCompilationUnit acu = java.parse(TYPE_NAME_NODE);
        ASTVariableId id = acu.descendants(ASTVariableId.class).first();

        ASTClassType name = (ASTClassType) id.getTypeNameNode();
        assertEquals("String", name.getSimpleName());
    }

    @Test
    void testAnnotations() {
        ASTCompilationUnit acu = java.parse(TEST_ANNOTATIONS);
        ASTVariableId id = acu.descendants(ASTVariableId.class).first();

        ASTClassType name = (ASTClassType) id.getTypeNode();
        assertEquals("String", name.getSimpleName());
    }

    @Test
    void testLambdaWithType() throws Exception {
        ASTCompilationUnit acu = java8.parse(TEST_LAMBDA_WITH_TYPE);
        ASTLambdaExpression lambda = acu.descendants(ASTLambdaExpression.class).first();
        ASTVariableId f = lambda.descendants(ASTVariableId.class).first();
        assertEquals("File", PrettyPrintingUtil.prettyPrintType(f.getTypeNode()));
    }

    @Test
    void testLambdaWithoutType() throws Exception {
        ASTCompilationUnit acu = java8.parse(TEST_LAMBDA_WITHOUT_TYPE);
        ASTLambdaExpression lambda = acu.descendants(ASTLambdaExpression.class).first();
        ASTVariableId f = lambda.descendants(ASTVariableId.class).first();
        assertNull(f.getTypeNode());
    }

    private static final String TYPE_NAME_NODE = "public class Test {\n  private String bar;\n}";
    private static final String EXCEPTION_PARAMETER = "public class Test { { try {} catch(Exception ie) {} } }";
    private static final String TEST_ANNOTATIONS = "public class Foo {\n    public void bar(@A1 @A2 String s) {}\n}";
    private static final String TEST_LAMBDA_WITH_TYPE =
        "public class Foo {\n    public void bar() {\n        FileFilter java = (File f) -> f.getName().endsWith(\".java\");\n    }\n}\n";
    private static final String TEST_LAMBDA_WITHOUT_TYPE =
        "public class Foo {\n    public void bar() {\n        FileFilter java2 = f -> f.getName().endsWith(\".java\");\n    }\n}\n";

}
