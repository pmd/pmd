/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.java.typeresolution.typedefinition.JavaTypeDefinition;

public class Java10Test {

    private final JavaParsingHelper java10 =
        JavaParsingHelper.WITH_PROCESSING.withDefaultVersion("10")
                                         .withResourceContext(Java10Test.class, "jdkversiontests/java10/");

    private final JavaParsingHelper java9 = java10.withDefaultVersion("9");

    @Test
    public void testLocalVarInferenceBeforeJava10() {
        // note, it can be parsed, but we'll have a ReferenceType of "var"

        List<ASTLocalVariableDeclaration> localVars = java9.parseResource("LocalVariableTypeInference.java")
                                                           .findDescendantsOfType(ASTLocalVariableDeclaration.class);
        assertEquals(3, localVars.size());

        // first: var list = new ArrayList<String>();
        ASTType type = localVars.get(0).getFirstChildOfType(ASTType.class);
        assertEquals("var", type.getTypeImage());
        assertEquals(1, type.getNumChildren());
        ASTReferenceType referenceType = type.getFirstChildOfType(ASTReferenceType.class);
        assertNotNull(referenceType);
        assertEquals(1, referenceType.getNumChildren());
        ASTClassOrInterfaceType classType = referenceType.getFirstChildOfType(ASTClassOrInterfaceType.class);
        assertNotNull(classType);
        assertEquals("var", classType.getImage());
        // in that case, we don't have a class named "var", so the type will be null
        assertNull(classType.getType());
        assertNull(type.getType());

        // check the type of the variable initializer's expression
        ASTExpression initExpression = localVars.get(0)
                .getFirstChildOfType(ASTVariableDeclarator.class)
                .getFirstChildOfType(ASTVariableInitializer.class)
                .getFirstChildOfType(ASTExpression.class);
        assertSame("type should be ArrayList", ArrayList.class, initExpression.getType());
    }

    @Test
    public void testLocalVarInferenceCanBeParsedJava10() {
        ASTCompilationUnit compilationUnit = java10.parseResource("LocalVariableTypeInference.java");
        List<ASTLocalVariableDeclaration> localVars = compilationUnit.findDescendantsOfType(ASTLocalVariableDeclaration.class);
        assertEquals(3, localVars.size());

        // first: var list = new ArrayList<String>();
        assertNull(localVars.get(0).getTypeNode());
        ASTVariableDeclarator varDecl = localVars.get(0).getFirstChildOfType(ASTVariableDeclarator.class);
        assertSame("type should be ArrayList", ArrayList.class, varDecl.getType());
        assertEquals("type should be ArrayList<String>", JavaTypeDefinition.forClass(ArrayList.class, JavaTypeDefinition.forClass(String.class)),
                varDecl.getTypeDefinition());
        ASTVariableDeclaratorId varId = varDecl.getFirstChildOfType(ASTVariableDeclaratorId.class);
        assertEquals("type should be equal", varDecl.getTypeDefinition(), varId.getTypeDefinition());

        // second: var stream = list.stream();
        assertNull(localVars.get(1).getTypeNode());
        //ASTVariableDeclarator varDecl2 = localVars.get(1).getFirstChildOfType(ASTVariableDeclarator.class);
        // TODO: return type of method call is unknown
        // assertEquals("type should be Stream<String>", JavaTypeDefinition.forClass(Stream.class, JavaTypeDefinition.forClass(String.class)),
        //         varDecl2.getTypeDefinition());

        // third: var s = "Java 10";
        assertNull(localVars.get(2).getTypeNode());
        ASTVariableDeclarator varDecl3 = localVars.get(2).getFirstChildOfType(ASTVariableDeclarator.class);
        assertEquals("type should be String", JavaTypeDefinition.forClass(String.class), varDecl3.getTypeDefinition());

        ASTArgumentList argumentList = compilationUnit.getFirstDescendantOfType(ASTArgumentList.class);
        ASTExpression expression3 = argumentList.getFirstChildOfType(ASTExpression.class);
        assertEquals("type should be String", JavaTypeDefinition.forClass(String.class), expression3.getTypeDefinition());
    }

    @Test
    public void testForLoopWithVar() {
        List<ASTLocalVariableDeclaration> localVars = java10.parseResource("LocalVariableTypeInferenceForLoop.java")
                                                            .findDescendantsOfType(ASTLocalVariableDeclaration.class);
        assertEquals(1, localVars.size());

        assertNull(localVars.get(0).getTypeNode());
        ASTVariableDeclarator varDecl = localVars.get(0).getFirstChildOfType(ASTVariableDeclarator.class);
        assertSame("type should be int", Integer.TYPE, varDecl.getType());
    }

    @Test
    public void testForLoopEnhancedWithVar() {
        List<ASTLocalVariableDeclaration> localVars = java10.parseResource("LocalVariableTypeInferenceForLoopEnhanced.java")
                                                            .findDescendantsOfType(ASTLocalVariableDeclaration.class);
        assertEquals(1, localVars.size());

        assertNull(localVars.get(0).getTypeNode());
        ASTVariableDeclarator varDecl = localVars.get(0).getFirstChildOfType(ASTVariableDeclarator.class);
        assertSame("type should be String", String.class, varDecl.getType());
    }

    @Test
    public void testForLoopEnhancedWithVar2() {
        List<ASTLocalVariableDeclaration> localVars = java10.parseResource("LocalVariableTypeInferenceForLoopEnhanced2.java")
                                                            .findDescendantsOfType(ASTLocalVariableDeclaration.class);
        assertEquals(4, localVars.size());

        assertNull(localVars.get(1).getTypeNode());
        ASTVariableDeclarator varDecl2 = localVars.get(1).getFirstChildOfType(ASTVariableDeclarator.class);
        assertSame("type should be String", String.class, varDecl2.getType());
        ASTVariableDeclaratorId varId2 = varDecl2.getFirstChildOfType(ASTVariableDeclaratorId.class);
        assertSame("type should be String", String.class, varId2.getType());

        assertNull(localVars.get(3).getTypeNode());
        ASTVariableDeclarator varDecl4 = localVars.get(3).getFirstChildOfType(ASTVariableDeclarator.class);
        assertSame("type should be int", Integer.TYPE, varDecl4.getType());
    }

    @Test
    public void testTryWithResourcesWithVar() {
        List<ASTResource> resources = java10.parseResource("LocalVariableTypeInferenceTryWithResources.java")
                                            .findDescendantsOfType(ASTResource.class);
        assertEquals(1, resources.size());

        assertNull(resources.get(0).getTypeNode());
        ASTVariableDeclaratorId varId = resources.get(0).getVariableDeclaratorId();
        assertSame("type should be FileInputStream", FileInputStream.class, varId.getType());
    }

    @Test
    public void testTypeResNullPointer() {
        java10.parseResource("LocalVariableTypeInference_typeres.java");
    }

    @Test
    public void testVarAsIdentifier() {
        java10.parseResource("LocalVariableTypeInference_varAsIdentifier.java");
    }

    @Test(expected = ParseException.class)
    public void testVarAsTypeIdentifier() {
        java10.parseResource("LocalVariableTypeInference_varAsTypeIdentifier.java");
    }

    @Test(expected = ParseException.class)
    public void testVarAsAnnotationName() {
        java10.parseResource("LocalVariableTypeInference_varAsAnnotationName.java");
    }

    @Test(expected = ParseException.class)
    public void testVarAsEnumName() {
        java10.parseResource("LocalVariableTypeInference_varAsEnumName.java");
    }
}
