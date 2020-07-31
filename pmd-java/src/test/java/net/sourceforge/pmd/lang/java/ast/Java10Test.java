/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.Test;

import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.java.typeresolution.TypeHelper;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.TypeSystem;

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

        ASTVariableDeclaratorId varId = localVars.get(0).getVarIds().firstOrThrow();

        // first: var list = new ArrayList<String>();
        assertTrue(varId.getTypeNode() instanceof ASTClassOrInterfaceType);
        // in that case, we don't have a class named "var", so the type will be null
        assertTrue(varId.getTypeMirror().getSymbol().isUnresolved());

        // check the type of the variable initializer's expression
        ASTExpression initExpression = varId.getInitializer();
        assertTrue("type should be ArrayList", TypeHelper.symbolEquals(ArrayList.class, initExpression.getTypeMirror()));
    }

    @Test
    public void testLocalVarInferenceCanBeParsedJava10() {
        ASTCompilationUnit compilationUnit = java10.parseResource("LocalVariableTypeInference.java");
        List<ASTLocalVariableDeclaration> localVars = compilationUnit.findDescendantsOfType(ASTLocalVariableDeclaration.class);
        assertEquals(3, localVars.size());

        TypeSystem ts = compilationUnit.getTypeSystem();
        JClassType stringT = (JClassType) ts.typeOf(ts.getClassSymbol(String.class), false);

        // first: var list = new ArrayList<String>();
        assertNull(localVars.get(0).getTypeNode());
        ASTVariableDeclaratorId varDecl = localVars.get(0).getVarIds().firstOrThrow();
        assertEquals("type should be ArrayList<String>", ts.parameterise(ts.getClassSymbol(ArrayList.class), listOf(stringT)), varDecl.getTypeMirror());

        // second: var stream = list.stream();
        assertNull(localVars.get(1).getTypeNode());
        ASTVariableDeclaratorId varDecl2 = localVars.get(1).getVarIds().firstOrThrow();
        // TODO: return type of method call is unknown
        assertEquals("type should be Stream<String>",
                     ts.parameterise(ts.getClassSymbol(Stream.class), listOf(stringT)),
                     varDecl2.getTypeMirror());

        // third: var s = "Java 10";
        assertNull(localVars.get(2).getTypeNode());
        ASTVariableDeclaratorId varDecl3 = localVars.get(2).getVarIds().firstOrThrow();
        assertEquals("type should be String", stringT, varDecl3.getTypeMirror());
    }

    @Test
    public void testForLoopWithVar() {
        List<ASTLocalVariableDeclaration> localVars = java10.parseResource("LocalVariableTypeInferenceForLoop.java")
                                                            .findDescendantsOfType(ASTLocalVariableDeclaration.class);
        assertEquals(1, localVars.size());

        assertNull(localVars.get(0).getTypeNode());
        ASTVariableDeclaratorId varDecl = localVars.get(0).getVarIds().firstOrThrow();
        assertSame("type should be int", varDecl.getTypeSystem().INT, varDecl.getTypeMirror());
    }

    @Test
    public void testForLoopEnhancedWithVar() {
        List<ASTLocalVariableDeclaration> localVars = java10.parseResource("LocalVariableTypeInferenceForLoopEnhanced.java")
                                                            .findDescendantsOfType(ASTLocalVariableDeclaration.class);
        assertEquals(1, localVars.size());

        assertNull(localVars.get(0).getTypeNode());
        ASTVariableDeclaratorId varDecl = localVars.get(0).getVarIds().firstOrThrow();
        assertTrue("type should be String", TypeHelper.symbolEquals(String.class, varDecl.getTypeMirror()));
    }

    @Test
    public void testForLoopEnhancedWithVar2() {
        List<ASTLocalVariableDeclaration> localVars = java10.parseResource("LocalVariableTypeInferenceForLoopEnhanced2.java")
                                                            .findDescendantsOfType(ASTLocalVariableDeclaration.class);
        assertEquals(4, localVars.size());

        assertNull(localVars.get(1).getTypeNode());
        @NonNull ASTVariableDeclaratorId varDecl2 = localVars.get(1).getVarIds().firstOrThrow();
        assertTrue("type should be String", TypeHelper.symbolEquals(String.class, varDecl2.getTypeMirror()));

        assertNull(localVars.get(3).getTypeNode());
        ASTVariableDeclaratorId varDecl4 = localVars.get(3).getVarIds().firstOrThrow();
        assertSame("type should be int", varDecl2.getTypeSystem().INT, varDecl4.getTypeMirror());
    }

    @Test
    public void testTryWithResourcesWithVar() {
        List<ASTResource> resources = java10.parseResource("LocalVariableTypeInferenceTryWithResources.java")
                                            .findDescendantsOfType(ASTResource.class);
        assertEquals(1, resources.size());

        assertNull(resources.get(0).asLocalVariableDeclaration().getTypeNode());
        ASTVariableDeclaratorId varId = resources.get(0).asLocalVariableDeclaration().getVarIds().firstOrThrow();
        assertTrue("type should be FileInputStream", TypeHelper.symbolEquals(FileInputStream.class, varId.getTypeMirror()));
    }

    @Test
    public void testTypeResNullPointer() {
        java10.parseResource("LocalVariableTypeInference_typeres.java");
    }

}
