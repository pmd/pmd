/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.rule.xpath.internal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.reporting.Report;

class KotlinModifiersFunctionTest extends BaseKotlinXPathFunctionTest {

    @Test
    void suspendFunctionMatches() {
        Report r = runXPath("//FunctionDeclaration[pmd-kotlin:modifiers() = 'suspend']",
                "class C { suspend fun foo() {} }");
        assertFalse(r.getViolations().isEmpty(), "suspend function should match");
    }

    @Test
    void nonSuspendFunctionDoesNotMatch() {
        Report r = runXPath("//FunctionDeclaration[pmd-kotlin:modifiers() = 'suspend']",
                "class C { fun bar() {} }");
        assertTrue(r.getViolations().isEmpty(), "non-suspend function should not match");
    }

    @Test
    void dataClassMatches() {
        Report r = runXPath("//ClassDeclaration[pmd-kotlin:modifiers() = 'data']",
                "data class Point(val x: Int, val y: Int)");
        assertFalse(r.getViolations().isEmpty(), "data class should match");
    }

    @Test
    void multipleModifiersMatch() {
        Report r = runXPath("//ClassDeclaration[pmd-kotlin:modifiers() = ('internal', 'abstract')]",
                "internal abstract class Base");
        assertFalse(r.getViolations().isEmpty(), "internal abstract class should match both");
    }

    @Test
    void overrideFunctionMatches() {
        Report r = runXPath("//FunctionDeclaration[pmd-kotlin:modifiers() = 'override']",
                "class C : Base() { override fun foo() {} }");
        assertFalse(r.getViolations().isEmpty(), "override function should match");
    }

    @Test
    void internalPropertyMatches() {
        Report r = runXPath("//PropertyDeclaration[pmd-kotlin:modifiers() = 'internal']",
                "class C { internal val x: Int = 0 }");
        assertFalse(r.getViolations().isEmpty(), "internal property should match");
    }

    @Test
    void noModifiersReturnsEmpty() {
        Report r = runXPath("//ClassDeclaration[pmd-kotlin:modifiers() = 'abstract']",
                "class Plain");
        assertTrue(r.getViolations().isEmpty(), "plain class should have no modifiers");
    }
}
