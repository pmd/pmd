/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.rule.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Test;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.RulesetsFactoryUtils;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.vf.VFTestUtils;
import net.sourceforge.pmd.lang.vf.VfLanguageModule;
import net.sourceforge.pmd.testframework.PmdRuleTst;

public class VfUnescapeElTest extends PmdRuleTst {
    public static final String EXPECTED_RULE_MESSAGE = "Avoid unescaped user controlled content in EL";

    /**
     * Verify that CustomFields stored in sfdx project format are correctly parsed
     */
    @Test
    public void testSfdxCustomFields() throws IOException, PMDException {
        Path vfPagePath = VFTestUtils.getMetadataPath(this, VFTestUtils.MetadataFormat.SFDX, VFTestUtils.MetadataType.Vf)
                .resolve("StandardAccount.page");

        Report report = runRule(vfPagePath);
        List<RuleViolation> ruleViolations = report.getViolations();
        assertEquals(6, ruleViolations.size());
        int firstLineWithErrors = 7;
        for (int i = 0; i < ruleViolations.size(); i++) {
            RuleViolation ruleViolation = ruleViolations.get(i);
            assertEquals(EXPECTED_RULE_MESSAGE, ruleViolation.getDescription());
            assertEquals(firstLineWithErrors + i, ruleViolation.getBeginLine());
        }
    }

    /**
     * Verify that CustomFields stored in mdapi format are correctly parsed
     */
    @Test
    public void testMdapiCustomFields() throws IOException, PMDException {
        Path vfPagePath = VFTestUtils.getMetadataPath(this, VFTestUtils.MetadataFormat.MDAPI, VFTestUtils.MetadataType.Vf).resolve("StandardAccount.page");

        Report report = runRule(vfPagePath);
        List<RuleViolation> ruleViolations = report.getViolations();
        assertEquals(6, ruleViolations.size());
        int firstLineWithErrors = 8;
        for (int i = 0; i < ruleViolations.size(); i++) {
            RuleViolation ruleViolation = ruleViolations.get(i);
            assertEquals(EXPECTED_RULE_MESSAGE, ruleViolation.getDescription());
            assertEquals(firstLineWithErrors + i, ruleViolation.getBeginLine());
        }
    }

    /**
     * Tests a page with a single Apex controller
     */
    @Test
    public void testApexController() throws IOException, PMDException {
        Path vfPagePath = VFTestUtils.getMetadataPath(this, VFTestUtils.MetadataFormat.SFDX, VFTestUtils.MetadataType.Vf).resolve("ApexController.page");

        Report report = runRule(vfPagePath);
        List<RuleViolation> ruleViolations = report.getViolations();
        assertEquals(2, ruleViolations.size());
        int firstLineWithErrors = 9;
        for (int i = 0; i < ruleViolations.size(); i++) {
            // There should start at line 9
            RuleViolation ruleViolation = ruleViolations.get(i);
            assertEquals(EXPECTED_RULE_MESSAGE, ruleViolation.getDescription());
            assertEquals(firstLineWithErrors + i, ruleViolation.getBeginLine());
        }
    }

    /**
     * Tests a page with a standard controller and two Apex extensions
     */
    @Test
    public void testExtensions() throws IOException, PMDException {
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
     * Runs a rule against a Visualforce page on the file system. This code is based on
     * {@link net.sourceforge.pmd.testframework.RuleTst#runTestFromString(String, Rule, Report, LanguageVersion, boolean)}
     */
    private Report runRule(Path vfPagePath) throws FileNotFoundException, PMDException {
        LanguageVersion languageVersion = LanguageRegistry.getLanguage(VfLanguageModule.NAME).getDefaultVersion();
        ParserOptions parserOptions = languageVersion.getLanguageVersionHandler().getDefaultParserOptions();
        Parser parser = languageVersion.getLanguageVersionHandler().getParser(parserOptions);

        Node node = parser.parse(vfPagePath.toString(), new FileReader(vfPagePath.toFile()));
        assertNotNull(node);

        // BEGIN Based on RuleTst class
        PMD p = new PMD();
        p.getConfiguration().setDefaultLanguageVersion(languageVersion);
        p.getConfiguration().setIgnoreIncrementalAnalysis(true);
        // simple class loader, that doesn't delegate to parent.
        // this allows us in the tests to simulate PMD run without
        // auxclasspath, not even the classes from the test dependencies
        // will be found.
        p.getConfiguration().setClassLoader(new ClassLoader() {
            @Override
            protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
                if (name.startsWith("java.") || name.startsWith("javax.")) {
                    return super.loadClass(name, resolve);
                }
                throw new ClassNotFoundException(name);
            }
        });

        Rule rule = findRule("category/vf/security.xml", "VfUnescapeEl");
        Report report = new Report();
        RuleContext ctx = new RuleContext();
        ctx.setReport(report);
        ctx.setSourceCodeFile(vfPagePath.toFile());
        ctx.setLanguageVersion(languageVersion);
        ctx.setIgnoreExceptions(false);
        RuleSet rules = RulesetsFactoryUtils.defaultFactory().createSingleRuleRuleSet(rule);
        p.getSourceCodeProcessor().processSourceCode(new FileReader(vfPagePath.toFile()), new RuleSets(rules), ctx);
        // END Based on RuleTst class

        return report;
    }
}
