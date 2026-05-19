/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.rule.xpath.internal;

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

class KotlinInsideLoopFunctionTest {

    @Test
    void forLoopMatches() {
        Report r = run("//PropertyDeclaration[pmd-kotlin:insideLoop()]",
                "fun f(items: List<String>) { for (x in items) { val sb = StringBuilder() } }");
        assertEquals(1, r.getViolations().size(), "property inside for loop should match");
    }

    @Test
    void whileLoopMatches() {
        Report r = run("//PropertyDeclaration[pmd-kotlin:insideLoop()]",
                "fun f() { var i = 0; while (i < 10) { val s = \"x\"; i++ } }");
        assertEquals(1, r.getViolations().size(), "property inside while loop should match");
    }

    @Test
    void doWhileLoopMatches() {
        Report r = run("//PropertyDeclaration[pmd-kotlin:insideLoop()]",
                "fun f() { do { val s = \"x\" } while (false) }");
        assertEquals(1, r.getViolations().size(), "property inside do-while loop should match");
    }

    @Test
    void outsideLoopDoesNotMatch() {
        Report r = run("//PropertyDeclaration[pmd-kotlin:insideLoop()]",
                "fun f() { val sb = StringBuilder() }");
        assertTrue(r.getViolations().isEmpty(), "property outside any loop should not match");
    }

    @Test
    void nestedLoopMatches() {
        Report r = run("//PropertyDeclaration[pmd-kotlin:insideLoop()]",
                "fun f(m: Array<IntArray>) { for (i in m.indices) { for (j in m[i].indices) { val x = 0 } } }");
        assertEquals(1, r.getViolations().size(), "property in inner loop should match once");
    }

    @Test
    void assignmentInsideForLoopMatches() {
        Report r = run("//Assignment[pmd-kotlin:insideLoop()]",
                "fun f(items: List<String>) { var s = \"\"; for (x in items) { s += x } }");
        assertEquals(1, r.getViolations().size(), "assignment inside for loop should match");
    }

    @Test
    void assignmentOutsideLoopDoesNotMatch() {
        Report r = run("//Assignment[pmd-kotlin:insideLoop()]",
                "fun f(a: String, b: String): String { var s = \"\"; s += a; s += b; return s }");
        assertTrue(r.getViolations().isEmpty(), "assignments outside loop should not match");
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
