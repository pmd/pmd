/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.coverage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.StandardOutputStreamLog;

import net.sourceforge.pmd.PMD;

public class PMDCoverageTest {

    @Rule
    public StandardOutputStreamLog output = new StandardOutputStreamLog();

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

        File f = null;
        try {
            f = File.createTempFile("pmd", ".txt");
            int n = args.length;
            String[] a = new String[n + 2];
            System.arraycopy(args, 0, a, 0, n);
            a[n] = "-reportfile";
            a[n + 1] = f.getAbsolutePath();
            args = a;

            PMD.run(args);

            assertFalse("There was at least one exception mentioned in the output", output.getLog().contains("Exception applying rule"));
            assertFalse("Wrong configuration? Ruleset not found", output.getLog().contains("Ruleset not found"));
            assertFalse("Some XPath rules use deprecated attributes", output.getLog().contains("Use of deprecated attribute"));

            String report = FileUtils.readFileToString(f);
            assertEquals("No processing errors expected", 0, StringUtils.countMatches(report, "Error while processing"));

            // we might have explicit examples of parsing errors, so these are maybe false positives
            assertEquals("No parsing error expected", 0, StringUtils.countMatches(report, "Error while parsing"));
        } catch (IOException ioe) {
            fail("Problem creating temporary file: " + ioe.getLocalizedMessage());
        } finally {
            if (f != null) {
                f.delete();
            }
        }
    }

    /**
     * Name of the configuration file used by testResourceFileCommands().
     */
    private static final String PMD_CONFIG_FILE = "pmd_tests.conf";

    /**
     * Run PMD using the command lines found in PMD_CONFIG_FILE.
     */
    @Test
    public void testResourceFileCommands() {

        InputStream is = getClass().getResourceAsStream(PMD_CONFIG_FILE);

        if (is != null) {
            try {
                BufferedReader r = new BufferedReader(new InputStreamReader(is));
                String l;
                while ((l = r.readLine()) != null) {
                    l = l.trim();
                    if (l.length() == 0 || l.charAt(0) == '#') {
                        continue;
                    }

                    runPmd(l);
                }
                r.close();
            } catch (IOException ioe) {
                fail("Problem reading config file: " + ioe.getLocalizedMessage());
            }
        } else {
            fail("Missing config file: " + PMD_CONFIG_FILE);
        }
    }
}
