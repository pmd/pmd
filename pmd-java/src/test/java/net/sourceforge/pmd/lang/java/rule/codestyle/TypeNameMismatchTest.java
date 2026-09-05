/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
import net.sourceforge.pmd.reporting.RuleViolation;
import net.sourceforge.pmd.test.PmdRuleTst;

/**
 * Tests for {@link TypeNameMismatchRule}.
 */
class TypeNameMismatchTest extends PmdRuleTst {

    @Test
    void packageAndClassNameMatch() {
        assertViolationsFoCodeInFile(Collections.emptyList(),
                "package foo.bar; class Foo {}",
                "foo/bar/Foo.java"
        );
    }

    @Test
    void packageAndClassNameMismatch() {
        assertViolationsFoCodeInFile(Arrays.asList("File path does not match package foo.baz",
                        "Top-level type Bar should be defined in a file called Bar.java"),
                "package foo.baz; class Bar {}",
                "foo/bar/Baz.java"
        );
    }

    private void assertViolationsFoCodeInFile(List<String> messages, String code, String filename) {
        PMDConfiguration configuration = new PMDConfiguration();
        configuration.setIgnoreIncrementalAnalysis(true);
        final List<Rule> rules = new ArrayList<>(getRules());
        LanguageVersion languageVersion = JavaLanguageModule.getInstance().getVersion("27");
        configuration.setDefaultLanguageVersion(languageVersion);
        configuration.setThreads(0); // don't use separate threads

        // Java-specific configuration

        try (PmdAnalysis pmd = PmdAnalysis.create(configuration)) {
            pmd.files().addFile(TextFile.forCharSeq(code, FileId.fromPathLikeString(filename), languageVersion));

            pmd.addRuleSet(RuleSet.forSingleRule(rules.get(0)));
            pmd.addListener(GlobalAnalysisListener.exceptionThrower());
            Report report = pmd.performAnalysisAndCollectReport();
            assertEquals(messages, report.getViolations().stream()
                    .map(RuleViolation::getDescription).collect(Collectors.toList()));
        }
    }
}
