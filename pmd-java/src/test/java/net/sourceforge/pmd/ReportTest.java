/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static net.sourceforge.pmd.lang.ast.test.TestUtilsKt.assertSize;
import static net.sourceforge.pmd.lang.ast.test.TestUtilsKt.assertSuppressed;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.java.JavaLanguageModule;
import net.sourceforge.pmd.lang.java.JavaParsingHelper;

public class ReportTest {


    private final JavaParsingHelper java = JavaParsingHelper.WITH_PROCESSING;

    @Test
    public void testBasic() {
        Report r = java.executeRule(new FooRule(), TEST1);
        assertFalse(r.getViolations().isEmpty());
    }

    @Test
    public void testExclusionsInReportWithRuleViolationSuppressRegex() {
        Rule rule = new FooRule();
        rule.setProperty(Rule.VIOLATION_SUPPRESS_REGEX_DESCRIPTOR, ".*No Foo.*");
        Report rpt = java.executeRule(rule, TEST1);
        assertSize(rpt, 0);
        assertSuppressed(rpt, 1);
    }

    @Test
    public void testExclusionsInReportWithRuleViolationSuppressXPath() {
        Rule rule = new FooRule();
        rule.setProperty(Rule.VIOLATION_SUPPRESS_XPATH_DESCRIPTOR, ".[@SimpleName = 'Foo']");
        Report rpt = java.executeRule(rule, TEST1);
        assertSize(rpt, 0);
        assertSuppressed(rpt, 1);
    }

    @Test
    public void testExclusionsInReportWithAnnotations() {
        LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getVersion("1.5");
        Report rpt =
            java.executeRule(new FooRule(), TEST2);
        assertSize(rpt, 0);
        assertSuppressed(rpt, 1);
    }

    @Test
    public void testExclusionsInReportWithAnnotationsFullName() {
        LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getVersion("1.5");
        Report rpt = java.executeRule(new FooRule(), TEST2_FULL);
        assertSize(rpt, 0);
        assertSuppressed(rpt, 1);
    }

    @Test
    public void testExclusionsInReportWithNOPMD() {
        Report rpt = java.executeRule(new FooRule(), TEST3);
        assertSize(rpt, 0);
        assertSuppressed(rpt, 1);
    }

    private static final String TEST1 = "public class Foo {}" + PMD.EOL;

    private static final String TEST2 = "@SuppressWarnings(\"PMD\")" + PMD.EOL + "public class Foo {}";
    private static final String TEST2_FULL = "@java.lang.SuppressWarnings(\"PMD\")" + PMD.EOL + "public class Foo {}";

    private static final String TEST3 = "public class Foo {} // NOPMD";
}
