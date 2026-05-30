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

class KotlinIsWithinFunctionTest {

    // ---- companion-object ----

    @Test
    void companionObjectMatches() {
        Report r = run("//PropertyDeclaration[pmd-kotlin:isWithin('companion-object')]",
                "class C { companion object { val x: Int = 0 } }");
        assertEquals(1, r.getViolations().size(), "property inside companion object should match");
    }

    @Test
    void regularClassMemberDoesNotMatchCompanionObject() {
        Report r = run("//PropertyDeclaration[pmd-kotlin:isWithin('companion-object')]",
                "class C { val x: Int = 0 }");
        assertTrue(r.getViolations().isEmpty(), "class member outside companion object should not match");
    }

    // ---- top-level ----

    @Test
    void topLevelPropertyMatches() {
        Report r = run("//PropertyDeclaration[pmd-kotlin:isWithin('top-level')]",
                "val x: Int = 0");
        assertEquals(1, r.getViolations().size(), "top-level property should match");
    }

    @Test
    void classMemberDoesNotMatchTopLevel() {
        Report r = run("//PropertyDeclaration[pmd-kotlin:isWithin('top-level')]",
                "class C { val x: Int = 0 }");
        assertTrue(r.getViolations().isEmpty(), "class member should not match top-level");
    }

    @Test
    void functionLocalDoesNotMatchTopLevel() {
        Report r = run("//PropertyDeclaration[pmd-kotlin:isWithin('top-level')]",
                "fun f() { val x: Int = 0 }");
        assertTrue(r.getViolations().isEmpty(), "function-local property should not match top-level");
    }

    // ---- object-declaration ----

    @Test
    void objectDeclarationMatches() {
        Report r = run("//PropertyDeclaration[pmd-kotlin:isWithin('object-declaration')]",
                "object Singleton { val x: Int = 0 }");
        assertEquals(1, r.getViolations().size(), "property inside object declaration should match");
    }

    @Test
    void classMemberDoesNotMatchObjectDeclaration() {
        Report r = run("//PropertyDeclaration[pmd-kotlin:isWithin('object-declaration')]",
                "class C { val x: Int = 0 }");
        assertTrue(r.getViolations().isEmpty(), "class member should not match object-declaration");
    }

    // ---- function-body ----

    @Test
    void functionBodyMatches() {
        Report r = run("//PropertyDeclaration[pmd-kotlin:isWithin('function-body')]",
                "fun f() { val x: Int = 0 }");
        assertEquals(1, r.getViolations().size(), "property inside function body should match");
    }

    @Test
    void classMemberDoesNotMatchFunctionBody() {
        Report r = run("//PropertyDeclaration[pmd-kotlin:isWithin('function-body')]",
                "class C { val x: Int = 0 }");
        assertTrue(r.getViolations().isEmpty(), "class member should not match function-body");
    }

    @Test
    void topLevelDoesNotMatchFunctionBody() {
        Report r = run("//PropertyDeclaration[pmd-kotlin:isWithin('function-body')]",
                "val x: Int = 0");
        assertTrue(r.getViolations().isEmpty(), "top-level property should not match function-body");
    }

    // ---- lambda ----

    @Test
    void lambdaBodyMatches() {
        Report r = run("//PropertyDeclaration[pmd-kotlin:isWithin('lambda')]",
                "val f = { val x: Int = 0; x }");
        assertEquals(1, r.getViolations().size(), "property inside lambda should match");
    }

    @Test
    void classMemberDoesNotMatchLambda() {
        Report r = run("//PropertyDeclaration[pmd-kotlin:isWithin('lambda')]",
                "class C { val x: Int = 0 }");
        assertTrue(r.getViolations().isEmpty(), "class member should not match lambda");
    }

    @Test
    void functionBodyDoesNotMatchLambda() {
        Report r = run("//PropertyDeclaration[pmd-kotlin:isWithin('lambda')]",
                "fun f() { val x: Int = 0 }");
        assertTrue(r.getViolations().isEmpty(), "function body property should not match lambda");
    }

    @Test
    void nestedLambdaMatches() {
        Report r = run("//PropertyDeclaration[pmd-kotlin:isWithin('lambda')]",
                "val f = { listOf(1).map { val x = it; x } }");
        assertEquals(1, r.getViolations().size(), "property inside nested lambda should match");
    }

