/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemErrRule;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.rules.TemporaryFolder;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.junit.JavaUtilLoggingRule;

import com.google.common.collect.ImmutableMap;

public class CPDCommandLineInterfaceTest {
    private static final String SRC_DIR = "src/test/resources/net/sourceforge/pmd/cpd/files/";
    private static final Map<String, Integer> NUMBER_OF_TOKENS = ImmutableMap.of(
            new File(SRC_DIR, "dup1.java").getAbsolutePath(), 126,
            new File(SRC_DIR, "dup2.java").getAbsolutePath(), 126,
            new File(SRC_DIR, "file_with_ISO-8859-1_encoding.java").getAbsolutePath(), 32,
            new File(SRC_DIR, "file_with_utf8_bom.java").getAbsolutePath(), 29
    );

    @Rule
    public final SystemOutRule log = new SystemOutRule().enableLog().muteForSuccessfulTests();

    @Rule
    public final SystemErrRule errLog = new SystemErrRule().enableLog().muteForSuccessfulTests();

    @Rule
    public final JavaUtilLoggingRule loggingRule = new JavaUtilLoggingRule(PMD.class.getPackage().getName()).mute();
    @Rule
    public TemporaryFolder tempDir = new TemporaryFolder();


    private String getExpectedFileEntryXml(final String filename) {
        final int numberOfTokens = NUMBER_OF_TOKENS.get(filename);
        return String.format("   <file path=\"%s\"\n         totalNumberOfTokens=\"%d\"/>\n",
                new File(filename).getAbsolutePath(),
                numberOfTokens);
    }

    private String getExpectedFileEntriesXml(final Collection<String> filenames) {
        final StringBuilder expectedFilesXmlBuilder = new StringBuilder();
        for (final String filename : filenames) {
            expectedFilesXmlBuilder.append(getExpectedFileEntryXml(filename));
        }
        return expectedFilesXmlBuilder.toString();
    }

    @Test
    public void testEmptyResultRendering() {
        CPD.StatusCode statusCode = CPD.runCpd("--minimum-tokens", "340", "--language", "java", "--dir",
                SRC_DIR, "--format", "xml");
        final String expectedFilesXml = getExpectedFileEntriesXml(NUMBER_OF_TOKENS.keySet());
        assertEquals(CPD.StatusCode.OK, statusCode);
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\n" + "<pmd-cpd>" + "\n" + expectedFilesXml + "</pmd-cpd>", log.getLog());
    }

    @Test
    public void testDeprecatedOptionsWarning() throws IOException {
        final List<String> filepaths = Arrays.asList(
                new File(SRC_DIR, "dup1.java").getAbsolutePath(),
                new File(SRC_DIR, "dup2.java").getAbsolutePath());
        final Path filelist = tempDir.getRoot().toPath().resolve("cpd-test-file-list.txt");
        Files.write(filelist, filepaths, StandardCharsets.UTF_8);
        final String expectedFilesXml = getExpectedFileEntriesXml(filepaths);

        CPD.StatusCode statusCode = CPD.runCpd("--minimum-tokens", "340", "--language", "java", "--filelist",
                filelist.toAbsolutePath().toString(), "--format", "xml", "-failOnViolation", "true");
        assertEquals(CPD.StatusCode.OK, statusCode);
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\n" + "<pmd-cpd>" + "\n" + expectedFilesXml + "</pmd-cpd>", log.getLog());
        assertTrue(loggingRule.getLog().contains("Some deprecated options were used on the command-line, including -failOnViolation"));
        assertTrue(loggingRule.getLog().contains("Consider replacing it with --fail-on-violation"));
        // only one parameter is logged
        assertFalse(loggingRule.getLog().contains("Some deprecated options were used on the command-line, including --filelist"));
        assertFalse(loggingRule.getLog().contains("Consider replacing it with --file-list"));
    }

    @Test
    public void testDebugLogging() {
        CPD.StatusCode statusCode = CPD.runCpd("--minimum-tokens", "340", "--language", "java", "--dir",
                SRC_DIR, "--debug");
        assertEquals(CPD.StatusCode.OK, statusCode);
        assertThat(errLog.getLog(), containsString("Tokenizing ")); // this is a debug logging
    }

    @Test
    public void testNormalLogging() {
        loggingRule.clear();
        CPD.StatusCode statusCode = CPD.runCpd("--minimum-tokens", "340", "--language", "java", "--dir",
                SRC_DIR);
        assertEquals(CPD.StatusCode.OK, statusCode);
        assertThat(errLog.getLog(), not(containsString("Tokenizing "))); // this is a debug logging
    }
}
