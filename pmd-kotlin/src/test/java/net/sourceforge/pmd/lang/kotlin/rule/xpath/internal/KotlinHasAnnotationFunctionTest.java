/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.rule.xpath.internal;

import static org.junit.jupiter.api.Assertions.assertFalse;
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

class KotlinHasAnnotationFunctionTest {

    private static final String RESOURCE_DIR =
            "net/sourceforge/pmd/lang/kotlin/rule/xpath/hasAnnotation";

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
    void hasAnnotationSimpleNameMatchesProperty() {
        Report report = runXPath(
                "//PropertyDeclaration[pmd-kotlin:hasAnnotation('Column')]",
                getResource(RESOURCE_DIR + "/AnnotatedEntities.kt"));
        assertTrue(report.getProcessingErrors().isEmpty());
        // lines 11 and 14 have @Column
        assertTrue(report.getViolations().stream().anyMatch(v -> v.getBeginLine() == 11),
                "Expected @Column on name property (line 11)");
        assertTrue(report.getViolations().stream().anyMatch(v -> v.getBeginLine() == 14),
                "Expected @Column on email property (line 14)");
        // line 17 (id) has no annotation
        assertTrue(report.getViolations().stream().noneMatch(v -> v.getBeginLine() == 17),
                "Did not expect @Column on id (line 17)");
    }

    @Test
    void hasAnnotationFqnMatchesFunction() {
        Report report = runXPath(
                "//FunctionDeclaration[pmd-kotlin:hasAnnotation('kotlin.Deprecated')]",
                getResource(RESOURCE_DIR + "/AnnotatedEntities.kt"));
        assertTrue(report.getProcessingErrors().isEmpty());
        assertTrue(report.getViolations().stream().anyMatch(v -> v.getBeginLine() == 20),
                "FQN match should find kotlin.Deprecated on oldMethod (line 20)");
        assertTrue(report.getViolations().stream().noneMatch(v -> v.getBeginLine() == 22),
                "normalMethod (line 22) has no annotation");
    }

    @Test
    void hasAnnotationSimpleNameMatchesFunction() {
        Report report = runXPath(
                "//FunctionDeclaration[pmd-kotlin:hasAnnotation('Deprecated')]",
                getResource(RESOURCE_DIR + "/AnnotatedEntities.kt"));
        assertTrue(report.getProcessingErrors().isEmpty());
        assertTrue(report.getViolations().stream().anyMatch(v -> v.getBeginLine() == 20),
                "Expected @Deprecated on oldMethod (line 20)");
        // normalMethod has no annotation
        assertTrue(report.getViolations().stream().noneMatch(v -> v.getBeginLine() == 22),
                "Did not expect @Deprecated on normalMethod (line 22)");
    }

    @Test
    void hasAnnotationSimpleNameMatchesClass() {
        Report report = runXPath(
                "//ClassDeclaration[pmd-kotlin:hasAnnotation('Entity')]",
                getResource(RESOURCE_DIR + "/AnnotatedEntities.kt"));
        assertTrue(report.getProcessingErrors().isEmpty());
        assertTrue(report.getViolations().stream().anyMatch(v -> v.getBeginLine() == 6),
                "Expected @Entity on UserEntity class (starts at line 6, before annotation on line 7)");
    }

    @Test
    void hasAnnotationDoesNotMatchWrongName() {
        Report report = runXPath(
                "//PropertyDeclaration[pmd-kotlin:hasAnnotation('Entity')]",
                getResource(RESOURCE_DIR + "/AnnotatedEntities.kt"));
        assertTrue(report.getProcessingErrors().isEmpty());
        assertTrue(report.getViolations().isEmpty(),
                "No property should match @Entity");
    }

    @Test
    void hasAnnotationDoesNotMatchWrongFqn() {
        Report report = runXPath(
                "//PropertyDeclaration[pmd-kotlin:hasAnnotation('com.other.Column')]",
                getResource(RESOURCE_DIR + "/AnnotatedEntities.kt"));
        assertTrue(report.getProcessingErrors().isEmpty());
        // FQN queries require FQN resolution to match. Without javax.persistence on the
        // analysis classpath, the actual FQN is unresolved, so com.other.Column won't match.
        assertFalse(report.getViolations().stream().anyMatch(v -> v.getBeginLine() == 11),
                "Should not match com.other.Column when actual annotation is javax.persistence.Column");
    }

    @Test
    void hasAnnotationSimpleNameMatchesFqnWrittenAnnotation() {
        // @org.springframework.stereotype.Service written as FQN in source (no import)
        // hasAnnotation('Service') must match via simple-name suffix comparison (path 3)
        Report report = runXPath(
                "//ClassDeclaration[pmd-kotlin:hasAnnotation('Service')]",
                getResource(RESOURCE_DIR + "/AnnotatedEntities.kt"));
        assertTrue(report.getProcessingErrors().isEmpty());
        // FqnAnnotatedService is declared around lines 27-28 -- verify at least one match exists
        assertFalse(report.getViolations().isEmpty(),
                "Expected hasAnnotation('Service') to match @org.springframework.stereotype.Service on FqnAnnotatedService");
        // UserEntity (@Entity, not @Service) must not appear in results
        assertTrue(report.getViolations().stream().noneMatch(v -> v.getBeginLine() <= 9 && v.getBeginLine() >= 7),
                "UserEntity (lines 7-9) must not match hasAnnotation('Service')");
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