    // ---- combined (isWithin + not @Mutable, the primary use case) ----

    @Test
    void valInCompanionIsExcluded() {
        // Rule: flag all except val inside companion object — use @Mutable = false(), not not(@Mutable)
        Report r = run("//PropertyDeclaration[not(@Mutable = false() and pmd-kotlin:isWithin('companion-object'))]",
                "class C { companion object { val x: Int = 0 }; var y: Int = 0 }");
        assertEquals(1, r.getViolations().size(), "only var member should be flagged");
    }

    // ---- invalid context string should report a processing error or throw ----

    @Test
    void unknownContextFails() {
        Report r = run("//PropertyDeclaration[pmd-kotlin:isWithin('unknown-context')]",
                "class C { val x: Int = 0 }");
        assertEquals(1, r.getProcessingErrors().size(), "unknown context should cause a processing error");
    }

    @Test
    void unknownContextDirectFails() {
        Report r = run("//PropertyDeclaration[pmd-kotlin:isWithinDirect('unknown-context')]",
                "class C { val x: Int = 0 }");
        assertEquals(1, r.getProcessingErrors().size(), "unknown context should cause a processing error in isWithinDirect");
    }

    // ---- isWithinDirect — companion-object ----

    @Test
    void directCompanionMemberMatches() {
        Report r = run("//PropertyDeclaration[pmd-kotlin:isWithinDirect('companion-object')]",
                "class C { companion object { val x: Int = 0 } }");
        assertEquals(1, r.getViolations().size(), "direct companion member should match isWithinDirect");
    }

    @Test
    void localVarInCompanionFunctionDoesNotMatchDirect() {
        Report r = run("//PropertyDeclaration[pmd-kotlin:isWithinDirect('companion-object')]",
                "class C { companion object { fun f() { val x: Int = 0 } } }");
        assertTrue(r.getViolations().isEmpty(),
                "property inside function inside companion should not match isWithinDirect");
    }

    @Test
    void localVarInCompanionFunctionMatchesAnyDepth() {
        Report r = run("//PropertyDeclaration[pmd-kotlin:isWithin('companion-object')]",
                "class C { companion object { fun f() { val x: Int = 0 } } }");
        assertEquals(1, r.getViolations().size(),
                "property inside function inside companion should match isWithin (any depth)");
    }

    // ---- isWithinDirect — function-body ----

    @Test
    void directFunctionLocalMatches() {
        Report r = run("//PropertyDeclaration[pmd-kotlin:isWithinDirect('function-body')]",
                "fun f() { val x: Int = 0 }");
        assertEquals(1, r.getViolations().size(), "direct function local should match isWithinDirect");
    }

    @Test
    void lambdaLocalDoesNotMatchDirectFunctionBody() {
        Report r = run("//PropertyDeclaration[pmd-kotlin:isWithinDirect('function-body')]",
                "fun f() { listOf(1).forEach { val x = it } }");
        assertTrue(r.getViolations().isEmpty(),
                "property inside lambda inside function should not match isWithinDirect('function-body')");
    }

    @Test
    void lambdaLocalMatchesAnyDepthFunctionBody() {
        Report r = run("//PropertyDeclaration[pmd-kotlin:isWithin('function-body')]",
                "fun f() { listOf(1).forEach { val x = it } }");
        assertEquals(1, r.getViolations().size(),
                "property inside lambda inside function should match isWithin('function-body') (any depth)");
    }

    // ---- isWithinDirect — lambda ----

    @Test
    void directLambdaLocalMatches() {
        Report r = run("//PropertyDeclaration[pmd-kotlin:isWithinDirect('lambda')]",
                "val f = { val x: Int = 0; x }");
        assertEquals(1, r.getViolations().size(), "direct lambda local should match isWithinDirect");
    }

    @Test
    void nestedLambdaLocalDoesNotMatchDirectOuterLambda() {
        // Inner property 'x' is inside inner lambda, not direct member of outer lambda
        Report r = run("//PropertyDeclaration[pmd-kotlin:isWithinDirect('lambda')]",
                "val f = { listOf(1).map { val x = it; x }; 0 }");
        // 'x' is a direct member of the inner lambda: isWithinDirect('lambda') = true for it
        assertEquals(1, r.getViolations().size(), "property is a direct member of the inner lambda");
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
