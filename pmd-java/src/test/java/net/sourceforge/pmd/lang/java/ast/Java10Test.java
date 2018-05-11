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
import net.sourceforge.pmd.lang.java.typeresolution.typedefinition.JavaTypeDefinition;

public class Java10Test {

    private static String loadSource(String name) {
        try {
            return IOUtils.toString(Java10Test.class.getResourceAsStream("jdkversiontests/java10/" + name),
                    StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void assertVarType(ASTType type) {
        assertEquals("var", type.getImage());
        assertEquals(0, type.jjtGetNumChildren());
        assertTrue(type.isVarType());
    }

    @Test
    public void testLocalVarInferenceBeforeJava10() {
        // note, it can be parsed, but we'll have a ReferenceType of "var"
        ASTCompilationUnit compilationUnit = ParserTstUtil.parseAndTypeResolveJava("9",
                loadSource("LocalVariableTypeInference.java"));

        List<ASTLocalVariableDeclaration> localVars = compilationUnit.findDescendantsOfType(ASTLocalVariableDeclaration.class);
        assertEquals(3, localVars.size());

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
        assertEquals(3, localVars.size());

        // first: var list = new ArrayList<String>();
        ASTType type = localVars.get(0).getTypeNode();
        assertVarType(type);
        assertSame("type should be ArrayList", ArrayList.class, type.getType());
        assertEquals("type should be ArrayList<String>", JavaTypeDefinition.forClass(ArrayList.class, JavaTypeDefinition.forClass(String.class)),
                type.getTypeDefinition());
        ASTVariableDeclarator varDecl = localVars.get(0).getFirstChildOfType(ASTVariableDeclarator.class);
        assertEquals("type should be equal", type.getTypeDefinition(), varDecl.getTypeDefinition());
        ASTVariableDeclaratorId varId = varDecl.getFirstChildOfType(ASTVariableDeclaratorId.class);
        assertEquals("type should be equal", type.getTypeDefinition(), varId.getTypeDefinition());

        // second: var stream = list.stream();
        ASTType type2 = localVars.get(1).getTypeNode();
        assertVarType(type2);
        // TODO: return type of method call is unknown
        //assertEquals("type should be Stream<String>", JavaTypeDefinition.forClass(Stream.class, JavaTypeDefinition.forClass(String.class)),
        //        type2.getTypeDefinition());

        // third: var s = "Java 10";
        ASTType type3 = localVars.get(2).getTypeNode();
        assertVarType(type3);
        assertEquals("type should be String", JavaTypeDefinition.forClass(String.class), type3.getTypeDefinition());

        ASTArgumentList argumentList = compilationUnit.getFirstDescendantOfType(ASTArgumentList.class);
        ASTExpression expression3 = argumentList.getFirstChildOfType(ASTExpression.class);
        assertEquals("type should be String", JavaTypeDefinition.forClass(String.class), expression3.getTypeDefinition());
    }

    @Test
    public void testForLoopWithVar() {
        ASTCompilationUnit compilationUnit = ParserTstUtil.parseAndTypeResolveJava("10",
                loadSource("LocalVariableTypeInferenceForLoop.java"));
        List<ASTLocalVariableDeclaration> localVars = compilationUnit.findDescendantsOfType(ASTLocalVariableDeclaration.class);
        assertEquals(1, localVars.size());

        ASTType type = localVars.get(0).getTypeNode();
        assertVarType(type);
        assertSame("type should be int", Integer.TYPE, type.getType());
    }

    @Test
    public void testForLoopEnhancedWithVar() {
        ASTCompilationUnit compilationUnit = ParserTstUtil.parseAndTypeResolveJava("10",
                loadSource("LocalVariableTypeInferenceForLoopEnhanced.java"));
        List<ASTLocalVariableDeclaration> localVars = compilationUnit.findDescendantsOfType(ASTLocalVariableDeclaration.class);
        assertEquals(1, localVars.size());

        ASTType type = localVars.get(0).getTypeNode();
        assertVarType(type);
        assertSame("type should be String", String.class, type.getType());
    }

    @Test
    public void testForLoopEnhancedWithVar2() {
        ASTCompilationUnit compilationUnit = ParserTstUtil.parseAndTypeResolveJava("10",
                loadSource("LocalVariableTypeInferenceForLoopEnhanced2.java"));
        List<ASTLocalVariableDeclaration> localVars = compilationUnit.findDescendantsOfType(ASTLocalVariableDeclaration.class);
        assertEquals(4, localVars.size());

        ASTType type2 = localVars.get(1).getTypeNode();
        assertVarType(type2);
        assertSame("type should be String", String.class, type2.getType());
        ASTVariableDeclarator varDecl2 = localVars.get(1).getFirstChildOfType(ASTVariableDeclarator.class);
        assertSame("type should be String", String.class, varDecl2.getType());
        ASTVariableDeclaratorId varId2 = varDecl2.getFirstChildOfType(ASTVariableDeclaratorId.class);
        assertSame("type should be String", String.class, varId2.getType());

        ASTType type4 = localVars.get(3).getTypeNode();
        assertVarType(type4);
        assertSame("type should be int", Integer.TYPE, type4.getType());
    }

    @Test
    public void testTryWithResourcesWithVar() {
        ASTCompilationUnit compilationUnit = ParserTstUtil.parseAndTypeResolveJava("10",
                loadSource("LocalVariableTypeInferenceTryWithResources.java"));
        List<ASTResource> resources = compilationUnit.findDescendantsOfType(ASTResource.class);
        assertEquals(1, resources.size());

        ASTType type = resources.get(0).getTypeNode();
        assertVarType(type);
        assertSame("type should be FileInputStream", FileInputStream.class, type.getType());
        ASTVariableDeclaratorId varId = resources.get(0).getVariableDeclaratorId();
        assertSame("type should be FileInputStream", FileInputStream.class, varId.getType());
    }
}
