/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.rule.xpath.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

class KotlinMatchesSigFunctionTest {

    private static final String MATCHES_SIG_RESOURCE_DIR =
            "net/sourceforge/pmd/lang/kotlin/rule/xpath/matchesSig";

    private KotlinTypeXPathTestHelper helper;

    @BeforeEach
    void setUp() {
        URL resource = getClass().getClassLoader().getResource(MATCHES_SIG_RESOURCE_DIR);
        if (resource == null) {
            throw new IllegalStateException("Cannot find test resources at: " + MATCHES_SIG_RESOURCE_DIR);
        }
        helper = KotlinTypeXPathTestHelper.forDirectory(new File(resource.getFile()));
        helper.injectContext();
    }

    @AfterEach
    void tearDown() {
        KotlinTypeAnalysisContextHolder.clearGlobal();
    }

    @Test
    void matchesSigPatternMatchesJavaParamNames() {
        File kotlinFile = getResource(MATCHES_SIG_RESOURCE_DIR + "/PatternUsage.kt");
        Report report = runXPath(
                "//PostfixUnaryExpression[pmd-kotlin:matchesSig('java.util.regex.Pattern#matches(java.lang.String,java.lang.CharSequence)')]",
                kotlinFile);
        assertTrue(report.getProcessingErrors().isEmpty(), "No processing errors expected");
        assertTrue(report.getViolations().stream().anyMatch(v -> v.getBeginLine() == 5),
                "Expected violation at line 5 (Pattern.matches call)");
    }

    @Test
    void matchesSigPatternMatchesKotlinParamNames() {
        File kotlinFile = getResource(MATCHES_SIG_RESOURCE_DIR + "/PatternUsage.kt");
        Report report = runXPath(
                "//PostfixUnaryExpression[pmd-kotlin:matchesSig('java.util.regex.Pattern#matches(kotlin.String,kotlin.CharSequence)')]",
                kotlinFile);
        assertTrue(report.getProcessingErrors().isEmpty(), "No processing errors expected");
        assertTrue(report.getViolations().stream().anyMatch(v -> v.getBeginLine() == 5),
                "Expected violation at line 5 (Pattern.matches call with Kotlin param names)");
    }

    @Test
    void matchesSigPatternMatchesWildcardParams() {
        File kotlinFile = getResource(MATCHES_SIG_RESOURCE_DIR + "/PatternUsage.kt");
        Report report = runXPath(
                "//PostfixUnaryExpression[pmd-kotlin:matchesSig('java.util.regex.Pattern#matches(_,_)')]",
                kotlinFile);
        assertTrue(report.getProcessingErrors().isEmpty(), "No processing errors expected");
        assertTrue(report.getViolations().stream().anyMatch(v -> v.getBeginLine() == 5),
                "Expected violation at line 5 (Pattern.matches with wildcard params)");
    }

    @Test
    void matchesSigPatternDoesNotMatchTrimCall() {
        File kotlinFile = getResource(MATCHES_SIG_RESOURCE_DIR + "/PatternUsage.kt");
        Report report = runXPath(
                "//PostfixUnaryExpression[pmd-kotlin:matchesSig('java.util.regex.Pattern#matches(_,_)')]",
                kotlinFile);
        assertTrue(report.getProcessingErrors().isEmpty(), "No processing errors expected");
        // trimIt() call on line 8 should NOT match Pattern.matches
        assertTrue(report.getViolations().stream().noneMatch(v -> v.getBeginLine() == 8),
                "Did not expect violation at line 8 (trim() call)");
    }

    @Test
    void matchesSigKotlinListSizeMatches() {
        File kotlinFile = getResource(MATCHES_SIG_RESOURCE_DIR + "/ListUsage.kt");
        Report report = runXPath(
                "//PostfixUnaryExpression[pmd-kotlin:matchesSig('kotlin.collections.List#size()')]",
                kotlinFile);
        assertTrue(report.getProcessingErrors().isEmpty(), "No processing errors expected");
        assertTrue(report.getViolations().stream().anyMatch(v -> v.getBeginLine() == 3),
                "Expected violation at line 3 (items.size call)");
    }

    @Test
    void matchesSigJavaListSizeMatches() {
        File kotlinFile = getResource(MATCHES_SIG_RESOURCE_DIR + "/ListUsage.kt");
        Report report = runXPath(
                "//PostfixUnaryExpression[pmd-kotlin:matchesSig('java.util.List#size()')]",
                kotlinFile);
        assertTrue(report.getProcessingErrors().isEmpty(), "No processing errors expected");
        assertTrue(report.getViolations().stream().anyMatch(v -> v.getBeginLine() == 3),
                "Expected violation at line 3 (items.size with java.util.List name)");
    }

    @Test
    void matchesSigWildcardReceiverSizeMatches() {
        File kotlinFile = getResource(MATCHES_SIG_RESOURCE_DIR + "/ListUsage.kt");
        Report report = runXPath(
                "//PostfixUnaryExpression[pmd-kotlin:matchesSig('_#size()')]",
                kotlinFile);
        assertTrue(report.getProcessingErrors().isEmpty(), "No processing errors expected");
        assertTrue(report.getViolations().stream().anyMatch(v -> v.getBeginLine() == 3),
                "Expected violation at line 3 (items.size with wildcard receiver)");
    }

