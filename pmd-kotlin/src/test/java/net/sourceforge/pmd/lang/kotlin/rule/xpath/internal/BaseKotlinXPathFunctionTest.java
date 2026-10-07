/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.rule.xpath.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.PmdAnalysis;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.document.FileId;
import net.sourceforge.pmd.lang.rule.Rule;
import net.sourceforge.pmd.lang.rule.RuleSet;
import net.sourceforge.pmd.lang.rule.xpath.XPathRule;
import net.sourceforge.pmd.lang.rule.xpath.XPathVersion;
import net.sourceforge.pmd.reporting.Report;
import net.sourceforge.pmd.util.internal.AuxClasspathUtil;

/**
 * Shared helpers for Kotlin XPath function tests.
 *
 * @since 7.28.0
 */
abstract class BaseKotlinXPathFunctionTest {

    protected Report runXPath(String xpathExpr, File kotlinFile) {
        return runXPath(xpathExpr, kotlinFile, false);
    }

    /**
     * Runs {@code xpathExpr} against {@code kotlinFile}, configuring the module's own
     * runtime classpath as the Kotlin auxClasspath when {@code withAuxClasspath} is
     * {@code true}. Use this to resolve annotation/type FQNs for classes already on the
     * test classpath (e.g. {@code org.junit.jupiter.api.Test}) without adding a new
     * test dependency.
     */
    protected Report runXPath(String xpathExpr, File kotlinFile, boolean withAuxClasspath) {
        PMDConfiguration config = new PMDConfiguration();
        config.setIgnoreIncrementalAnalysis(true);
        config.setDefaultLanguageVersion(
                LanguageRegistry.PMD.getLanguageById("kotlin").getDefaultVersion());
        if (withAuxClasspath) {
            config.setAuxClasspath(AuxClasspathUtil.toRawClasspath(AuxClasspathUtil.getRuntimeClasspath()));
        }

        try (PmdAnalysis pmd = PmdAnalysis.create(config)) {
            pmd.addRuleSet(RuleSet.forSingleRule(buildXPathRule(xpathExpr)));
            pmd.files().addFile(kotlinFile.toPath());
            return pmd.performAnalysisAndCollectReport();
        }
    }

    protected Report runXPath(String xpathExpr, String code) {
        PMDConfiguration config = new PMDConfiguration();
        config.setIgnoreIncrementalAnalysis(true);
        config.setDefaultLanguageVersion(
                LanguageRegistry.PMD.getLanguageById("kotlin").getDefaultVersion());

        try (PmdAnalysis pmd = PmdAnalysis.create(config)) {
            pmd.addRuleSet(RuleSet.forSingleRule(buildXPathRule(xpathExpr)));
            pmd.files().addSourceFile(FileId.fromPathLikeString("snippet.kt"), code);
            return pmd.performAnalysisAndCollectReport();
        }
    }


    protected Rule buildXPathRule(String xpathExpr) {
        XPathRule rule = new XPathRule(XPathVersion.DEFAULT, xpathExpr);
        rule.setLanguage(LanguageRegistry.PMD.getLanguageById("kotlin"));
        rule.setMessage("test");
        rule.setName("TestRule");
        return rule;
    }

    protected File getResource(String path) {
        URL resource = getClass().getClassLoader().getResource(path);
        if (resource == null) {
            throw new IllegalStateException("Cannot find resource: " + path);
        }
        try {
            return Paths.get(resource.toURI()).toFile();
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Invalid resource URI: " + resource, e);
        }
    }

    protected static void assertNoErrors(Report report) {
        assertTrue(report.getProcessingErrors().isEmpty(), "Unexpected processing errors");
    }

    protected static void assertViolationAtLine(Report report, int line, String message) {
        assertTrue(report.getViolations().stream().anyMatch(v -> v.getBeginLine() == line), message);
    }

    protected static void assertNoViolationAtLine(Report report, int line, String message) {
        assertTrue(report.getViolations().stream().noneMatch(v -> v.getBeginLine() == line), message);
    }

    protected static void assertViolationAt(Report report, int line, int column, String message) {
        assertTrue(report.getViolations().stream()
                .anyMatch(v -> v.getBeginLine() == line && v.getBeginColumn() == column), message);
    }

    protected static void assertNoViolationAt(Report report, int line, int column, String message) {
        assertTrue(report.getViolations().stream()
                .noneMatch(v -> v.getBeginLine() == line && v.getBeginColumn() == column), message);
    }

    /**
     * Asserts that the report's violations occur at exactly {@code expectedLines}, one violation
     * per listed line number (order doesn't matter, but count does: a duplicate expected line
     * requires two violations on that line). Use this instead of combining several
     * {@link #assertViolationAtLine}/{@link #assertNoViolationAtLine} calls when you need to
     * rule out unexpected violations on lines you didn't think to check.
     */
    protected static void assertViolationsOnlyAtLines(Report report, String message, int... expectedLines) {
        List<Integer> actual = report.getViolations().stream()
                .map(v -> v.getBeginLine())
                .sorted()
                .collect(Collectors.toList());
        List<Integer> expected = Arrays.stream(expectedLines).boxed()
                .sorted()
                .collect(Collectors.toList());
        assertEquals(expected, actual, message);
    }
}
