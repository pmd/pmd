/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.PmdAnalysis;
import net.sourceforge.pmd.internal.util.IOUtil;
import net.sourceforge.pmd.lang.LanguageVersion;

import com.github.stefanbirkner.systemlambda.SystemLambda;

class PMDCoverageTest {

    @TempDir
    private Path tempFolder;

    @Test
    void runAllJavaPmdOnSourceTree() {
        runPmd("src/main/java", conf -> {});
    }

    @Test
    void runAllJavaPmdOnTestResourcesWithLatestJavaVersion() {
        LanguageVersion latest = JavaLanguageModule.getInstance().getLatestVersion();

        runPmd("src/test/resources", conf -> conf.setDefaultLanguageVersion(latest));
    }

    /**
     * Run the PMD command line tool, i.e. call PMD.main().
     */
    private void runPmd(String inputPath, Consumer<PMDConfiguration> configure) {
        StringBuilder report = new StringBuilder("missing report");

        try {
            Path f = Files.createTempFile(tempFolder, PMDCoverageTest.class.getSimpleName(), null);

            String output = SystemLambda.tapSystemOut(() -> {
                String errorOutput = SystemLambda.tapSystemErr(() -> {
                    PMDConfiguration conf = new PMDConfiguration();
                    conf.addInputPath(Paths.get(inputPath));
                    conf.setReportFile(f);
                    conf.addRuleSet("rulesets/internal/all-java.xml");
                    conf.setThreads(Runtime.getRuntime().availableProcessors());
                    configure.accept(conf);

                    try (PmdAnalysis pmd = PmdAnalysis.create(conf)) {
                        pmd.performAnalysis();
                    }

                    report.setLength(0);
                    report.append(IOUtil.readFileToString(f.toFile(), StandardCharsets.UTF_8));
                });
                assertThat(errorOutput, not(containsString("Exception applying rule")));
                assertThat(errorOutput, not(containsString("Ruleset not found")));
                assertThat(errorOutput, not(containsString("Use of deprecated attribute")));
            });

            assertThat(output, is(emptyString()));

            // No processing errors expected
            assertThat(report.toString(), not(containsString("Error while processing")));
            // we might have explicit examples of parsing errors, so these are maybe false positives
            // these examples of parsing errors need to be excluded in rulesets/internal/all-java.xml via
            // exclude-patterns.
            assertThat(report.toString(), not(containsString("Error while parsing")));
        } catch (IOException ioe) {
            fail("Problem creating temporary file: " + ioe.getLocalizedMessage());
        } catch (AssertionError ae) {
            System.out.println("\nReport:\n");
            System.out.println(report);
            throw ae;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
