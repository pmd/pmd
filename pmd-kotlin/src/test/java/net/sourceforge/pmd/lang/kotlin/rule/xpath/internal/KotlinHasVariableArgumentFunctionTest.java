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

class KotlinHasVariableArgumentFunctionTest {

    private static final String XPATH = "//PostfixUnaryExpression[pmd-kotlin:hasVariableArgument()]";

    // ---- function parameter as argument ----

    @Test
    void functionParamArgMatches() {
        Report r = run(XPATH,
                "fun f(pattern: String) { DateTimeFormatter.ofPattern(pattern) }");
        assertEquals(1, r.getViolations().size(), "call with function-param arg should match");
    }

    @Test
    void literalArgDoesNotMatch() {
        Report r = run(XPATH,
                "fun f() { DateTimeFormatter.ofPattern(\"yyyy-MM-dd\") }");
        assertTrue(r.getViolations().isEmpty(), "call with literal arg should not match");
    }

    @Test
    void multipleFunctionParamsOneUsedMatches() {
        Report r = run(XPATH,
                "fun f(raw: String, pattern: String) { DateTimeFormatter.ofPattern(pattern) }");
        assertEquals(1, r.getViolations().size(), "call with one param arg should match");
    }

    // ---- constructor parameter as argument ----

    @Test
    void constructorParamArgMatches() {
        Report r = run(XPATH,
                "class C(val pattern: String) { fun f() { DateTimeFormatter.ofPattern(pattern) } }");
        assertEquals(1, r.getViolations().size(), "call with constructor-param arg should match");
    }

    // ---- local variable (not a param) does not match ----

    @Test
    void localVarArgDoesNotMatch() {
        Report r = run(XPATH,
                "fun f() { val pattern = \"yyyy-MM-dd\"; DateTimeFormatter.ofPattern(pattern) }");
        assertTrue(r.getViolations().isEmpty(), "call with local-var arg should not match (not a param)");
    }

    // ---- multiple arguments ----

    @Test
    void secondArgIsParamMatches() {
        Report r = run(XPATH,
                "fun f(locale: String) { DateTimeFormatter.ofPattern(\"yyyy\", locale) }");
        assertEquals(1, r.getViolations().size(), "call with param as second arg should match");
    }

    @Test
    void bothArgsLiteralsDoNotMatch() {
        Report r = run(XPATH,
                "fun f() { DateTimeFormatter.ofPattern(\"yyyy\", \"extra\") }");
        assertTrue(r.getViolations().isEmpty(), "call with all literal args should not match");
    }

    // ---- nested call — guard applies only to direct args ----

    @Test
    void nestedCallInnerParamDoesNotMatchOuter() {
        // LocalDate.parse(raw, DateTimeFormatter.ofPattern("yyyy"))
        // outer call has 'raw' (a param) as first arg → hasVariableArgument() is true for outer
        // inner call has literal arg → hasVariableArgument() is false for inner
        Report r = run(XPATH,
                "fun f(raw: String) { LocalDate.parse(raw, DateTimeFormatter.ofPattern(\"yyyy\")) }");
        assertEquals(1, r.getViolations().size(), "only outer call has param arg; inner has literal");
    }

    // ---- lambda parameter as argument ----

    @Test
    void lambdaParamArgMatches() {
        Report r = run(XPATH,
                "val block: (String) -> Unit = { pattern -> DateTimeFormatter.ofPattern(pattern) }");
        assertEquals(1, r.getViolations().size(), "call with lambda-param arg should match");
    }

    // ---- no args ----

    @Test
    void noArgsDoesNotMatch() {
        Report r = run(XPATH,
                "fun f() { DateTimeFormatter.ISO_DATE_TIME.toString() }");
        assertTrue(r.getViolations().isEmpty(), "call with no args should not match");
    }

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
