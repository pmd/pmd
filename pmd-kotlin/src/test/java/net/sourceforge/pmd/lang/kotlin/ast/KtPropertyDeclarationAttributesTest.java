/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

class KtPropertyDeclarationAttributesTest {

    @Test
    void varPropertyIsMutable() {
        Report r = run("//PropertyDeclaration[@Mutable = true()]",
                "class C { var x: Int = 0 }");
        assertEquals(1, r.getViolations().size(), "var property should have @Mutable = true");
    }

    @Test
    void valPropertyIsNotMutable() {
        Report r = run("//PropertyDeclaration[@Mutable = false()]",
                "class C { val x: Int = 0 }");
        assertEquals(1, r.getViolations().size(), "val property should have @Mutable = false");
    }

    @Test
    void valDoesNotMatchMutableTrue() {
        Report r = run("//PropertyDeclaration[@Mutable = true()]",
                "class C { val x: Int = 0 }");
        assertTrue(r.getViolations().isEmpty(), "val should not match @Mutable = true");
    }

    @Test
    void varDoesNotMatchMutableFalse() {
        Report r = run("//PropertyDeclaration[@Mutable = false()]",
                "class C { var x: Int = 0 }");
        assertTrue(r.getViolations().isEmpty(), "var should not match @Mutable = false");
    }

    @Test
    void multipleMixedProperties() {
        Report r = run("//PropertyDeclaration[@Mutable = true()]",
                "class C { val a: Int = 0; var b: Int = 1; val c: String = \"\"; var d: String = \"x\" }");
        assertEquals(2, r.getViolations().size(), "only var properties should match @Mutable = true");
    }

    @Test
    void topLevelVarIsMutable() {
        Report r = run("//PropertyDeclaration[@Mutable = true()]",
                "var topLevel: String = \"hello\"");
        assertEquals(1, r.getViolations().size(), "top-level var should have @Mutable = true");
    }

    @Test
    void topLevelValIsNotMutable() {
        Report r = run("//PropertyDeclaration[@Mutable = false()]",
                "val topLevel: String = \"hello\"");
        assertEquals(1, r.getViolations().size(), "top-level val should have @Mutable = false");
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
