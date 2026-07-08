/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.ast.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtAssignment;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtFunctionBody;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtFunctionDeclaration;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtKotlinFile;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtLambdaLiteral;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtPrimaryExpression;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtSimpleIdentifier;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParsingHelper;

class KotlinAstUtilTest {

    @Test
    void textOfSimpleIdentifierReturnsName() {
        KtKotlinFile file = KotlinParsingHelper.DEFAULT.parse("val foo = 1");
        KtSimpleIdentifier si =
                file.descendants(KtSimpleIdentifier.class).first();
        assertEquals("foo", KotlinAstUtil.textOf(si));
    }

    @Test
    void textOfSimpleIdentifierReturnsNullForNull() {
        assertNull(KotlinAstUtil.textOf((KtSimpleIdentifier) null));
    }

    @Test
    void textOfPrimaryExpressionReturnsIdentifier() {
        KtKotlinFile file = KotlinParsingHelper.DEFAULT.parse("val x = bar");
        KtPrimaryExpression pe =
                file.descendants(KtPrimaryExpression.class).first();
        assertEquals("bar", KotlinAstUtil.textOf(pe));
    }

    @Test
    void collectParamNamesReturnsParameterNames() {
        KtKotlinFile file =
                KotlinParsingHelper.DEFAULT.parse("fun greet(name: String, age: Int) {}");
        KtFunctionDeclaration func =
                file.descendants(KtFunctionDeclaration.class).first();
        Set<String> params = KotlinAstUtil.collectParamNames(func);
        assertEquals(2, params.size());
        assertTrue(params.contains("name"));
        assertTrue(params.contains("age"));
    }

    @Test
    void collectParamNamesReturnsEmptyForNoParams() {
        KtKotlinFile file = KotlinParsingHelper.DEFAULT.parse("fun noArgs() {}");
        KtFunctionDeclaration func =
                file.descendants(KtFunctionDeclaration.class).first();
        assertTrue(KotlinAstUtil.collectParamNames(func).isEmpty());
    }

    @Test
    void collectLocalVarNamesFindsLocals() {
        KtKotlinFile file = KotlinParsingHelper.DEFAULT.parse(
                "fun work() { val result = 1; val total = 2 }");
        KtFunctionDeclaration func =
                file.descendants(KtFunctionDeclaration.class).first();
        KtFunctionBody body = func.functionBody();
        Set<String> locals = KotlinAstUtil.collectLocalVarNames(body);
        assertTrue(locals.contains("result"));
        assertTrue(locals.contains("total"));
    }

    @Test
    void isWithinReturnsTrueForDirectDescendant() {
        KtKotlinFile file = KotlinParsingHelper.DEFAULT.parse(
                "fun outer() { fun inner() {} }");
        KtFunctionDeclaration outer =
                file.descendants(KtFunctionDeclaration.class).first();
        KtFunctionDeclaration inner =
                file.descendants(KtFunctionDeclaration.class).get(1);
        assertTrue(KotlinAstUtil.isWithin(inner, KtFunctionDeclaration.class, outer));
        assertFalse(KotlinAstUtil.isWithin(inner, KtFunctionDeclaration.class, inner));
    }

    @Test
    void collectLambdaParamNamesReturnsExplicitParams() {
        KtKotlinFile file = KotlinParsingHelper.DEFAULT.parse(
                "fun foo() { listOf(1).forEach { item -> println(item) } }");
        KtLambdaLiteral lambda = file.descendants(KtLambdaLiteral.class).first();
        Set<String> params = KotlinAstUtil.collectLambdaParamNames(lambda);
        assertEquals(1, params.size());
        assertTrue(params.contains("item"));
    }

    @Test
    void collectClassVarFieldNamesReturnsOnlyVarFields() {
        KtKotlinFile file = KotlinParsingHelper.DEFAULT.parse(
                "class Foo { val id: Int = 0; var name: String = \"\" }");
        // use any node inside the class to look up class fields
        KtSimpleIdentifier si = file.descendants(KtSimpleIdentifier.class).first();
        Set<String> fields = KotlinAstUtil.collectClassVarFieldNames(si);
        assertFalse(fields.contains("id"), "val field should not be included");
        assertTrue(fields.contains("name"), "var field should be included");
    }

    @Test
    void getLhsVarNameReturnsSimpleIdentifier() {
        KtKotlinFile file = KotlinParsingHelper.DEFAULT.parse(
                "fun foo() { var x = 0; x = 1 }");
        KtAssignment assignment = file.descendants(KtAssignment.class).first();
        assertEquals("x", KotlinAstUtil.getLhsVarName(assignment));
    }
}
