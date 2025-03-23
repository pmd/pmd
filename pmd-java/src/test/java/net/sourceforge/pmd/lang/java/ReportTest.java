/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java;

import static net.sourceforge.pmd.lang.test.ast.TestUtilsKt.assertSize;
import static net.sourceforge.pmd.lang.test.ast.TestUtilsKt.assertSuppressed;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Optional;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.rule.Rule;
import net.sourceforge.pmd.reporting.Report;

class ReportTest {


    private final JavaParsingHelper java = JavaParsingHelper.DEFAULT;

    @Test
    void testBasic() {
        Report r = java.executeRule(new FooRule(), TEST1);
        assertFalse(r.getViolations().isEmpty());
    }

    @Test
    void testExclusionsInReportWithRuleViolationSuppressRegex() {
        Rule rule = new FooRule();
        rule.setProperty(Rule.VIOLATION_SUPPRESS_REGEX_DESCRIPTOR, Optional.of(Pattern.compile(".*No Foo.*")));
        Report rpt = java.executeRule(rule, TEST1);
        assertSize(rpt, 0);
        assertSuppressed(rpt, 1);
    }

    @Test
    void testExclusionsInReportWithRuleViolationSuppressXPath() {
        Rule rule = new FooRule();
        rule.setProperty(Rule.VIOLATION_SUPPRESS_XPATH_DESCRIPTOR, Optional.of(".[@SimpleName = 'Foo']"));
        Report rpt = java.executeRule(rule, TEST1);
        assertSize(rpt, 0);
        assertSuppressed(rpt, 1);
    }

    @Test
    void testExclusionsInReportWithAnnotations() {
        Report rpt =
            java.executeRule(new FooRule(), TEST2);
        assertSize(rpt, 0);
        assertSuppressed(rpt, 1);
    }

    @Test
    void testExclusionsInReportWithAnnotationsFullName() {
        Report rpt = java.executeRule(new FooRule(), TEST2_FULL);
        assertSize(rpt, 0);
        assertSuppressed(rpt, 1);
    }

    @Test
    void testExclusionsInReportWithNOPMD() {
        Report rpt = java.executeRule(new FooRule(), TEST3);
        assertSize(rpt, 0);
        assertSuppressed(rpt, 1);
    }

    private static final String TEST1 = "public class Foo {}";

    private static final String TEST2 = "@SuppressWarnings(\"PMD\")\npublic class Foo {}";
    private static final String TEST2_FULL = "@java.lang.SuppressWarnings(\"PMD\")\npublic class Foo {}";

    private static final String TEST3 = "public class Foo {} // NOPMD";
}
