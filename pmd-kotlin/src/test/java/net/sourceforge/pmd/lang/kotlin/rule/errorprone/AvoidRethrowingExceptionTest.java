/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.rule.errorprone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.PmdAnalysis;
import net.sourceforge.pmd.lang.JvmLanguagePropertyBundle;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.document.FileId;
import net.sourceforge.pmd.lang.rule.Rule;
import net.sourceforge.pmd.lang.rule.RuleSet;
import net.sourceforge.pmd.lang.rule.RuleSetLoader;
import net.sourceforge.pmd.reporting.Report;
import net.sourceforge.pmd.test.PmdRuleTst;

class AvoidRethrowingExceptionTest extends PmdRuleTst {
    // Basic cases in xml/AvoidRethrowingException.xml

    @Test
    void kotlinxCancellationExceptionRethrowIsNotAViolation() {
        // kotlinx.coroutines.CancellationException is a Kotlin typealias for
        // java.util.concurrent.CancellationException — no dedicated Java class exists in the JAR.
        // Locate the JAR by scanning the test classpath instead.
        String kotlinxJarPath = Arrays.stream(System.getProperty("java.class.path").split(File.pathSeparator))
                .filter(e -> e.contains("kotlinx-coroutines-core-jvm"))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "kotlinx-coroutines-core-jvm not found on test classpath"));
        File kotlinxJar = new File(kotlinxJarPath);

        String code =
                "import kotlinx.coroutines.CancellationException\n"
                + "fun check() {\n"
                + "    try { risky() }\n"
                + "    catch (e: CancellationException) { throw e }\n"
                + "    catch (e: Exception) { println(\"handled\") }\n"
                + "}\n"
                + "fun risky() {}\n";

        Language kotlin = LanguageRegistry.PMD.getLanguageById("kotlin");
        PMDConfiguration config = new PMDConfiguration();
        config.setIgnoreIncrementalAnalysis(true);
        config.setDefaultLanguageVersion(kotlin.getDefaultVersion());
        config.getLanguageProperties(kotlin).setProperty(
                JvmLanguagePropertyBundle.AUX_CLASSPATH, kotlinxJar.getAbsolutePath());

        RuleSet ruleSet = new RuleSetLoader().loadFromResource("category/kotlin/errorprone.xml");
        Rule rule = ruleSet.getRuleByName("AvoidRethrowingException");
        assertNotNull(rule, "AvoidRethrowingException rule not found in errorprone.xml");

        try (PmdAnalysis pmd = PmdAnalysis.create(config)) {
            pmd.addRuleSet(RuleSet.forSingleRule(rule));
            pmd.files().addSourceFile(FileId.fromPathLikeString("snippet.kt"), code);
            Report report = pmd.performAnalysisAndCollectReport();
            assertTrue(report.getProcessingErrors().isEmpty(),
                    "No processing errors expected: " + report.getProcessingErrors());
            assertEquals(0, report.getViolations().size(),
                    "Expected 0 violations for kotlinx.coroutines.CancellationException rethrow, but got: "
                    + report.getViolations());
        }
    }
}
