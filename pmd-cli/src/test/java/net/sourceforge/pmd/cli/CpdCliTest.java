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
import static org.hamcrest.Matchers.not;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import net.sourceforge.pmd.cli.internal.CliExitCode;
import net.sourceforge.pmd.internal.Slf4jSimpleConfiguration;

import com.github.stefanbirkner.systemlambda.SystemLambda;

class CpdCliTest extends BaseCliTest {

    private static final String BASE_RES_PATH = "src/test/resources/net/sourceforge/pmd/cli/cpd/";
    private static final String SRC_DIR = BASE_RES_PATH + "files/";
    private static final Path SRC_PATH = Paths.get(SRC_DIR).toAbsolutePath();

    private static final Map<String, Integer> NUMBER_OF_TOKENS;

    static {
        Map<String, Integer> map = new LinkedHashMap<>();
        map.put(SRC_PATH.resolve("dup1.java").toString(), 89);
        map.put(SRC_PATH.resolve("dup2.java").toString(), 89);
        map.put(SRC_PATH.resolve("fileWith_ISO8859_1_Encoding.java").toString(), 5);
        map.put(SRC_PATH.resolve("fileWith_UTF_8_BOM_Encoding.java").toString(), 5);
        NUMBER_OF_TOKENS = Collections.unmodifiableMap(map);
    }

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
    void testEmptyResultRendering() throws Exception {
        final String expectedFilesXml = getExpectedFileEntriesXml(NUMBER_OF_TOKENS.keySet());
        runCliSuccessfully("--minimum-tokens", "340", "--language", "java", "--dir", SRC_DIR, "--format", "xml")
                .verify(result -> result.checkStdOut(equalTo(
                        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\n" + "<pmd-cpd>\n" + expectedFilesXml + "</pmd-cpd>\n"
                )));
    }

