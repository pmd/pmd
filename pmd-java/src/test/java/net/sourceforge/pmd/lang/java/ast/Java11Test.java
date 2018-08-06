/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ParserTstUtil;

public class Java11Test {
    private static String loadSource(String name) {
        try {
            return IOUtils.toString(Java10Test.class.getResourceAsStream("jdkversiontests/java11/" + name),
                    StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testLocalVariableSyntaxForLambdaParametersWithJava10() {
        ASTCompilationUnit compilationUnit = ParserTstUtil.parseAndTypeResolveJava("10",
                loadSource("LocalVariableSyntaxForLambdaParameters.java"));

        List<ASTLambdaExpression> lambdas = compilationUnit.findDescendantsOfType(ASTLambdaExpression.class);
        Assert.assertEquals(4, lambdas.size());

        // (var x) -> String.valueOf(x);
        List<ASTFormalParameter> formalParameters = lambdas.get(0).findDescendantsOfType(ASTFormalParameter.class);
        Assert.assertEquals(1, formalParameters.size());
        ASTType type = formalParameters.get(0).getFirstChildOfType(ASTType.class);
        assertEquals("var", type.getTypeImage());
        assertEquals(1, type.jjtGetNumChildren());
        ASTReferenceType referenceType = type.getFirstChildOfType(ASTReferenceType.class);
        assertNotNull(referenceType);
        assertEquals(1, referenceType.jjtGetNumChildren());
        ASTClassOrInterfaceType classType = referenceType.getFirstChildOfType(ASTClassOrInterfaceType.class);
        assertNotNull(classType);
        assertEquals("var", classType.getImage());

        // (var x, var y) -> x + y;
        formalParameters = lambdas.get(1).findDescendantsOfType(ASTFormalParameter.class);
        Assert.assertEquals(2, formalParameters.size());
        type = formalParameters.get(0).getFirstChildOfType(ASTType.class);
        assertEquals("var", type.getTypeImage());
        assertEquals(1, type.jjtGetNumChildren());
        referenceType = type.getFirstChildOfType(ASTReferenceType.class);
        assertNotNull(referenceType);
        assertEquals(1, referenceType.jjtGetNumChildren());
        classType = referenceType.getFirstChildOfType(ASTClassOrInterfaceType.class);
        assertNotNull(classType);
        assertEquals("var", classType.getImage());
        type = formalParameters.get(1).getFirstChildOfType(ASTType.class);
        assertEquals("var", type.getTypeImage());
        assertEquals(1, type.jjtGetNumChildren());

        // (@Nonnull var x) -> String.valueOf(x);
        formalParameters = lambdas.get(2).findDescendantsOfType(ASTFormalParameter.class);
        Assert.assertEquals(1, formalParameters.size());
        Node firstChild = formalParameters.get(0).jjtGetChild(0);
        Assert.assertTrue(firstChild instanceof ASTAnnotation);
    }

    @Test
    public void testLocalVariableSyntaxForLambdaParametersWithJava11() {
        ASTCompilationUnit compilationUnit = ParserTstUtil.parseAndTypeResolveJava("11",
                loadSource("LocalVariableSyntaxForLambdaParameters.java"));

        List<ASTLambdaExpression> lambdas = compilationUnit.findDescendantsOfType(ASTLambdaExpression.class);
        Assert.assertEquals(4, lambdas.size());

        // (var x) -> String.valueOf(x);
        List<ASTFormalParameter> formalParameters = lambdas.get(0).findDescendantsOfType(ASTFormalParameter.class);
        Assert.assertEquals(1, formalParameters.size());
        Assert.assertNull(formalParameters.get(0).getTypeNode());
        Assert.assertTrue(formalParameters.get(0).isTypeInferred());
    }
}
