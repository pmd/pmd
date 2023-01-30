/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.coverage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.internal.util.IOUtil;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.java.JavaLanguageModule;

import com.github.stefanbirkner.systemlambda.SystemLambda;

// TODO enable test
@Disabled("Test is failing and was excluded on PMD 7 branch")
class PMDCoverageTest {

    @TempDir
    private Path tempFolder;

    @Test
    void testPmdOptions() {
        runPmd("-d src/main/java/net/sourceforge/pmd/lang/java/rule/design -f text -R rulesets/internal/all-java.xml -stress -benchmark");
    }


    @Test
    void runAllJavaPmdOnSourceTree() {
        runPmd("-d src/main/java -f text -R rulesets/internal/all-java.xml");
    }

    @Test
    void runAllJavaPmdOnTestResourcesWithLatestJavaVersion() {
        List<LanguageVersion> versions = JavaLanguageModule.getInstance().getVersions();
        LanguageVersion latest = versions.get(versions.size() - 1);

        runPmd("-d src/test/resources -f text -R rulesets/internal/all-java.xml -language java -version " + latest.getVersion());
    }

    /**
     * Run the PMD command line tool, i.e. call PMD.main().
     *
     * @param commandLine
     */
    private void runPmd(String commandLine) {
        StringBuilder report = new StringBuilder("missing report");

        try {
            Path f = Files.createTempFile(tempFolder, PMDCoverageTest.class.getSimpleName(), null);
            String[] args = ArrayUtils.addAll(
                commandLine.split("\\s"),
                "-reportfile",
                f.toAbsolutePath().toString(),
                "-threads",
                String.valueOf(Runtime.getRuntime().availableProcessors())
            );

            System.err.println("Running PMD with: " + Arrays.toString(args));
            String output = SystemLambda.tapSystemOut(() -> {
                String errorOutput = SystemLambda.tapSystemErr(() -> {
                    PMD.runPmd(args);

                    report.setLength(0);
                    report.append(IOUtil.readFileToString(f.toFile(), StandardCharsets.UTF_8));
                });
                assertEquals(0, StringUtils.countMatches(errorOutput, "Exception applying rule"), "No exceptions expected");
                assertFalse(errorOutput.contains("Ruleset not found"), "Wrong configuration? Ruleset not found");
                assertEquals(0, StringUtils.countMatches(errorOutput, "Use of deprecated attribute"), "No usage of deprecated XPath attributes expected");
            });

            assertEquals(0, output.length(), "Nothing should be output to stdout");

            assertEquals(0, StringUtils.countMatches(report, "Error while processing"), "No processing errors expected");

            // we might have explicit examples of parsing errors, so these are maybe false positives
            assertEquals(0, StringUtils.countMatches(report, "Error while parsing"), "No parsing error expected");
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
