/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import net.sourceforge.pmd.internal.Slf4jSimpleConfiguration;

import com.github.stefanbirkner.systemlambda.SystemLambda;
import com.google.common.collect.ImmutableMap;

class CPDCommandLineInterfaceTest {
    private static final String SRC_DIR = "src/test/resources/net/sourceforge/pmd/cpd/files/";
    private static final Map<String, Integer> NUMBER_OF_TOKENS = ImmutableMap.of(
            new File(SRC_DIR, "dup1.java").getAbsolutePath(), 126,
            new File(SRC_DIR, "dup2.java").getAbsolutePath(), 126,
            new File(SRC_DIR, "file_with_ISO-8859-1_encoding.java").getAbsolutePath(), 32,
            new File(SRC_DIR, "file_with_utf8_bom.java").getAbsolutePath(), 29
    );

    @TempDir
    private Path tempDir;

    @AfterEach
    void resetLogging() {
        // reset logging in case "--debug" changed the logging properties
        // See also Slf4jSimpleConfigurationForAnt
        Slf4jSimpleConfiguration.reconfigureDefaultLogLevel(null);
    }

    @BeforeEach
    void setup() {
        System.setProperty(CPDCommandLineInterface.NO_EXIT_AFTER_RUN, "true");
    }
    
    @Test
    void testEmptyResultRendering() throws Exception {
        final String expectedFilesXml = getExpectedFileEntriesXml(NUMBER_OF_TOKENS.keySet());
        String stdout = SystemLambda.tapSystemOut(() -> {
            SystemLambda.tapSystemErr(() -> {
                CPD.StatusCode statusCode = CPD.runCpd("--minimum-tokens", "340", "--language", "java", "--files",
                        SRC_DIR, "--format", "xml");
                assertEquals(CPD.StatusCode.OK, statusCode);
            });
        });
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\n" + "<pmd-cpd>\n" + expectedFilesXml + "</pmd-cpd>", stdout.trim());
    }

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


}
