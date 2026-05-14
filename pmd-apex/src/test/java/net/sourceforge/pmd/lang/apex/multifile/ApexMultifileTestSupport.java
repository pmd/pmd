/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.multifile;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.PmdAnalysis;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.apex.ApexLanguageModule;
import net.sourceforge.pmd.lang.apex.ApexLanguageProperties;
import net.sourceforge.pmd.lang.rule.Rule;
import net.sourceforge.pmd.lang.rule.RuleSet;
import net.sourceforge.pmd.lang.rule.RuleSetLoader;
import net.sourceforge.pmd.reporting.GlobalAnalysisListener;
import net.sourceforge.pmd.reporting.Report;
import net.sourceforge.pmd.reporting.RuleViolation;

import com.nawforce.pkgforce.path.PathLike;
import com.nawforce.runtime.platform.Environment;
import scala.Option;

public final class ApexMultifileTestSupport {
    private ApexMultifileTestSupport() {}

    public static void assertViolation(RuleViolation violation, String fileName, int lineNumber) {
        assertEquals(fileName, violation.getFileId().getFileName());
        assertEquals(lineNumber, violation.getBeginLine());
    }

    public static Report runRule(Path tempDir, Path testProjectDir, String categoryName, String ruleName) throws IOException {
        Option<PathLike> pathLikeOption = Option.apply(new com.nawforce.runtime.platform.Path(tempDir));
        Option<Option<PathLike>> cacheDirOption = Option.apply(pathLikeOption);
        Environment.setCacheDirOverride(cacheDirOption);

        Language apexLanguage = ApexLanguageModule.getInstance();
        LanguageVersion languageVersion = apexLanguage.getDefaultVersion();
        PMDConfiguration configuration = new PMDConfiguration();
        configuration.setIgnoreIncrementalAnalysis(true);
        configuration.setDefaultLanguageVersion(languageVersion);
        configuration.setThreads(0); // don't use separate threads

        configuration.getLanguageProperties(apexLanguage)
                .setProperty(ApexLanguageProperties.MULTIFILE_DIRECTORY, Optional.of(testProjectDir.toString()));

        RuleSet parsedRset = new RuleSetLoader().warnDeprecated(false).loadFromResource("category/apex/" + categoryName + ".xml");
        Rule rule = parsedRset.getRuleByName(ruleName);

        try (PmdAnalysis pmd = PmdAnalysis.create(configuration)) {
            pmd.files().addDirectory(testProjectDir);
            pmd.addRuleSet(RuleSet.forSingleRule(rule));
            pmd.addListener(GlobalAnalysisListener.exceptionThrower());
            return pmd.performAnalysisAndCollectReport();
        }
    }
}
