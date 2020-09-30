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

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.StandardErrorStreamLog;
import org.junit.contrib.java.lang.system.StandardOutputStreamLog;
import org.junit.rules.TemporaryFolder;

import net.sourceforge.pmd.PMD;

public class PMDCoverageTest {

    @Rule
    public StandardOutputStreamLog output = new StandardOutputStreamLog();

    @Rule
    public StandardErrorStreamLog errorStream = new StandardErrorStreamLog();

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    /**
     * Test some of the PMD command line options
     */
    @Test
    public void testPmdOptions() {
        runPmd("-d src/main/java/net/sourceforge/pmd/lang/java/rule/design -f text -R rulesets/internal/all-java.xml -language java -stress -benchmark");
    }

    /**
     * Run the PMD command line tool, i.e. call PMD.main().
     *
     * @param commandLine
     */
    private void runPmd(String commandLine) {
        String[] args;
        args = commandLine.split("\\s");

        try {
            File f = folder.newFile();
            int n = args.length;
            String[] a = new String[n + 2 + 2];
            System.arraycopy(args, 0, a, 0, n);
            a[n] = "-reportfile";
            a[n + 1] = f.getAbsolutePath();
            a[n + 2] = "-threads";
            a[n + 3] = String.valueOf(Runtime.getRuntime().availableProcessors());
            args = a;

            PMD.run(args);

            assertEquals("Nothing should be output to stdout", 0, output.getLog().length());


            assertEquals("No exceptions expected", 0, StringUtils.countMatches(errorStream.getLog(), "Exception applying rule"));
            assertFalse("Wrong configuration? Ruleset not found", errorStream.getLog().contains("Ruleset not found"));
            assertEquals("No usage of deprecated XPath attributes expected", 0, StringUtils.countMatches(errorStream.getLog(), "Use of deprecated attribute"));

            String report = FileUtils.readFileToString(f, StandardCharsets.UTF_8);
            assertEquals("No processing errors expected", 0, StringUtils.countMatches(report, "Error while processing"));

            // we might have explicit examples of parsing errors, so these are maybe false positives
            assertEquals("No parsing error expected", 0, StringUtils.countMatches(report, "Error while parsing"));
        } catch (IOException ioe) {
            fail("Problem creating temporary file: " + ioe.getLocalizedMessage());
        }
    }

    @Test
    public void runAllJavaPmdOnSourceTree() {
        runPmd("-d src/main/java -f text -R rulesets/internal/all-java.xml -language java");
    }
}