    private String getExpectedFileEntryXml(final String filename) {
        final int numberOfTokens = NUMBER_OF_TOKENS.get(filename);
        return String.format("   <file path=\"%s\"\n         totalNumberOfTokens=\"%d\"/>\n",
                filename,
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
    void debugLogging() throws Exception {
        CliExecutionResult result = runCliSuccessfully("--debug", "--minimum-tokens", "340", "--dir", SRC_DIR);
        result.checkStdErr(containsString("[DEBUG] Log level is at TRACE"));
    }

    @Test
    void debugLoggingShouldMentionLanguage() throws Exception {
        final CliExecutionResult result = runCli(VIOLATIONS_FOUND, "--minimum-tokens", "34", "--dir", SRC_DIR, "--debug");
        result.checkStdErr(containsString("Created new FileCollector with LanguageVersionDiscoverer(LanguageRegistry(java))"));
    }

    @Test
    void defaultLogging() throws Exception {
        CliExecutionResult result = runCliSuccessfully("--minimum-tokens", "340", "--dir", SRC_DIR);
        result.checkStdErr(not(containsString("[DEBUG] Log level is at TRACE")));
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
    void testWrongCliOptionResultsInErrorLoggingAfterDir() throws Exception {
        // --ignore-identifiers doesn't take an argument anymore - it is interpreted as a file for inputPaths
        final CliExecutionResult result = runCli(VIOLATIONS_FOUND, "--minimum-tokens", "34", "--dir", SRC_DIR, "--ignore-identifiers", "false");
        result.checkStdErr(containsString("No such file false"));
    }

    @Test
    void testWrongCliOptionResultsInErrorLoggingBeforeDir() throws Exception {
        // --ignore-identifiers doesn't take an argument anymore - it is interpreted as a file for inputPaths
        final CliExecutionResult result = runCli(VIOLATIONS_FOUND, "--minimum-tokens", "34", "--ignore-identifiers", "false", "--dir", SRC_DIR);
        result.checkStdErr(containsString("No such file false"));
    }

    @Test
    void testFindJavaDuplication() throws Exception {
        runCli(VIOLATIONS_FOUND, "--minimum-tokens", "7", "--dir", SRC_DIR)
            .verify(result -> result.checkStdOut(containsString(
                "Found a 14 line (86 tokens) duplication in the following files:"
            )));
    }

    /**
     * Test ignore identifiers argument.
     */
    @Test
    void testIgnoreIdentifiers() throws Exception {
        runCli(VIOLATIONS_FOUND, "--minimum-tokens", "34", "--dir", SRC_DIR, "--ignore-identifiers", "false", "--debug")
            .verify(result -> result.checkStdOut(containsString(
                    "Found a 14 line (89 tokens) duplication"
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
        String expectedReport = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<pmd-cpd>\n"
            + "   <file path=\"" + SRC_PATH.resolve("dup1.java") + "\"\n"
            + "         totalNumberOfTokens=\"89\"/>\n"
            + "   <file path=\"" + SRC_PATH.resolve("dup2.java") + "\"\n"
            + "         totalNumberOfTokens=\"89\"/>\n"
            + "   <file path=\"" + SRC_PATH.resolve("fileWith_ISO8859_1_Encoding.java") + "\"\n"
            + "         totalNumberOfTokens=\"5\"/>\n"
            + "   <file path=\"" + SRC_PATH.resolve("fileWith_UTF_8_BOM_Encoding.java") + "\"\n"
            + "         totalNumberOfTokens=\"5\"/>\n"
            + "</pmd-cpd>\n";

        runCliSuccessfully("--minimum-tokens", "340", "--language", "java", "--dir", SRC_DIR, "--format", "xml")
                .verify(result -> result.checkStdOut(equalTo(expectedReport)));
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
                   "--encoding", "UTF-8",
                   "--debug")
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
    void testSkipLexicalErrors() throws Exception {
        runCli(VIOLATIONS_FOUND,
               "--minimum-tokens", "10",
               "-d", BASE_RES_PATH + "badandgood/",
               "--format", "text",
               "--skip-lexical-errors")
            .verify(r -> {
                r.checkStdErr(containsPattern("Skipping file: Lexical error in file .*?BadFile\\.java"));
                r.checkStdOut(containsString("Found a 5 line (13 tokens) duplication"));
            });
    }


    @Test
    void jsShouldFindDuplicatesWithDifferentFileExtensions() throws Exception {
        runCli(VIOLATIONS_FOUND, "--minimum-tokens", "5", "--language", "typescript",
               "-d", BASE_RES_PATH + "tsFiles/File1.ts", BASE_RES_PATH + "tsFiles/File2.ts")
            .checkStdOut(containsString("Found a 9 line (32 tokens) duplication in the following files"));
    }

    @Test
    void jsShouldFindNoDuplicatesWithDifferentFileExtensions() throws Exception {
        runCli(OK, "--minimum-tokens", "5", "--language", "ecmascript",
               "-d", BASE_RES_PATH + "tsFiles/")
            .checkStdOut(emptyString());
    }

    @Test
    void renderEmptyReportXml() throws Exception {
        runCli(OK, "--minimum-tokens", "5", "--language", "ecmascript",
               "-f", "xml",
               "-d", BASE_RES_PATH + "tsFiles/")
            .checkStdOut(equalTo(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<pmd-cpd/>\n"
            ));
    }

    @Test
    void testFileListOnly() throws Exception {
        Path fileList = tempDir.resolve("fileList.txt");
        StringBuilder fileListContent = new StringBuilder();
        fileListContent.append(SRC_PATH.resolve("dup1.java")).append(System.lineSeparator());
        fileListContent.append(SRC_PATH.resolve("dup2.java")).append(System.lineSeparator());
        Files.write(fileList, fileListContent.toString().getBytes(StandardCharsets.UTF_8));
        runCli(VIOLATIONS_FOUND, "--minimum-tokens", "5", "--file-list", fileList.toString())
            .verify(result -> result.checkStdOut(containsString(
                    "Found a 14 line (86 tokens) duplication in the following files:"
            )));
    }
}