    // --- AvoidPrintStackTrace ---

    @Test
    void printStackTraceMatchesBadCase() {
        File kotlinFile = getResource(MATCHES_SIG_RESOURCE_DIR + "/PrintStackTraceUsage.kt");
        Report report = runXPath(
                "//PostfixUnaryExpression[pmd-kotlin:matchesSig('java.lang.Throwable#printStackTrace(*)')]",
                kotlinFile);
        assertTrue(report.getProcessingErrors().isEmpty(), "No processing errors expected");
        assertTrue(report.getViolations().stream().anyMatch(v -> v.getBeginLine() == 6),
                "Expected violation at line 6 (e.printStackTrace())");
    }

    @Test
    void printStackTraceDoesNotMatchRethrow() {
        File kotlinFile = getResource(MATCHES_SIG_RESOURCE_DIR + "/PrintStackTraceUsage.kt");
        Report report = runXPath(
                "//PostfixUnaryExpression[pmd-kotlin:matchesSig('java.lang.Throwable#printStackTrace(*)')]",
                kotlinFile);
        assertTrue(report.getProcessingErrors().isEmpty(), "No processing errors expected");
        assertTrue(report.getViolations().stream().noneMatch(v -> v.getBeginLine() == 14),
                "Line 14 is a re-throw, not printStackTrace -- should not match");
    }

    @Test
    void printStackTraceDoesNotMatchEnclosingTryBlock() {
        File kotlinFile = getResource(MATCHES_SIG_RESOURCE_DIR + "/PrintStackTraceUsage.kt");
        Report report = runXPath(
                "//PostfixUnaryExpression[pmd-kotlin:matchesSig('java.lang.Throwable#printStackTrace(*)')]",
                kotlinFile);
        assertTrue(report.getProcessingErrors().isEmpty(), "No processing errors expected");
        assertEquals(1, report.getViolations().size(), "Expected exactly one printStackTrace match");
        assertTrue(report.getViolations().stream().anyMatch(v -> v.getBeginLine() == 6),
                "Expected violation at line 6 (e.printStackTrace())");
        assertTrue(report.getViolations().stream().noneMatch(v -> v.getBeginLine() == 3),
                "Try block should not match printStackTrace");
    }

    // --- SystemPrintln ---

    @Test
    void systemOutPrintlnMatchesBadCase() {
        File kotlinFile = getResource(MATCHES_SIG_RESOURCE_DIR + "/SystemPrintlnUsage.kt");
        Report report = runXPath(
                "//PostfixUnaryExpression[pmd-kotlin:matchesSig('java.io.PrintStream#println(*)')]",
                kotlinFile);
        assertTrue(report.getProcessingErrors().isEmpty(), "No processing errors expected");
        assertTrue(report.getViolations().stream().anyMatch(v -> v.getBeginLine() == 3),
                "Expected violation at line 3 (System.out.println)");
        assertTrue(report.getViolations().stream().anyMatch(v -> v.getBeginLine() == 4),
                "Expected violation at line 4 (System.err.println)");
    }

    @Test
    void systemPrintlnDoesNotMatchUnrelatedCode() {
        File kotlinFile = getResource(MATCHES_SIG_RESOURCE_DIR + "/SystemPrintlnUsage.kt");
        Report report = runXPath(
                "//PostfixUnaryExpression[pmd-kotlin:matchesSig('java.io.PrintStream#println(*)')]",
                kotlinFile);
        assertTrue(report.getProcessingErrors().isEmpty(), "No processing errors expected");
        assertTrue(report.getViolations().stream().noneMatch(v -> v.getBeginLine() == 8),
                "Line 8 is simple arithmetic -- should not match");
    }

    // --- AvoidCalendarDateCreation ---

    @Test
    void calendarGetInstanceMatchesBadCase() {
        File kotlinFile = getResource(MATCHES_SIG_RESOURCE_DIR + "/CalendarDateUsage.kt");
        Report report = runXPath(
                "//PostfixUnaryExpression[pmd-kotlin:matchesSig('java.util.Calendar#getInstance(*)')]",
                kotlinFile);
        assertTrue(report.getProcessingErrors().isEmpty(), "No processing errors expected");
        assertTrue(report.getViolations().stream().anyMatch(v -> v.getBeginLine() == 5),
                "Expected violation at line 5 (Calendar.getInstance())");
    }

    @Test
    void calendarGetInstanceDoesNotMatchLocalDateTimeNow() {
        File kotlinFile = getResource(MATCHES_SIG_RESOURCE_DIR + "/CalendarDateUsage.kt");
        Report report = runXPath(
                "//PostfixUnaryExpression[pmd-kotlin:matchesSig('java.util.Calendar#getInstance(*)')]",
                kotlinFile);
        assertTrue(report.getProcessingErrors().isEmpty(), "No processing errors expected");
        assertTrue(report.getViolations().stream().noneMatch(v -> v.getBeginLine() == 10),
                "Line 10 uses LocalDateTime.now() -- should not match Calendar.getInstance");
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
