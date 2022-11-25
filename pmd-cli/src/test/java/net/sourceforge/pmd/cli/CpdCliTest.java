/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli;

import static net.sourceforge.pmd.cli.internal.CliExitCode.OK;
import static net.sourceforge.pmd.cli.internal.CliExitCode.VIOLATIONS_FOUND;
import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import net.sourceforge.pmd.cli.internal.CliExitCode;
import net.sourceforge.pmd.internal.Slf4jSimpleConfiguration;

import com.github.stefanbirkner.systemlambda.SystemLambda;

class CpdCliTest extends BaseCliTest {

    private static final String BASE_RES_PATH = "src/test/resources/net/sourceforge/pmd/cli/cpd/";
    private static final String SRC_DIR = BASE_RES_PATH + "files/";

    @TempDir
    private Path tempDir;

    @Override
    protected List<String> cliStandardArgs() {
        return listOf("cpd");
    }

    @AfterAll
    static void resetLogging() {
        // reset logging in case "--debug" changed the logging properties
        // See also Slf4jSimpleConfigurationForAnt
        Slf4jSimpleConfiguration.reconfigureDefaultLogLevel(null);
    }

    @Test
    void debugLogging() throws Exception {
        CliExecutionResult result = runCliSuccessfully("--debug", "--minimum-tokens", "340", "--dir", SRC_DIR);
        result.checkStdOut(containsString("[main] INFO net.sourceforge.pmd.cli.commands.internal.AbstractPmdSubcommand - Log level is at TRACE"));
    }

    @Test
    void defaultLogging() throws Exception {
        CliExecutionResult result = runCliSuccessfully("--minimum-tokens", "340", "--dir", SRC_DIR);
        result.checkStdOut(containsString("[main] INFO net.sourceforge.pmd.cli.commands.internal.AbstractPmdSubcommand - Log level is at INFO"));
    }

    @Test
    void testMissingMinimumTokens() throws Exception {
        final CliExecutionResult result = runCli(CliExitCode.USAGE_ERROR);
        result.checkStdErr(containsString("Missing required option: '--minimum-tokens=<minimumTokens>'"));
    }

    @Test
    void testMissingSource() throws Exception {
        final CliExecutionResult result = runCli(CliExitCode.USAGE_ERROR, "--minimum-tokens", "340");
        result.checkStdErr(containsString("Please provide a parameter for source root directory"));
    }

    @Test
    void testWrongCliOptionsDoPrintUsage() throws Exception {
        final CliExecutionResult result = runCli(CliExitCode.USAGE_ERROR, "--invalid", "--minimum-tokens", "340", "-d", SRC_DIR);
        result.checkStdErr(containsString("Unknown option: '--invalid'"));
        result.checkStdErr(containsString("Usage: pmd cpd"));
    }

    @Test
    void testFindJavaDuplication() throws Exception {
        runCli(VIOLATIONS_FOUND, "--minimum-tokens", "7", "--dir", SRC_DIR)
            .verify(result -> result.checkStdOut(containsString(
                "Found a 14 line (86 tokens) duplication in the following files:"
            )));
    }

    @Test
    void testNoFailOnViolation() throws Exception {
        runCli(CliExitCode.OK, "--minimum-tokens", "7", "--dir", SRC_DIR, "--no-fail-on-violation")
            .verify(result -> result.checkStdOut(containsString(
                "Found a 14 line (86 tokens) duplication in the following files:"
            )));
    }

    @Test
    void testExcludeFiles() throws Exception {
        runCliSuccessfully("--minimum-tokens", "7", "--dir", SRC_DIR,
                           "--exclude", SRC_DIR + "/dup2.java",
                           SRC_DIR + "/dup1.java")
            .verify(result -> result.checkStdOut(emptyString()));
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
                assertEquals(CliExitCode.OK.getExitCode(), statusCode);
            });
        });
        final Path absoluteSrcDir = Paths.get(SRC_DIR).toAbsolutePath();
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                         + "<pmd-cpd>\n"
                         + "   <file path=\"" + absoluteSrcDir.resolve("dup1.java").toString() + "\"\n"
                         + "         totalNumberOfTokens=\"89\"/>\n"
                         + "   <file path=\"" + absoluteSrcDir.resolve("dup2.java").toString() + "\"\n"
                         + "         totalNumberOfTokens=\"89\"/>\n"
                         + "   <file path=\"" + absoluteSrcDir.resolve("file_with_ISO-8859-1_encoding.java").toString()
                         + "\"\n"
                         + "         totalNumberOfTokens=\"8\"/>\n"
                         + "   <file path=\"" + absoluteSrcDir.resolve("file_with_utf8_bom.java").toString() + "\"\n"
                         + "         totalNumberOfTokens=\"9\"/>\n"
                         + "</pmd-cpd>", stdout.trim());
    }

    /**
     * #1144 CPD encoding argument has no effect
     */
    @Test
    void testEncodingOption() throws Exception {

        SystemLambda.restoreSystemProperties(() -> {
            // set the default encoding under Windows
            System.setProperty("file.encoding", "Cp1252");

            runCli(VIOLATIONS_FOUND, "--minimum-tokens", "34",
                   "-d", BASE_RES_PATH + "encodingTest/",
                   "--ignore-identifiers", "--format", "xml",
                   // request UTF-8 for CPD
                   "--encoding", "UTF-8")
                .verify(r -> {
                    r.checkStdOut(startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
                    r.checkStdOut(containsPattern("System\\.out\\.println\\([ij] \\+ \"ä\"\\);"));
                });
        });
    }

    /**
     * See: https://sourceforge.net/p/pmd/bugs/1178/
     */
    @Test
    public void testSkipLexicalErrors() throws Exception {
        runCli(VIOLATIONS_FOUND,
               "--minimum-tokens", "10",
               "-d", BASE_RES_PATH + "badandgood/",
               "--format", "text",
               "--skip-lexical-errors")
            .verify(r -> {
                r.checkStdErr(containsPattern("Skipping .*?BadFile\\.java\\. Reason: Lexical error in file"));
                r.checkStdOut(containsString("Found a 5 line (13 tokens) duplication"));
            });
    }


    @Test
    public void jsShouldFindDuplicatesWithDifferentFileExtensions() throws Exception {
        runCli(VIOLATIONS_FOUND, "--minimum-tokens", "5", "--language", "js",
               "-d", BASE_RES_PATH + "tsFiles/File1.ts", BASE_RES_PATH + "tsFiles/File2.ts")
            .checkStdOut(containsString("Found a 9 line (32 tokens) duplication in the following files"));
    }

    @Test
    public void jsShouldFindNoDuplicatesWithDifferentFileExtensions() throws Exception {
        runCli(OK, "--minimum-tokens", "5", "--language", "js",
               "-d", BASE_RES_PATH + "tsFiles/")
            .checkStdOut(emptyString());
    }

    @Test
    public void renderEmptyReportXml() throws Exception {
        runCli(OK, "--minimum-tokens", "5", "--language", "js",
               "-f", "xml",
               "-d", BASE_RES_PATH + "tsFiles/")
            .checkStdOut(equalTo(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<pmd-cpd/>\n"
            ));
    }
}
