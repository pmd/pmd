/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.rule.xpath.internal;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.net.URL;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.PmdAnalysis;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.rule.Rule;
import net.sourceforge.pmd.lang.rule.RuleSet;
import net.sourceforge.pmd.lang.rule.xpath.XPathRule;
import net.sourceforge.pmd.lang.rule.xpath.XPathVersion;
import net.sourceforge.pmd.reporting.Report;

class KotlinIsNullableFunctionTest {

    private static final String IS_NULLABLE_RESOURCE_DIR =
            "net/sourceforge/pmd/lang/kotlin/rule/xpath/isNullable";

    private KotlinTypeXPathTestHelper helper;

    @BeforeEach
    void setUp() {
        URL resource = getClass().getClassLoader().getResource(IS_NULLABLE_RESOURCE_DIR);
        if (resource == null) {
            throw new IllegalStateException("Cannot find test resources at: " + IS_NULLABLE_RESOURCE_DIR);
        }
        helper = KotlinTypeXPathTestHelper.forDirectory(new File(resource.getFile()));
        helper.injectContext();
    }

    @AfterEach
    void tearDown() {
        KotlinTypeAnalysisContextHolder.clearGlobal();
    }

    @Test
    void isNullableMatchesNullableListProperty() {
        File kotlinFile = getResource(IS_NULLABLE_RESOURCE_DIR + "/NullableTypes.kt");
        Report report = runXPath("//PropertyDeclaration[pmd-kotlin:isNullable()]", kotlinFile);
        assertTrue(report.getProcessingErrors().isEmpty(), "No processing errors expected");
        // items: List<String>? at line 3
        assertTrue(report.getViolations().stream().anyMatch(v -> v.getBeginLine() == 3),
                "Expected violation at line 3 (items: List<String>?)");
    }

    @Test
    void isNullableMatchesNullableStringProperty() {
        File kotlinFile = getResource(IS_NULLABLE_RESOURCE_DIR + "/NullableTypes.kt");
        Report report = runXPath("//PropertyDeclaration[pmd-kotlin:isNullable()]", kotlinFile);
        assertTrue(report.getProcessingErrors().isEmpty(), "No processing errors expected");
        // tag: String? at line 15
        assertTrue(report.getViolations().stream().anyMatch(v -> v.getBeginLine() == 15),
                "Expected violation at line 15 (tag: String?)");
    }

    @Test
    void isNullableDoesNotMatchNonNullableListProperty() {
        File kotlinFile = getResource(IS_NULLABLE_RESOURCE_DIR + "/NullableTypes.kt");
        Report report = runXPath("//PropertyDeclaration[pmd-kotlin:isNullable()]", kotlinFile);
        assertTrue(report.getProcessingErrors().isEmpty(), "No processing errors expected");
        // names: List<String> at line 6 -- NOT nullable
        assertTrue(report.getViolations().stream().noneMatch(v -> v.getBeginLine() == 6),
                "Did not expect violation at line 6 (names: List<String>)");
    }

    @Test
    void isNullableMatchesNullableReturnType() {
        File kotlinFile = getResource(IS_NULLABLE_RESOURCE_DIR + "/NullableTypes.kt");
        Report report = runXPath("//FunctionDeclaration[pmd-kotlin:isNullable()]", kotlinFile);
        assertTrue(report.getProcessingErrors().isEmpty(), "No processing errors expected");
        // findItem(): String? at line 9
        assertTrue(report.getViolations().stream().anyMatch(v -> v.getBeginLine() == 9),
                "Expected violation at line 9 (findItem(): String?)");
    }

    @Test
    void isNullableDoesNotMatchNonNullableReturnType() {
        File kotlinFile = getResource(IS_NULLABLE_RESOURCE_DIR + "/NullableTypes.kt");
        Report report = runXPath("//FunctionDeclaration[pmd-kotlin:isNullable()]", kotlinFile);
        assertTrue(report.getProcessingErrors().isEmpty(), "No processing errors expected");
        // getItem(): String at line 12 -- NOT nullable
        assertTrue(report.getViolations().stream().noneMatch(v -> v.getBeginLine() == 12),
                "Did not expect violation at line 12 (getItem(): String)");
    }

    @Test
    void isNullableMatchesNullableFunctionParameter() {
        File kotlinFile = getResource(IS_NULLABLE_RESOURCE_DIR + "/NullableTypes.kt");
        Report report = runXPath("//FunctionValueParameter[pmd-kotlin:isNullable()]", kotlinFile);
        assertTrue(report.getProcessingErrors().isEmpty(), "No processing errors expected");
        // fun process(input: String?) at line 18 -- nullable parameter
        assertTrue(report.getViolations().stream().anyMatch(v -> v.getBeginLine() == 18),
                "Expected violation at line 18 (input: String?)");
    }

    @Test
    void isNullableDoesNotMatchNonNullableFunctionParameter() {
        File kotlinFile = getResource(IS_NULLABLE_RESOURCE_DIR + "/NullableTypes.kt");
        Report report = runXPath("//FunctionValueParameter[pmd-kotlin:isNullable()]", kotlinFile);
        assertTrue(report.getProcessingErrors().isEmpty(), "No processing errors expected");
        // fun transform(input: String) at line 21 -- NOT nullable
        assertTrue(report.getViolations().stream().noneMatch(v -> v.getBeginLine() == 21),
                "Did not expect violation at line 21 (input: String)");
    }

    private Report runXPath(String xpathExpr, File kotlinFile) {
        PMDConfiguration config = new PMDConfiguration();
        config.setIgnoreIncrementalAnalysis(true);
        config.setDefaultLanguageVersion(
                LanguageRegistry.PMD.getLanguageById("kotlin").getDefaultVersion());

        try (PmdAnalysis pmd = PmdAnalysis.create(config)) {
            pmd.addRuleSet(RuleSet.forSingleRule(buildXPathRule(xpathExpr)));
            pmd.files().addFile(kotlinFile.toPath());
            return pmd.performAnalysisAndCollectReport();
        }
    }

    private Rule buildXPathRule(String xpathExpr) {
        XPathRule rule = new XPathRule(XPathVersion.DEFAULT, xpathExpr);
        rule.setLanguage(LanguageRegistry.PMD.getLanguageById("kotlin"));
        rule.setMessage("test");
        rule.setName("TestRule");
        return rule;
    }

    private File getResource(String path) {
        URL resource = getClass().getClassLoader().getResource(path);
        if (resource == null) {
            throw new IllegalStateException("Cannot find test resource: " + path);
        }
        return new File(resource.getFile());
    }
}
