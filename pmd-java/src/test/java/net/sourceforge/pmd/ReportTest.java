/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.java.JavaLanguageModule;
import net.sourceforge.pmd.testframework.RuleTst;

public class ReportTest extends RuleTst {

    private LanguageVersion defaultLanguage = LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getDefaultVersion();

    @Test
    public void testBasic() {
        Report r = new Report();
        runTestFromString(TEST1, new FooRule(), r, defaultLanguage);
        assertFalse(r.isEmpty());
    }

    @Test
    public void testExclusionsInReportWithRuleViolationSuppressRegex() {
        Report rpt = new Report();
        Rule rule = new FooRule();
        rule.setProperty(Rule.VIOLATION_SUPPRESS_REGEX_DESCRIPTOR, ".*No Foo.*");
        runTestFromString(TEST1, rule, rpt, defaultLanguage);
        assertTrue(rpt.isEmpty());
        assertEquals(1, rpt.getSuppressedRuleViolations().size());
    }

    @Test
    public void testExclusionsInReportWithRuleViolationSuppressXPath() {
        Report rpt = new Report();
        Rule rule = new FooRule();
        rule.setProperty(Rule.VIOLATION_SUPPRESS_XPATH_DESCRIPTOR, ".[@SimpleName = 'Foo']");
        runTestFromString(TEST1, rule, rpt, defaultLanguage);
        assertTrue(rpt.isEmpty());
        assertEquals(1, rpt.getSuppressedRuleViolations().size());
    }

    @Test
    public void testExclusionsInReportWithAnnotations() {
        Report rpt = new Report();
        runTestFromString(TEST2, new FooRule(), rpt,
                LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getVersion("1.5"));
        assertTrue(rpt.isEmpty());
        assertEquals(1, rpt.getSuppressedRuleViolations().size());
    }

    @Test
    public void testExclusionsInReportWithAnnotationsFullName() {
        Report rpt = new Report();
        runTestFromString(TEST2_FULL, new FooRule(), rpt,
                LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getVersion("1.5"));
        assertTrue(rpt.isEmpty());
        assertEquals(1, rpt.getSuppressedRuleViolations().size());
    }

    @Test
    public void testExclusionsInReportWithNOPMD() {
        Report rpt = new Report();
        runTestFromString(TEST3, new FooRule(), rpt, defaultLanguage);
        assertTrue(rpt.isEmpty());
        assertEquals(1, rpt.getSuppressedRuleViolations().size());
    }

    private static final String TEST1 = "public class Foo {}" + PMD.EOL;

    private static final String TEST2 = "@SuppressWarnings(\"PMD\")" + PMD.EOL + "public class Foo {}";
    private static final String TEST2_FULL = "@java.lang.SuppressWarnings(\"PMD\")" + PMD.EOL + "public class Foo {}";

    private static final String TEST3 = "public class Foo {} // NOPMD";
}
