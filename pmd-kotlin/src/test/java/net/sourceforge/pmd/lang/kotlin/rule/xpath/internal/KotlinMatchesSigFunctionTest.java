/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.rule.xpath.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.reporting.Report;

class KotlinMatchesSigFunctionTest extends BaseKotlinXPathFunctionTest {

    private static final String RESOURCE_DIR =
            "net/sourceforge/pmd/lang/kotlin/rule/xpath/matchesSig";

    @Test
    void matchesSigPatternMatchesJavaParamNames() {
        Report report = runXPath(
                "//PostfixUnaryExpression[pmd-kotlin:matchesSig('java.util.regex.Pattern#matches(java.lang.String,java.lang.CharSequence)')]",
                getResource(RESOURCE_DIR + "/PatternUsage.kt"));
        assertNoErrors(report);
        assertViolationAtLine(report, 5, "Expected violation at line 5 (Pattern.matches call)");
    }

    @Test
    void matchesSigPatternMatchesKotlinParamNames() {
        Report report = runXPath(
                "//PostfixUnaryExpression[pmd-kotlin:matchesSig('java.util.regex.Pattern#matches(kotlin.String,kotlin.CharSequence)')]",
                getResource(RESOURCE_DIR + "/PatternUsage.kt"));
        assertNoErrors(report);
        assertViolationAtLine(report, 5, "Expected violation at line 5 (Pattern.matches call with Kotlin param names)");
    }

    @Test
    void matchesSigPatternMatchesWildcardParams() {
        Report report = runXPath(
                "//PostfixUnaryExpression[pmd-kotlin:matchesSig('java.util.regex.Pattern#matches(_,_)')]",
                getResource(RESOURCE_DIR + "/PatternUsage.kt"));
        assertNoErrors(report);
        assertViolationAtLine(report, 5, "Expected violation at line 5 (Pattern.matches with wildcard params)");
    }

    @Test
    void matchesSigPatternDoesNotMatchTrimCall() {
        Report report = runXPath(
                "//PostfixUnaryExpression[pmd-kotlin:matchesSig('java.util.regex.Pattern#matches(_,_)')]",
                getResource(RESOURCE_DIR + "/PatternUsage.kt"));
        assertNoErrors(report);
        assertNoViolationAtLine(report, 8, "Did not expect violation at line 8 (trim() call)");
    }

    @Test
    void matchesSigKotlinListSizeMatches() {
        Report report = runXPath(
                "//PostfixUnaryExpression[pmd-kotlin:matchesSig('kotlin.collections.List#size()')]",
                getResource(RESOURCE_DIR + "/ListUsage.kt"));
        assertNoErrors(report);
        assertViolationAtLine(report, 3, "Expected violation at line 3 (items.size call)");
    }

    @Test
    void matchesSigJavaListSizeMatches() {
        Report report = runXPath(
                "//PostfixUnaryExpression[pmd-kotlin:matchesSig('java.util.List#size()')]",
                getResource(RESOURCE_DIR + "/ListUsage.kt"));
        assertNoErrors(report);
        assertViolationAtLine(report, 3, "Expected violation at line 3 (items.size with java.util.List name)");
    }

    @Test
    void matchesSigWildcardReceiverSizeMatches() {
        Report report = runXPath(
                "//PostfixUnaryExpression[pmd-kotlin:matchesSig('_#size()')]",
                getResource(RESOURCE_DIR + "/ListUsage.kt"));
        assertNoErrors(report);
        assertViolationAtLine(report, 3, "Expected violation at line 3 (items.size with wildcard receiver)");
    }

    @Test
    void printStackTraceMatchesBadCase() {
        Report report = runXPath(
                "//PostfixUnaryExpression[pmd-kotlin:matchesSig('java.lang.Throwable#printStackTrace(*)')]",
                getResource(RESOURCE_DIR + "/PrintStackTraceUsage.kt"));
        assertNoErrors(report);
        assertViolationAtLine(report, 6, "Expected violation at line 6 (e.printStackTrace())");
    }

    @Test
    void printStackTraceDoesNotMatchRethrow() {
        Report report = runXPath(
                "//PostfixUnaryExpression[pmd-kotlin:matchesSig('java.lang.Throwable#printStackTrace(*)')]",
                getResource(RESOURCE_DIR + "/PrintStackTraceUsage.kt"));
        assertNoErrors(report);
        assertNoViolationAtLine(report, 14, "Line 14 is a re-throw, not printStackTrace -- should not match");
    }

