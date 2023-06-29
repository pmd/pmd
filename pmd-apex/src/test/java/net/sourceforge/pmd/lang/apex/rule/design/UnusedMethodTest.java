/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.design;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.PmdAnalysis;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetLoader;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.apex.ApexLanguageModule;
import net.sourceforge.pmd.lang.apex.ApexLanguageProperties;
import net.sourceforge.pmd.reporting.GlobalAnalysisListener;

class UnusedMethodTest {

    @Test
    void findUnusedMethodsWithSfdxProject() throws Exception {
        Path testProjectDir = Paths.get("src/test/resources/net/sourceforge/pmd/lang/apex/rule/design/UnusedMethod/project1");
        Report report = runRule(testProjectDir);
        assertEquals(1, report.getViolations().size());
        assertViolation(report.getViolations().get(0), "Foo.cls", 6);
    }

    private void assertViolation(RuleViolation violation, String fileName, int lineNumber) {
        assertEquals("Foo.cls", violation.getFileId().getFileName());
        assertEquals(6, violation.getBeginLine()); // line 6 is method unusedMethod()
    }

    private Report runRule(Path testProjectDir) throws IOException {
        Language apexLanguage = ApexLanguageModule.getInstance();
        LanguageVersion languageVersion = apexLanguage.getDefaultVersion();
        PMDConfiguration configuration = new PMDConfiguration();
        configuration.setIgnoreIncrementalAnalysis(true);
        configuration.setDefaultLanguageVersion(languageVersion);
        configuration.setThreads(0); // don't use separate threads
        configuration.prependAuxClasspath(".");

        configuration.getLanguageProperties(apexLanguage).setProperty(ApexLanguageProperties.MULTIFILE_DIRECTORY, testProjectDir.toString());

        RuleSet parsedRset = new RuleSetLoader().warnDeprecated(false).loadFromResource("category/apex/design.xml");
        Rule rule = parsedRset.getRuleByName("UnusedMethod");

        try (PmdAnalysis pmd = PmdAnalysis.create(configuration)) {
            pmd.files().addDirectory(testProjectDir);
            pmd.addRuleSet(RuleSet.forSingleRule(rule));
            pmd.addListener(GlobalAnalysisListener.exceptionThrower());
            return pmd.performAnalysisAndCollectReport();
        }
    }
}
