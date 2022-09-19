/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import net.sourceforge.pmd.cli.internal.ExecutionResult;
import net.sourceforge.pmd.internal.Slf4jSimpleConfiguration;

import com.github.stefanbirkner.systemlambda.SystemLambda;

class CpdCliTest extends BaseCliTest {
    private static final String SRC_DIR = "src/test/resources/net/sourceforge/pmd/cpd/files/";

    @TempDir
    private Path tempDir;

    @AfterAll
    static void resetLogging() {
        // reset logging in case "--debug" changed the logging properties
        // See also Slf4jSimpleConfigurationForAnt
        Slf4jSimpleConfiguration.reconfigureDefaultLogLevel(null);
    }

    @Test
    void debugLogging() throws Exception {
        // restoring system properties: --debug might change logging properties
        SystemLambda.restoreSystemProperties(() -> {
            String log = runCliSuccessfully("--debug", "--minimum-tokens", "340", "--dir", SRC_DIR);
            assertThat(log, containsString("[main] INFO net.sourceforge.pmd.cli.commands.internal.AbstractPmdSubcommand - Log level is at TRACE"));
        });
    }

    @Test
    void defaultLogging() throws Exception {
        String log = runCliSuccessfully("--minimum-tokens", "340", "--dir", SRC_DIR);
        assertThat(log, containsString("[main] INFO net.sourceforge.pmd.cli.commands.internal.AbstractPmdSubcommand - Log level is at INFO"));
    }
    
    @Test
    void testMissingminimumTokens() throws Exception {
        final String log = runCli(ExecutionResult.USAGE_ERROR);
        assertThat(log, containsString("Missing required option: '--minimum-tokens=<minimumTokens>'"));
    }
    
    @Test
    void testMissingSource() throws Exception {
        final String log = runCli(ExecutionResult.USAGE_ERROR, "--minimum-tokens", "340");
        assertThat(log, containsString("Please provide a parameter for source root directory"));
    }
    
    @Test
    void testWrongCliOptionsDoPrintUsage() throws Exception {
        final String log = runCli(ExecutionResult.USAGE_ERROR, "--invalid", "--minimum-tokens", "340", "-d", SRC_DIR);
        assertThat(log, containsString("Unknown option: '--invalid'"));
        assertThat(log, containsString("Usage: pmd cpd"));
    }
    
    @Test
    void testNoDuplicatesResultRendering() throws Exception {
        final String stdout = SystemLambda.tapSystemOut(() -> {
            SystemLambda.tapSystemErr(() -> {
                final int statusCode = SystemLambda.catchSystemExit(() -> {
                    PmdCli.main(new String[] {
                        "cpd", "--minimum-tokens", "340", "--language", "java", "--dir",
                        SRC_DIR, "--format", "xml",
                    });
                });
                assertEquals(ExecutionResult.OK.getExitCode(), statusCode);
            });
        });
        final Path absoluteSrcDir = Paths.get(SRC_DIR).toAbsolutePath();
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<pmd-cpd>\n"
                + "   <file path=\"" + absoluteSrcDir.resolve("dup1.java").toString() + "\"\n"
                + "         totalNumberOfTokens=\"89\"/>\n"
                + "   <file path=\"" + absoluteSrcDir.resolve("dup2.java").toString() + "\"\n"
                + "         totalNumberOfTokens=\"89\"/>\n"
                + "   <file path=\"" + absoluteSrcDir.resolve("file_with_ISO-8859-1_encoding.java").toString() + "\"\n"
                + "         totalNumberOfTokens=\"8\"/>\n"
                + "   <file path=\"" + absoluteSrcDir.resolve("file_with_utf8_bom.java").toString() + "\"\n"
                + "         totalNumberOfTokens=\"9\"/>\n"
                + "</pmd-cpd>", stdout.trim());
    }

    @Override
    protected List<String> cliStandardArgs() {
        final List<String> argList = new ArrayList<>();
        
        argList.add("cpd");
        
        return argList;
    }
}
