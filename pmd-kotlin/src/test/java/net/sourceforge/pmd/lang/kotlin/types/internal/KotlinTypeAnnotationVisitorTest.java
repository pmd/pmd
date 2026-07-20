/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.types.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtCatchBlock;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtClassDeclaration;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtClassParameter;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtDelegationSpecifier;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtForStatement;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtFunctionDeclaration;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtFunctionValueParameter;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtKotlinFile;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtPropertyDeclaration;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtUnescapedAnnotation;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParsingHelper;
import net.sourceforge.pmd.lang.kotlin.types.KotlinNodeTypeData;

/**
 * Tests that KotlinTypeAnnotationVisitor correctly annotates AST nodes with
 * type data during the parse step driven by KotlinLanguageProcessor.
 * Uses KotlinParsingHelper (which goes through KotlinLanguageProcessor)
 * to match production behaviour as closely as possible.
 */
class KotlinTypeAnnotationVisitorTest {

    private static final KotlinParsingHelper PARSER = KotlinParsingHelper.DEFAULT;

    // --- PropertyDeclaration ---

    @Test
    void propertyTypeNameMatchesKotlinType() {
        KtKotlinFile root = PARSER.parse("val x: String = \"hello\"");
        KtPropertyDeclaration prop = root.descendants(KtPropertyDeclaration.class).first();
        assertEquals("kotlin.String", KotlinNodeTypeData.getTypeName(prop));
    }

    // --- FunctionDeclaration ---

    @Test
    void returnTypeNameMatchesKotlinType() {
        KtKotlinFile root = PARSER.parse("fun count(): Int = 42");
        KtFunctionDeclaration fn = root.descendants(KtFunctionDeclaration.class).first();
        assertEquals("kotlin.Int", KotlinNodeTypeData.getReturnTypeName(fn));
    }

    // --- CatchBlock ---

    @Test
    void catchBlockTypeNameSet() {
        KtKotlinFile root = PARSER.parse(
                "fun f() { try { } catch (e: IllegalArgumentException) { } }");
        KtCatchBlock catchBlock = root.descendants(KtCatchBlock.class).first();
        assertNotNull(catchBlock);
        assertEquals("java.lang.IllegalArgumentException", KotlinNodeTypeData.getTypeName(catchBlock));
    }

    // --- ForStatement ---

    @Test
    void forStatementLoopVariableTypeNameSet() {
        KtKotlinFile root = PARSER.parse(
                "fun f(items: List<String>) { for (item in items) { } }");
        KtForStatement forStmt = root.descendants(KtForStatement.class).first();
        assertNotNull(forStmt);
        assertEquals("kotlin.String", KotlinNodeTypeData.getTypeName(forStmt));
    }

    @Test
    void functionParameterTypeNameMatchesListType() {
        KtKotlinFile root = PARSER.parse(
                "fun f(items: List<String>) { for (item in items) { } }");
        KtFunctionValueParameter param = root.descendants(KtFunctionValueParameter.class).first();
        assertNotNull(param);
        assertEquals("kotlin.collections.List<kotlin.String>", KotlinNodeTypeData.getTypeName(param));
    }

    // --- ClassParameter (primary constructor val/var) ---

    @Test
    void classParameterTypeNameSet() {
        KtKotlinFile root = PARSER.parse("class Foo(val name: String)");
        KtClassParameter param = root.descendants(KtClassParameter.class).first();
        assertNotNull(param);
        assertEquals("kotlin.String", KotlinNodeTypeData.getTypeName(param));
    }

    // --- ClassDeclaration ---

    @Test
    void classDeclarationTypeNameSetToSimpleName() {
        // No package → simple name only (no FQN)
        KtKotlinFile root = PARSER.parse("class MyService");
        KtClassDeclaration clazz = root.descendants(KtClassDeclaration.class).first();
        assertNotNull(clazz);
        assertEquals("MyService", KotlinNodeTypeData.getTypeName(clazz));
    }

    @Test
    void classDeclarationTypeNameSetToFqnWhenPackageDeclared() {
        KtKotlinFile root = PARSER.parse("package com.example\nclass MyService");
        KtClassDeclaration clazz = root.descendants(KtClassDeclaration.class).first();
        assertNotNull(clazz);
        assertEquals("com.example.MyService", KotlinNodeTypeData.getTypeName(clazz));
    }

    // --- DelegationSpecifier (extends / implements) ---

    @Test
    void delegationSpecifierTypeNameSetForSuperclass() {
        KtKotlinFile root = PARSER.parse("class Foo : Exception(\"msg\")");
        KtDelegationSpecifier spec = root.descendants(KtDelegationSpecifier.class).first();
        assertNotNull(spec);
        assertEquals("java.lang.Exception", KotlinNodeTypeData.getTypeName(spec));
    }

    @Test
    void delegationSpecifierTypeNameSetForInterface() {
        KtKotlinFile root = PARSER.parse("class Foo : java.io.Serializable");
        KtDelegationSpecifier spec = root.descendants(KtDelegationSpecifier.class).first();
        assertNotNull(spec);
        assertEquals("java.io.Serializable", KotlinNodeTypeData.getTypeName(spec));
    }

