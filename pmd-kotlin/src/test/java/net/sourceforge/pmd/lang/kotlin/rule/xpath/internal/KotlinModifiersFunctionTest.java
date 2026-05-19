/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.rule.xpath.internal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.PmdAnalysis;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.document.FileId;
import net.sourceforge.pmd.lang.rule.RuleSet;
import net.sourceforge.pmd.lang.rule.xpath.XPathRule;
import net.sourceforge.pmd.lang.rule.xpath.XPathVersion;
import net.sourceforge.pmd.reporting.Report;

class KotlinModifiersFunctionTest {

    @Test
    void suspendFunctionMatches() {
        Report r = run("//FunctionDeclaration[pmd-kotlin:modifiers() = 'suspend']",
                "class C { suspend fun foo() {} }");
        assertFalse(r.getViolations().isEmpty(), "suspend function should match");
    }

    @Test
    void nonSuspendFunctionDoesNotMatch() {
        Report r = run("//FunctionDeclaration[pmd-kotlin:modifiers() = 'suspend']",
                "class C { fun bar() {} }");
        assertTrue(r.getViolations().isEmpty(), "non-suspend function should not match");
    }

    @Test
    void dataClassMatches() {
        Report r = run("//ClassDeclaration[pmd-kotlin:modifiers() = 'data']",
                "data class Point(val x: Int, val y: Int)");
        assertFalse(r.getViolations().isEmpty(), "data class should match");
    }

    @Test
    void multipleModifiersMatch() {
        // internal + abstract class
        Report r = run("//ClassDeclaration[pmd-kotlin:modifiers() = ('internal', 'abstract')]",
                "internal abstract class Base");
        assertFalse(r.getViolations().isEmpty(), "internal abstract class should match both");
    }

    @Test
    void overrideFunctionMatches() {
        Report r = run("//FunctionDeclaration[pmd-kotlin:modifiers() = 'override']",
                "class C : Base() { override fun foo() {} }");
        assertFalse(r.getViolations().isEmpty(), "override function should match");
    }

    @Test
    void internalPropertyMatches() {
        Report r = run("//PropertyDeclaration[pmd-kotlin:modifiers() = 'internal']",
                "class C { internal val x: Int = 0 }");
        assertFalse(r.getViolations().isEmpty(), "internal property should match");
    }

    @Test
    void noModifiersReturnsEmpty() {
        // plain class, no modifiers -> modifiers() = 'abstract' should not fire
        Report r = run("//ClassDeclaration[pmd-kotlin:modifiers() = 'abstract']",
                "class Plain");
        assertTrue(r.getViolations().isEmpty(), "plain class should have no modifiers");
    }

    // -------------------------------------------------------------------------

    private Report run(String xpath, String code) {
        PMDConfiguration config = new PMDConfiguration();
        config.setIgnoreIncrementalAnalysis(true);
        config.setDefaultLanguageVersion(
                LanguageRegistry.PMD.getLanguageById("kotlin").getDefaultVersion());
        XPathRule rule = new XPathRule(XPathVersion.DEFAULT, xpath);
        rule.setLanguage(LanguageRegistry.PMD.getLanguageById("kotlin"));
        rule.setMessage("hit");
        rule.setName("R" + Math.abs(xpath.hashCode() % 99999));
        try (PmdAnalysis pmd = PmdAnalysis.create(config)) {
            pmd.addRuleSet(RuleSet.forSingleRule(rule));
            pmd.files().addSourceFile(FileId.fromPathLikeString("snippet.kt"), code);
            return pmd.performAnalysisAndCollectReport();
        }
    }
}