    @Test
    void printStackTraceDoesNotMatchEnclosingTryBlock() {
        Report report = runXPath(
                "//PostfixUnaryExpression[pmd-kotlin:matchesSig('java.lang.Throwable#printStackTrace(*)')]",
                getResource(RESOURCE_DIR + "/PrintStackTraceUsage.kt"));
        assertNoErrors(report);
        assertEquals(1, report.getViolations().size(), "Expected exactly one printStackTrace match");
        assertViolationAtLine(report, 6, "Expected violation at line 6 (e.printStackTrace())");
        assertNoViolationAtLine(report, 3, "Try block should not match printStackTrace");
    }

    @Test
    void systemOutPrintlnMatchesBadCase() {
        Report report = runXPath(
                "//PostfixUnaryExpression[pmd-kotlin:matchesSig('java.io.PrintStream#println(*)')]",
                getResource(RESOURCE_DIR + "/SystemPrintlnUsage.kt"));
        assertNoErrors(report);
        assertViolationAtLine(report, 3, "Expected violation at line 3 (System.out.println)");
        assertViolationAtLine(report, 4, "Expected violation at line 4 (System.err.println)");
    }

    @Test
    void systemPrintlnDoesNotMatchUnrelatedCode() {
        Report report = runXPath(
                "//PostfixUnaryExpression[pmd-kotlin:matchesSig('java.io.PrintStream#println(*)')]",
                getResource(RESOURCE_DIR + "/SystemPrintlnUsage.kt"));
        assertNoErrors(report);
        assertNoViolationAtLine(report, 8, "Line 8 is simple arithmetic -- should not match");
    }

    @Test
    void calendarGetInstanceMatchesBadCase() {
        Report report = runXPath(
                "//PostfixUnaryExpression[pmd-kotlin:matchesSig('java.util.Calendar#getInstance(*)')]",
                getResource(RESOURCE_DIR + "/CalendarDateUsage.kt"));
        assertNoErrors(report);
        assertViolationAtLine(report, 5, "Expected violation at line 5 (Calendar.getInstance())");
    }

    @Test
    void dateTimeFormatterOfPatternMatchesWildcard() {
        Report report = runXPath(
                "//PostfixUnaryExpression[pmd-kotlin:matchesSig('java.time.format.DateTimeFormatter#ofPattern(*)')]",
                getResource(RESOURCE_DIR + "/DateTimeFormatterUsage.kt"));
        assertNoErrors(report);
        assertViolationAtLine(report, 5, "Expected violation at line 5 (DateTimeFormatter.ofPattern static call)");
    }

    @Test
    void matchesSigNestedArgProducesExactlyOneViolation() {
        Report report = runXPath(
                "//PostfixUnaryExpression[pmd-kotlin:matchesSig('java.time.format.DateTimeFormatter#ofPattern(*)')]",
                getResource(RESOURCE_DIR + "/NestedArgUsage.kt"));
        assertNoErrors(report);
        assertEquals(1, report.getViolations().size(),
                "Expected exactly 1 violation — outer LocalDate.parse must not double-match");
        assertViolationAtLine(report, 7, "Expected violation at line 7 (DateTimeFormatter.ofPattern nested in LocalDate.parse)");
    }

    @Test
    void calendarGetInstanceDoesNotMatchLocalDateTimeNow() {
        Report report = runXPath(
                "//PostfixUnaryExpression[pmd-kotlin:matchesSig('java.util.Calendar#getInstance(*)')]",
                getResource(RESOURCE_DIR + "/CalendarDateUsage.kt"));
        assertNoErrors(report);
        assertNoViolationAtLine(report, 10, "Line 10 uses LocalDateTime.now() -- should not match Calendar.getInstance");
    }

    @Test
    void constructorCtorMatchesSimpleCall() {
        Report report = runXPath(
                "//PostfixUnaryExpression[pmd-kotlin:matchesSig('java.util.Date#<init>(*)')]",
                getResource(RESOURCE_DIR + "/JodaDateTimeFormatterCtorUsage.kt"));
        assertNoErrors(report);
        assertEquals(1, report.getViolations().stream().filter(v -> v.getBeginLine() == 5).count(),
                "Line 5 (simple ctor) should match java.util.Date#<init>(*)");
    }

    @Test
    void constructorCtorMatchesFqnCall() {
        Report report = runXPath(
                "//PostfixUnaryExpression[pmd-kotlin:matchesSig('java.util.Date#<init>(*)')]",
                getResource(RESOURCE_DIR + "/JodaDateTimeFormatterCtorUsage.kt"));
        assertNoErrors(report);
        assertEquals(1, report.getViolations().stream().filter(v -> v.getBeginLine() == 6).count(),
                "Line 6 (FQN ctor java.util.Date()) should match java.util.Date#<init>(*)");
    }
}
