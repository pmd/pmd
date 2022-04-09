/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.rule.security;

import static org.junit.Assert.assertEquals;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Test;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.vf.VFTestUtils;
import net.sourceforge.pmd.lang.vf.ast.VfParsingHelper;
import net.sourceforge.pmd.testframework.PmdRuleTst;

public class VfUnescapeElTest extends PmdRuleTst {
    public static final String EXPECTED_RULE_MESSAGE = "Avoid unescaped user controlled content in EL";

    /**
     * Verify that CustomFields stored in sfdx project format are correctly parsed
     */
    @Test
    public void testSfdxCustomFields() {
        Path vfPagePath = VFTestUtils.getMetadataPath(this, VFTestUtils.MetadataFormat.SFDX, VFTestUtils.MetadataType.Vf)
                .resolve("StandardAccount.page");

        Report report = runRule(vfPagePath);
        List<RuleViolation> ruleViolations = report.getViolations();
        assertEquals("Number of violations", 20, ruleViolations.size());

        int firstLineWithErrors = 14;
        for (int i = 0; i < ruleViolations.size(); i++) {
            RuleViolation ruleViolation = ruleViolations.get(i);
            assertEquals(EXPECTED_RULE_MESSAGE, ruleViolation.getDescription());
            int expectedLineNumber = firstLineWithErrors + i;
            if (ruleViolations.size() + firstLineWithErrors - 1 == expectedLineNumber) {
                // The last line has two errors on the same page
                expectedLineNumber = expectedLineNumber - 1;
            }
            assertEquals("Line Number", expectedLineNumber, ruleViolation.getBeginLine());
        }
    }

    /**
     * Verify that CustomFields stored in mdapi format are correctly parsed
     */
    @Test
    public void testMdapiCustomFields() {
        Path vfPagePath = VFTestUtils.getMetadataPath(this, VFTestUtils.MetadataFormat.MDAPI, VFTestUtils.MetadataType.Vf).resolve("StandardAccount.page");

        Report report = runRule(vfPagePath);
        List<RuleViolation> ruleViolations = report.getViolations();
        assertEquals("Number of violations", 6, ruleViolations.size());
        int firstLineWithErrors = 8;
        for (int i = 0; i < ruleViolations.size(); i++) {
            RuleViolation ruleViolation = ruleViolations.get(i);
            assertEquals(EXPECTED_RULE_MESSAGE, ruleViolation.getDescription());
            assertEquals("Line Number", firstLineWithErrors + i, ruleViolation.getBeginLine());
        }
    }

    /**
     * Tests a page with a single Apex controller
     */
    @Test
    public void testApexController() {
        Path vfPagePath = VFTestUtils.getMetadataPath(this, VFTestUtils.MetadataFormat.SFDX, VFTestUtils.MetadataType.Vf).resolve("ApexController.page");

        Report report = runRule(vfPagePath);
        List<RuleViolation> ruleViolations = report.getViolations();
        assertEquals("Number of violations", 2, ruleViolations.size());
        int firstLineWithErrors = 9;
        for (int i = 0; i < ruleViolations.size(); i++) {
            // There should start at line 9
            RuleViolation ruleViolation = ruleViolations.get(i);
            assertEquals(EXPECTED_RULE_MESSAGE, ruleViolation.getDescription());
            assertEquals("Line Number", firstLineWithErrors + i, ruleViolation.getBeginLine());
        }
    }

    /**
     * Tests a page with a standard controller and two Apex extensions
     */
    @Test
    public void testExtensions() {
        Path vfPagePath = VFTestUtils.getMetadataPath(this, VFTestUtils.MetadataFormat.SFDX, VFTestUtils.MetadataType.Vf)
                .resolve(Paths.get("StandardAccountWithExtensions.page"));

        Report report = runRule(vfPagePath);
        List<RuleViolation> ruleViolations = report.getViolations();
        assertEquals(8, ruleViolations.size());
        int firstLineWithErrors = 9;
        for (int i = 0; i < ruleViolations.size(); i++) {
            RuleViolation ruleViolation = ruleViolations.get(i);
            assertEquals(EXPECTED_RULE_MESSAGE, ruleViolation.getDescription());
            assertEquals(firstLineWithErrors + i, ruleViolation.getBeginLine());
        }
    }

    /**
     * Runs a rule against a Visualforce page on the file system.
     */
    private Report runRule(Path vfPagePath) {
        Rule rule = findRule("category/vf/security.xml", "VfUnescapeEl");
        return VfParsingHelper.DEFAULT.executeRuleOnFile(rule, vfPagePath);
    }
}
