/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.coverage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemErrRule;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.rules.TemporaryFolder;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.java.JavaLanguageModule;

public class PMDCoverageTest {

    @Rule
    public SystemOutRule output = new SystemOutRule().muteForSuccessfulTests().enableLog();

    @Rule
    public SystemErrRule errorStream = new SystemErrRule().muteForSuccessfulTests().enableLog();

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testPmdOptions() {
        runPmd("-d src/main/java/net/sourceforge/pmd/lang/java/rule/design -f text -R rulesets/internal/all-java.xml -stress -benchmark");
    }


    @Test
    public void runAllJavaPmdOnSourceTree() {
        runPmd("-d src/main/java -f text -R rulesets/internal/all-java.xml");
    }

    @Test
    public void runAllJavaPmdOnTestResourcesWithLatestJavaVersion() {
        List<LanguageVersion> versions = LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getVersions();
        LanguageVersion latest = versions.get(versions.size() - 1);

        runPmd("-d src/test/resources -f text -R rulesets/internal/all-java.xml -language java -version " + latest.getVersion());
    }

    /**
     * Run the PMD command line tool, i.e. call PMD.main().
     *
     * @param commandLine
     */
    private void runPmd(String commandLine) {
        String[] args = commandLine.split("\\s");
        String report = "missing report";

        try {

            File f = folder.newFile();
            args = ArrayUtils.addAll(
                args,
                "-reportfile",
                f.getAbsolutePath(),
                "-threads",
                String.valueOf(Runtime.getRuntime().availableProcessors())
            );

            System.err.println("Running PMD with: " + Arrays.toString(args));
            PMD.runPmd(args);
            report = FileUtils.readFileToString(f, StandardCharsets.UTF_8);

            assertEquals("Nothing should be output to stdout", 0, output.getLog().length());

            assertEquals("No exceptions expected", 0, StringUtils.countMatches(errorStream.getLog(), "Exception applying rule"));
            assertFalse("Wrong configuration? Ruleset not found", errorStream.getLog().contains("Ruleset not found"));
            assertEquals("No usage of deprecated XPath attributes expected", 0, StringUtils.countMatches(errorStream.getLog(), "Use of deprecated attribute"));

            assertEquals("No processing errors expected", 0, StringUtils.countMatches(report, "Error while processing"));

            // we might have explicit examples of parsing errors, so these are maybe false positives
            assertEquals("No parsing error expected", 0, StringUtils.countMatches(report, "Error while parsing"));
        } catch (IOException ioe) {
            fail("Problem creating temporary file: " + ioe.getLocalizedMessage());
        } catch (AssertionError ae) {
            System.out.println("\nReport:\n");
            System.out.println(report);
            throw ae;
        }
    }
}
