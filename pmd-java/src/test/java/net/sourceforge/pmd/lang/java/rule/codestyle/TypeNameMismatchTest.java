/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.PmdAnalysis;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.document.FileId;
import net.sourceforge.pmd.lang.document.TextFile;
import net.sourceforge.pmd.lang.java.JavaLanguageModule;
import net.sourceforge.pmd.lang.rule.Rule;
import net.sourceforge.pmd.lang.rule.RuleSet;
import net.sourceforge.pmd.reporting.GlobalAnalysisListener;
import net.sourceforge.pmd.reporting.Report;
import net.sourceforge.pmd.test.PmdRuleTst;

/**
 * Tests for {@link TypeNameMismatchRule}.
 */
class TypeNameMismatchTest extends PmdRuleTst {

    @Test
    void runTestFromString() {
        PMDConfiguration configuration = new PMDConfiguration();
        configuration.setIgnoreIncrementalAnalysis(true);
        final List<Rule> rules = new ArrayList<>(getRules());
        LanguageVersion languageVersion = JavaLanguageModule.getInstance().getVersion("27");
        configuration.setDefaultLanguageVersion(languageVersion);
        configuration.setThreads(0); // don't use separate threads

        // Java-specific configuration

        try (PmdAnalysis pmd = PmdAnalysis.create(configuration)) {
            pmd.files().addFile(TextFile.forCharSeq("class Foo {}", FileId.fromPathLikeString("Foo.java"), languageVersion));

            pmd.addRuleSet(RuleSet.forSingleRule(rules.get(0)));
            pmd.addListener(GlobalAnalysisListener.exceptionThrower());
            Report report = pmd.performAnalysisAndCollectReport();
            assertEquals(0, report.getViolations().size());
        }
    }
}
