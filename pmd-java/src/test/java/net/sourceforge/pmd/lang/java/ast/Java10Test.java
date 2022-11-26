/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.TypeSystem;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;

class Java10Test {

    private final JavaParsingHelper java10 =
        JavaParsingHelper.DEFAULT.withDefaultVersion("10")
                                 .withResourceContext(Java10Test.class, "jdkversiontests/java10/");

    private final JavaParsingHelper java9 = java10.withDefaultVersion("9");

    @Test
    void testLocalVarInferenceBeforeJava10() {
        // note, it can be parsed, but we'll have a ReferenceType of "var"

        List<ASTLocalVariableDeclaration> localVars = java9.parseResource("LocalVariableTypeInference.java")
                                                           .findDescendantsOfType(ASTLocalVariableDeclaration.class);
        assertEquals(3, localVars.size());

        ASTVariableDeclaratorId varId = localVars.get(0).getVarIds().firstOrThrow();

        // first: var list = new ArrayList<String>();
        assertTrue(varId.getTypeNode() instanceof ASTClassOrInterfaceType);
        // in that case, we don't have a class named "var", so the type will be null
        assertTrue(varId.getTypeMirror().getSymbol().isUnresolved());

        // check the type of the variable initializer's expression
        ASTExpression initExpression = varId.getInitializer();
        assertTrue(TypeTestUtil.isA(ArrayList.class, initExpression), "type should be ArrayList");
    }

    @Test
    void testLocalVarInferenceCanBeParsedJava10() {
        ASTCompilationUnit compilationUnit = java10.parseResource("LocalVariableTypeInference.java");
        List<ASTLocalVariableDeclaration> localVars = compilationUnit.findDescendantsOfType(ASTLocalVariableDeclaration.class);
        assertEquals(3, localVars.size());

        TypeSystem ts = compilationUnit.getTypeSystem();
        JClassType stringT = (JClassType) ts.typeOf(ts.getClassSymbol(String.class), false);

        // first: var list = new ArrayList<String>();
        assertNull(localVars.get(0).getTypeNode());
        ASTVariableDeclaratorId varDecl = localVars.get(0).getVarIds().firstOrThrow();
        assertEquals(ts.parameterise(ts.getClassSymbol(ArrayList.class), listOf(stringT)), varDecl.getTypeMirror(), "type should be ArrayList<String>");

        // second: var stream = list.stream();
        assertNull(localVars.get(1).getTypeNode());
        ASTVariableDeclaratorId varDecl2 = localVars.get(1).getVarIds().firstOrThrow();
        assertEquals(ts.parameterise(ts.getClassSymbol(Stream.class), listOf(stringT)),
                varDecl2.getTypeMirror(),
                "type should be Stream<String>");

        // third: var s = "Java 10";
        assertNull(localVars.get(2).getTypeNode());
        ASTVariableDeclaratorId varDecl3 = localVars.get(2).getVarIds().firstOrThrow();
        assertEquals(stringT, varDecl3.getTypeMirror(), "type should be String");
    }

    @Test
    void testForLoopWithVar() {
        List<ASTLocalVariableDeclaration> localVars = java10.parseResource("LocalVariableTypeInferenceForLoop.java")
                                                            .findDescendantsOfType(ASTLocalVariableDeclaration.class);
        assertEquals(1, localVars.size());

        assertNull(localVars.get(0).getTypeNode());
        ASTVariableDeclaratorId varDecl = localVars.get(0).getVarIds().firstOrThrow();
        assertSame(varDecl.getTypeSystem().INT, varDecl.getTypeMirror(), "type should be int");
    }

    @Test
    void testForLoopEnhancedWithVar() {
        List<ASTLocalVariableDeclaration> localVars = java10.parseResource("LocalVariableTypeInferenceForLoopEnhanced.java")
                                                            .findDescendantsOfType(ASTLocalVariableDeclaration.class);
        assertEquals(1, localVars.size());

        assertNull(localVars.get(0).getTypeNode());
        ASTVariableDeclaratorId varDecl = localVars.get(0).getVarIds().firstOrThrow();
        assertTrue(TypeTestUtil.isA(String.class, varDecl), "type should be String");
    }

    @Test
    void testForLoopEnhancedWithVar2() {
        List<ASTLocalVariableDeclaration> localVars = java10.parseResource("LocalVariableTypeInferenceForLoopEnhanced2.java")
                                                            .findDescendantsOfType(ASTLocalVariableDeclaration.class);
        assertEquals(4, localVars.size());

        assertNull(localVars.get(1).getTypeNode());
        @NonNull ASTVariableDeclaratorId varDecl2 = localVars.get(1).getVarIds().firstOrThrow();
        assertTrue(TypeTestUtil.isA(String.class, varDecl2), "type should be String");

        assertNull(localVars.get(3).getTypeNode());
        ASTVariableDeclaratorId varDecl4 = localVars.get(3).getVarIds().firstOrThrow();
        assertSame(varDecl2.getTypeSystem().INT, varDecl4.getTypeMirror(), "type should be int");
    }

    @Test
    void testTryWithResourcesWithVar() {
        List<ASTResource> resources = java10.parseResource("LocalVariableTypeInferenceTryWithResources.java")
                                            .findDescendantsOfType(ASTResource.class);
        assertEquals(1, resources.size());

        assertNull(resources.get(0).asLocalVariableDeclaration().getTypeNode());
        ASTVariableDeclaratorId varId = resources.get(0).asLocalVariableDeclaration().getVarIds().firstOrThrow();
        assertTrue(TypeTestUtil.isA(FileInputStream.class, varId), "type should be FileInputStream");
    }

    @Test
    void testTypeResNullPointer() {
        java10.parseResource("LocalVariableTypeInference_typeres.java");
    }

}
