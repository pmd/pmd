/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestRule;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.junit.JavaUtilLoggingRule;

public class CPDCommandLineInterfaceTest {
    private static final String SRC_DIR = "src/test/resources/net/sourceforge/pmd/cpd/files/";

    @Rule
    public final TestRule restoreSystemProperties = new RestoreSystemProperties();
    @Rule
    public final SystemOutRule log = new SystemOutRule().enableLog().muteForSuccessfulTests();
    @Rule
    public final JavaUtilLoggingRule loggingRule = new JavaUtilLoggingRule(PMD.class.getPackage().getName()).mute();
    @Rule
    public TemporaryFolder tempDir = new TemporaryFolder();


    @Before
    public void setup() {
        System.setProperty(CPDCommandLineInterface.NO_EXIT_AFTER_RUN, "true");
    }
    
    @Test
    public void testEmptyResultRendering() {
        CPDCommandLineInterface.main(new String[] { "--minimum-tokens", "340", "--language", "java", "--files",
            SRC_DIR, "--format", "xml", });
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\n" + "<pmd-cpd/>", log.getLog());
    }

    @Test
    public void testDeprecatedOptionsWarning() throws IOException {
        File filelist = new File(tempDir.getRoot(), "cpd-test-file-list.txt");
        Files.write(filelist.toPath(), Arrays.asList(
                new File(SRC_DIR, "dup1.java").getAbsolutePath(),
                new File(SRC_DIR, "dup2.java").getAbsolutePath()), StandardCharsets.UTF_8);

        CPDCommandLineInterface.main(new String[] { "--minimum-tokens", "340", "--language", "java", "--filelist",
            filelist.getAbsolutePath(), "--format", "xml", "-failOnViolation", "true" });
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\n" + "<pmd-cpd/>", log.getLog());
        assertTrue(loggingRule.getLog().contains("Some deprecated options were used on the command-line, including -failOnViolation"));
        assertTrue(loggingRule.getLog().contains("Consider replacing it with --fail-on-violation"));
        // only one parameter is logged
        assertFalse(loggingRule.getLog().contains("Some deprecated options were used on the command-line, including --filelist"));
        assertFalse(loggingRule.getLog().contains("Consider replacing it with --file-list"));

    }
}
