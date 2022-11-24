/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static net.sourceforge.pmd.lang.ast.test.TestUtilsKt.assertSize;
import static net.sourceforge.pmd.lang.ast.test.TestUtilsKt.assertSuppressed;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.java.JavaParsingHelper;

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
        rule.setProperty(Rule.VIOLATION_SUPPRESS_REGEX_DESCRIPTOR, ".*No Foo.*");
        Report rpt = java.executeRule(rule, TEST1);
        assertSize(rpt, 0);
        assertSuppressed(rpt, 1);
    }

    @Test
    void testExclusionsInReportWithRuleViolationSuppressXPath() {
        Rule rule = new FooRule();
        rule.setProperty(Rule.VIOLATION_SUPPRESS_XPATH_DESCRIPTOR, ".[@SimpleName = 'Foo']");
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

    private static final String TEST1 = "public class Foo {}" + PMD.EOL;

    private static final String TEST2 = "@SuppressWarnings(\"PMD\")" + PMD.EOL + "public class Foo {}";
    private static final String TEST2_FULL = "@java.lang.SuppressWarnings(\"PMD\")" + PMD.EOL + "public class Foo {}";

    private static final String TEST3 = "public class Foo {} // NOPMD";
}
