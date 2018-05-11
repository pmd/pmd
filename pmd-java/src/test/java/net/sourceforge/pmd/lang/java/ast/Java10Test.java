/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import net.sourceforge.pmd.lang.java.ParserTstUtil;

public class Java10Test {

    private static String loadSource(String name) {
        try {
            return IOUtils.toString(Java10Test.class.getResourceAsStream("jdkversiontests/java10/" + name),
                    StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testLocalVarInferenceBeforeJava10() {
        // note, it can be parsed, but we'll have a ReferenceType of "var"
        ASTCompilationUnit compilationUnit = ParserTstUtil.parseAndTypeResolveJava("9",
                loadSource("LocalVariableTypeInference.java"));

        List<ASTLocalVariableDeclaration> localVars = compilationUnit.findDescendantsOfType(ASTLocalVariableDeclaration.class);
        assertEquals(2, localVars.size());

        // first: var list = new ArrayList<String>();
        ASTType type = localVars.get(0).getFirstChildOfType(ASTType.class);
        assertEquals("var", type.getTypeImage());
        assertEquals(1, type.jjtGetNumChildren());
        ASTReferenceType referenceType = type.getFirstChildOfType(ASTReferenceType.class);
        assertNotNull(referenceType);
        assertEquals(1, referenceType.jjtGetNumChildren());
        ASTClassOrInterfaceType classType = referenceType.getFirstChildOfType(ASTClassOrInterfaceType.class);
        assertNotNull(classType);
        assertEquals("var", classType.getImage());
        // in that case, we don't have a class named "var", so the type will be null
        assertNull(classType.getType());
        assertNull(type.getType());
        assertFalse(type.isVarType());

        // check the type of the variable initializer's expression
        ASTExpression initExpression = localVars.get(0)
                .getFirstChildOfType(ASTVariableDeclarator.class)
                .getFirstChildOfType(ASTVariableInitializer.class)
                .getFirstChildOfType(ASTExpression.class);
        assertSame("type should be ArrayList", ArrayList.class, initExpression.getType());
    }

    @Test
    public void testLocalVarInferenceCanBeParsedJava10() {
        ASTCompilationUnit compilationUnit = ParserTstUtil.parseAndTypeResolveJava("10",
                loadSource("LocalVariableTypeInference.java"));
        List<ASTLocalVariableDeclaration> localVars = compilationUnit.findDescendantsOfType(ASTLocalVariableDeclaration.class);
        assertEquals(2, localVars.size());

        // first: var list = new ArrayList<String>();
        ASTType type = localVars.get(0).getFirstChildOfType(ASTType.class);
        assertEquals("var", type.getImage());
        assertTrue(type.isVarType());
        assertEquals(0, type.jjtGetNumChildren());
        assertSame("type should be ArrayList", ArrayList.class, type.getType());
    }

    @Test
    public void testForLoopWithVar() {
        ASTCompilationUnit compilationUnit = ParserTstUtil.parseAndTypeResolveJava("10",
                loadSource("LocalVariableTypeInferenceForLoop.java"));
        List<ASTLocalVariableDeclaration> localVars = compilationUnit.findDescendantsOfType(ASTLocalVariableDeclaration.class);
        assertEquals(1, localVars.size());

        ASTType type = localVars.get(0).getFirstChildOfType(ASTType.class);
        assertEquals("var", type.getImage());
        assertTrue(type.isVarType());
        assertEquals(0, type.jjtGetNumChildren());
        assertSame("type should be int", Integer.TYPE, type.getType());
    }

    @Test
    public void testForLoopEnhancedWithVar() {
        ASTCompilationUnit compilationUnit = ParserTstUtil.parseAndTypeResolveJava("10",
                loadSource("LocalVariableTypeInferenceForLoopEnhanced.java"));
        List<ASTLocalVariableDeclaration> localVars = compilationUnit.findDescendantsOfType(ASTLocalVariableDeclaration.class);
        assertEquals(1, localVars.size());

        ASTType type = localVars.get(0).getFirstChildOfType(ASTType.class);
        assertEquals("var", type.getImage());
        assertTrue(type.isVarType());
        assertEquals(0, type.jjtGetNumChildren());
        //TODO: the type is not known...
        //assertSame("type should be String", String.class, type.getType());
    }

    @Test
    public void testTryWithResourcesWithVar() {
        ASTCompilationUnit compilationUnit = ParserTstUtil.parseAndTypeResolveJava("10",
                loadSource("LocalVariableTypeInferenceTryWithResources.java"));
        List<ASTResource> resources = compilationUnit.findDescendantsOfType(ASTResource.class);
        assertEquals(1, resources.size());

        ASTType type = resources.get(0).getTypeNode();
        assertEquals("var", type.getImage());
        assertEquals(0, type.jjtGetNumChildren());
        assertSame("type should be FileInputStream", FileInputStream.class, type.getType());
    }
}