    // --- Annotation FQN ---

    @Test
    void annotationFqNamesSetOnFunctionDeclaration() {
        KtKotlinFile root = PARSER.parse("@Deprecated(\"use bar\") fun foo() {}");
        KtFunctionDeclaration fn = root.descendants(KtFunctionDeclaration.class).first();
        assertNotNull(fn);
        List<String> annotations = KotlinNodeTypeData.getAnnotationFqNames(fn);
        assertNotNull(annotations);
        assertEquals("kotlin.Deprecated", String.join(",", annotations));
    }

    // --- Multiline and complex constructs: line-number assertions prove ktm and PMD agree ---

    @Test
    void multipleAnnotationsOnSeparateLinesSetOnFunction() {
        // @Deprecated on line 1, @Suppress on line 2, fun on line 3.
        // ktm docs: "for annotated declarations, line = annotation line" → line 1.
        // PMD: KtFunctionDeclaration begins at the first token → line 1.
        // Type must be set without needing the +/-1 fallback.
        KtKotlinFile root = PARSER.parse(
                "@Deprecated(\"use bar\")\n"
                + "@Suppress(\"unused\")\n"
                + "fun foo(): String = \"x\"");
        KtFunctionDeclaration fn = root.descendants(KtFunctionDeclaration.class).first();
        assertNotNull(fn);
        assertEquals(1, fn.getBeginLine(), "PMD node must start on annotation line 1");
        assertEquals("kotlin.String", KotlinNodeTypeData.getReturnTypeName(fn));
        List<String> annotations = KotlinNodeTypeData.getAnnotationFqNames(fn);
        assertNotNull(annotations);
        assertEquals(2, annotations.size());
    }

    @Test
    void multilineFunctionDeclarationReturnTypeSet() {
        // fun keyword on line 1; params on lines 2-3; return type on line 4.
        // Both PMD and ktm should report line 1 for the declaration.
        KtKotlinFile root = PARSER.parse(
                "fun longName(\n"
                + "    param1: String,\n"
                + "    param2: Int\n"
                + "): List<String> = listOf(param1)");
        KtFunctionDeclaration fn = root.descendants(KtFunctionDeclaration.class).first();
        assertNotNull(fn);
        assertEquals(1, fn.getBeginLine(), "PMD node must start on line 1 (fun keyword)");
        assertEquals("kotlin.collections.List<kotlin.String>", KotlinNodeTypeData.getReturnTypeName(fn));
    }

    @Test
    void propertyWithLeadingBlankLinesTypeSet() {
        // val on line 3 (after 2 blank lines); both PMD and ktm must agree on line 3.
        KtKotlinFile root = PARSER.parse(
                "\n"
                + "\n"
                + "val x: Int = 42");
        KtPropertyDeclaration prop = root.descendants(KtPropertyDeclaration.class).first();
        assertNotNull(prop);
        assertEquals(3, prop.getBeginLine(), "PMD node must start on line 3");
        assertEquals("kotlin.Int", KotlinNodeTypeData.getTypeName(prop));
    }

    @Test
    void lambdaParameterTypeSetInForEach() {
        // Function parameter on line 1; lambda body on lines 2-4.
        // KtFunctionValueParameter for 'items' is on line 1 in both PMD and ktm.
        KtKotlinFile root = PARSER.parse(
                "fun f(items: List<String>) {\n"
                + "    items.forEach { item ->\n"
                + "        println(item)\n"
                + "    }\n"
                + "}");
        KtFunctionValueParameter param = root.descendants(KtFunctionValueParameter.class).first();
        assertNotNull(param);
        assertEquals(1, param.getBeginLine(), "PMD node must start on line 1");
        assertEquals("kotlin.collections.List<kotlin.String>", KotlinNodeTypeData.getTypeName(param));
    }

    @Test
    void chainedCallPropertyTypeSet() {
        // val on line 1; chained .filter on line 2; .first() on line 3.
        // KtPropertyDeclaration starts on line 1 in both PMD and ktm.
        KtKotlinFile root = PARSER.parse(
                "val result: String = listOf(\"a\", \"b\")\n"
                + "    .filter { it.isNotEmpty() }\n"
                + "    .first()");
        KtPropertyDeclaration prop = root.descendants(KtPropertyDeclaration.class).first();
        assertNotNull(prop);
        assertEquals(1, prop.getBeginLine(), "PMD node must start on line 1");
        assertEquals("kotlin.String", KotlinNodeTypeData.getTypeName(prop));
    }

    // --- Regression: #6891 KtModifiers vs KtModifier in collectAnnotationNodes ---

    @Test
    void unescapedAnnotationOnFunctionHasTypeName() {
        KtKotlinFile root = PARSER.parse("@Deprecated(\"use X\") fun foo(): String = \"\"");
        KtUnescapedAnnotation ann = root.descendants(KtUnescapedAnnotation.class).first();
        assertNotNull(ann);
        assertEquals("kotlin.Deprecated", KotlinNodeTypeData.getTypeName(ann));
    }
}
