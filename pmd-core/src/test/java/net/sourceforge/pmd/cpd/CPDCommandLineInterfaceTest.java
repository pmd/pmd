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
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemErrRule;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.rules.TemporaryFolder;

public class CPDCommandLineInterfaceTest {
    private static final String SRC_DIR = "src/test/resources/net/sourceforge/pmd/cpd/files/";

    @Rule
    public final SystemOutRule log = new SystemOutRule().enableLog().muteForSuccessfulTests();
    @Rule
    public final SystemErrRule systemErrRule = new SystemErrRule().muteForSuccessfulTests().enableLog();
    @Rule
    public TemporaryFolder tempDir = new TemporaryFolder();


    @Test
    public void testEmptyResultRendering() {
        CPD.StatusCode statusCode = CPD.runCpd("--minimum-tokens", "340", "--language", "java", "--files",
                SRC_DIR, "--format", "xml");
        Assert.assertEquals(CPD.StatusCode.OK, statusCode);
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\n" + "<pmd-cpd/>", log.getLog().trim());
    }

    @Test
    public void testDeprecatedOptionsWarning() throws IOException {
        File filelist = new File(tempDir.getRoot(), "cpd-test-file-list.txt");
        Files.write(filelist.toPath(), Arrays.asList(
                new File(SRC_DIR, "dup1.java").getAbsolutePath(),
                new File(SRC_DIR, "dup2.java").getAbsolutePath()), StandardCharsets.UTF_8);

        CPD.StatusCode statusCode = CPD.runCpd("--minimum-tokens", "340", "--language", "java", "--filelist",
                filelist.getAbsolutePath(), "--format", "xml", "-failOnViolation", "true");
        Assert.assertEquals(CPD.StatusCode.OK, statusCode);
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\n" + "<pmd-cpd/>", log.getLog().trim());
        assertTrue(systemErrRule.getLog().contains("Some deprecated options were used on the command-line, including -failOnViolation"));
        assertTrue(systemErrRule.getLog().contains("Consider replacing it with --fail-on-violation"));
        // only one parameter is logged
        assertFalse(systemErrRule.getLog().contains("Some deprecated options were used on the command-line, including --filelist"));
        assertFalse(systemErrRule.getLog().contains("Consider replacing it with --file-list"));
    }
}
