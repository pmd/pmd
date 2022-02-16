/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.rule.security;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.vf.VFTestUtils;
import net.sourceforge.pmd.lang.vf.ast.VfParsingHelper;
import net.sourceforge.pmd.testframework.PmdRuleTst;
import net.sourceforge.pmd.util.datasource.FileDataSource;

public class VfUnescapeElTest extends PmdRuleTst {
    public static final String EXPECTED_RULE_MESSAGE = "Avoid unescaped user controlled content in EL";

    /**
     * Verify that CustomFields stored in sfdx project format are correctly parsed
     */
    @Test
    public void testSfdxCustomFields() throws Exception {
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
            if ((ruleViolations.size() + firstLineWithErrors - 1) == expectedLineNumber) {
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
    public void testMdapiCustomFields() throws Exception {
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
    public void testApexController() throws Exception {
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
    public void testExtensions() throws Exception {
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
    private Report runRule(Path vfPagePath) throws Exception {
        Node node = VfParsingHelper.DEFAULT.parseFile(vfPagePath);
        assertNotNull(node);

        PMDConfiguration config = new PMDConfiguration();
        config.setIgnoreIncrementalAnalysis(true);
        // simple class loader, that doesn't delegate to parent.
        // this allows us in the tests to simulate PMD run without
        // auxclasspath, not even the classes from the test dependencies
        // will be found.
        config.setClassLoader(new ClassLoader() {
            @Override
            protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
                if (name.startsWith("java.") || name.startsWith("javax.")) {
                    return super.loadClass(name, resolve);
                }
                throw new ClassNotFoundException(name);
            }
        });
        Rule rule = findRule("category/vf/security.xml", "VfUnescapeEl");

        return PMD.processFiles(
            config,
            listOf(RuleSet.forSingleRule(rule)),
            listOf(new FileDataSource(vfPagePath.toAbsolutePath().toFile())),
            Collections.emptyList()
        );
    }
}
