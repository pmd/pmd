/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtFunctionBody;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtFunctionDeclaration;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtKotlinFile;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtPrimaryExpression;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtSimpleIdentifier;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtType;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParsingHelper;

class KotlinAstUtilTest {

    @Test
    void getIdentifierTextReturnsName() {
        KtKotlinFile file = KotlinParsingHelper.DEFAULT.parse("val foo = 1");
        KtSimpleIdentifier si =
                file.descendants(KtSimpleIdentifier.class).first();
        assertEquals("foo", KotlinAstUtil.getIdentifierText(si));
    }

    @Test
    void getIdentifierTextReturnsNullForNull() {
        assertNull(KotlinAstUtil.getIdentifierText(null));
    }

    @Test
    void getPrimaryExpressionTextReturnsIdentifier() {
        KtKotlinFile file = KotlinParsingHelper.DEFAULT.parse("val x = bar");
        KtPrimaryExpression pe =
                file.descendants(KtPrimaryExpression.class).first();
        assertEquals("bar", KotlinAstUtil.getPrimaryExpressionText(pe));
    }

    @Test
    void typeContainsNameReturnsTrueForMatch() {
        KtKotlinFile file = KotlinParsingHelper.DEFAULT.parse("val x: String = \"\"");
        KtType type = file.descendants(KtType.class).first();
        assertTrue(KotlinAstUtil.typeContainsName(type, "String"));
    }

    @Test
    void typeContainsNameReturnsFalseForMismatch() {
        KtKotlinFile file = KotlinParsingHelper.DEFAULT.parse("val x: Int = 0");
        KtType type = file.descendants(KtType.class).first();
        assertFalse(KotlinAstUtil.typeContainsName(type, "String"));
    }

    @Test
    void collectAllParamNamesReturnsParameterNames() {
        KtKotlinFile file =
                KotlinParsingHelper.DEFAULT.parse("fun greet(name: String, age: Int) {}");
        KtFunctionDeclaration func =
                file.descendants(KtFunctionDeclaration.class).first();
        Set<String> params = KotlinAstUtil.collectAllParamNames(func);
        assertEquals(2, params.size());
        assertTrue(params.contains("name"));
        assertTrue(params.contains("age"));
    }

    @Test
    void collectAllParamNamesReturnsEmptyForNoParams() {
        KtKotlinFile file = KotlinParsingHelper.DEFAULT.parse("fun noArgs() {}");
        KtFunctionDeclaration func =
                file.descendants(KtFunctionDeclaration.class).first();
        assertTrue(KotlinAstUtil.collectAllParamNames(func).isEmpty());
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
}
