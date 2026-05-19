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

class KotlinHasUnresolvedReferenceFunctionTest {

    private static final String RESOURCE_DIR =
            "net/sourceforge/pmd/lang/kotlin/rule/xpath/hasUnresolvedReference";

    private KotlinTypeXPathTestHelper helper;

    @BeforeEach
    void setUp() {
        URL resource = getClass().getClassLoader().getResource(RESOURCE_DIR);
        if (resource == null) {
            throw new IllegalStateException("Cannot find test resources at: " + RESOURCE_DIR);
        }
        helper = KotlinTypeXPathTestHelper.forDirectory(new File(resource.getFile()));
        helper.injectContext();
    }

    @AfterEach
    void tearDown() {
        KotlinTypeAnalysisContextHolder.clearGlobal();
    }

    @Test
    void unresolvedImportFiresOnMissingPackage() {
        Report report = runXPath(
                "//ImportHeader[pmd-kotlin:hasUnresolvedReference()]",
                getResource(RESOURCE_DIR + "/UnresolvedImports.kt"));

        assertTrue(report.getProcessingErrors().isEmpty(), "Unexpected processing errors");
        // lines 7 and 8 import com.example.external -- not in source tree, unresolved
        assertTrue(report.getViolations().stream().anyMatch(v -> v.getBeginLine() == 7),
                "Expected unresolved import at line 7 (com.example.external.MissingClass)");
        assertTrue(report.getViolations().stream().anyMatch(v -> v.getBeginLine() == 8),
                "Expected unresolved import at line 8 (com.example.external.AnotherMissing)");
    }

    @Test
    void resolvedImportDoesNotFire() {
        // A file with no external imports has no unresolved references -- rule must not fire.
        Report report = runXPath(
                "//ImportHeader[pmd-kotlin:hasUnresolvedReference()]",
                getResource(RESOURCE_DIR + "/NoImports.kt"));

        assertTrue(report.getProcessingErrors().isEmpty(), "Unexpected processing errors");
        assertTrue(report.getViolations().isEmpty(),
                "A file with no imports should have no UnresolvedType violations");
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
            throw new IllegalStateException("Cannot find resource: " + path);
        }
        return new File(resource.getFile());
    }
}
