/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli;

import static net.sourceforge.pmd.cli.internal.CliExitCode.ERROR;
import static net.sourceforge.pmd.cli.internal.CliExitCode.OK;
import static net.sourceforge.pmd.cli.internal.CliExitCode.VIOLATIONS_FOUND;
import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import net.sourceforge.pmd.cli.internal.CliExitCode;
import net.sourceforge.pmd.internal.Slf4jSimpleConfiguration;
import net.sourceforge.pmd.util.IOUtil;

import com.github.stefanbirkner.systemlambda.SystemLambda;

class PmdCliTest extends BaseCliTest {

    static final String RULESET_WITH_VIOLATION = "net/sourceforge/pmd/cli/RuleSetWithViolations.xml";
    private static final String RULESET_NO_VIOLATIONS = "net/sourceforge/pmd/cli/FakeRuleset.xml";
    private static final String NOT_A_RULESET = "ThisRuleSetDoesNotExist.xml";
    private static final String STRING_TO_REPLACE = "__should_be_replaced__";

    @TempDir
    private Path tempDir;
    private Path srcDir;

    @AfterAll
    static void resetLogging() {
        // reset logging in case "--debug" changed the logging properties
        // See also Slf4jSimpleConfigurationForAnt
        Slf4jSimpleConfiguration.reconfigureDefaultLogLevel(null);
    }

    @BeforeEach
    void setup() throws IOException {
        // set current directory to wd
        Path root = tempRoot();
        System.setProperty("user.dir", root.toString());

        // create a few files
        srcDir = Files.createDirectories(root.resolve("src"));
        writeString(srcDir.resolve("someSource.dummy"), "dummy text");
    }


    @Test
    void testPreExistingReportFile() throws Exception {
        Path reportFile = tempRoot().resolve("out/reportFile.txt");
        // now we create the file
        Files.createDirectories(reportFile.getParent());
        writeString(reportFile, STRING_TO_REPLACE);

        assertTrue(Files.exists(reportFile), "Report file should exist");

        runCliSuccessfully("-d", srcDir.toString(), "-R", RULESET_NO_VIOLATIONS, "-r", reportFile.toString());

        assertNotEquals(readString(reportFile), STRING_TO_REPLACE);
    }

    @Test
    void testPreExistingReportFileLongOption() throws Exception {
        Path reportFile = tempRoot().resolve("out/reportFile.txt");
        // now we create the file
        Files.createDirectories(reportFile.getParent());
        writeString(reportFile, STRING_TO_REPLACE);

        assertTrue(Files.exists(reportFile), "Report file should exist");

        runCliSuccessfully("--dir", srcDir.toString(), "--rulesets", RULESET_NO_VIOLATIONS, "--report-file", reportFile.toString());

        assertNotEquals(readString(reportFile), STRING_TO_REPLACE, "Report file should have been overwritten");
    }

    @Test
    void testNonExistentReportFile() throws Exception {
        Path reportFile = tempRoot().resolve("out/reportFile.txt");

        assertFalse(Files.exists(reportFile), "Report file should not exist");

        try {
            runCliSuccessfully("-d", srcDir.toString(), "-R", RULESET_NO_VIOLATIONS, "-r", reportFile.toString());
            assertTrue(Files.exists(reportFile), "Report file should have been created");
        } finally {
            Files.deleteIfExists(reportFile);
        }
    }

    @Test
    void testNonExistentReportFileLongOption() throws Exception {
        Path reportFile = tempRoot().resolve("out/reportFile.txt");

        assertFalse(Files.exists(reportFile), "Report file should not exist");

        runCliSuccessfully("--dir", srcDir.toString(), "--rulesets", RULESET_NO_VIOLATIONS, "--report-file", reportFile.toString());

        assertTrue(Files.exists(reportFile), "Report file should have been created");
    }

    @Test
    void testFileCollectionWithUnknownFiles() throws Exception {
        Path reportFile = tempRoot().resolve("out/reportFile.txt");
        Files.createFile(srcDir.resolve("foo.not_analysable"));
        assertFalse(Files.exists(reportFile), "Report file should not exist");

        // restoring system properties: --debug might change logging properties
        SystemLambda.restoreSystemProperties(() -> {
            runCliSuccessfully("--dir", srcDir.toString(), "--rulesets", RULESET_NO_VIOLATIONS, "--report-file", reportFile.toString(), "--debug");
        });

        assertTrue(Files.exists(reportFile), "Report file should have been created");
        String reportText = readString(reportFile);
        assertThat(reportText, not(containsStringIgnoringCase("error")));
    }

    /**
     * This tests to create the report file in the current working directory.
     *
     * <p>Note: We can't change the cwd in the running VM, so the file will not be created
     * in the temporary folder, but really in the cwd. The test fails if a file already exists
     * and makes sure to cleanup the file afterwards.
     */
    @Test
    void testRelativeReportFile() throws Exception {
        String reportFile = "reportFile.txt";
        Path absoluteReportFile = FileSystems.getDefault().getPath(reportFile).toAbsolutePath();
        // verify the file doesn't exist yet - we will delete the file at the end!
        assertFalse(Files.exists(absoluteReportFile), "Report file must not exist yet! " + absoluteReportFile);

        try {
            runCliSuccessfully("-d", srcDir.toString(), "-R", RULESET_NO_VIOLATIONS, "-r", reportFile);
            assertTrue(Files.exists(absoluteReportFile), "Report file should have been created");
        } finally {
            Files.deleteIfExists(absoluteReportFile);
        }
    }

    @Test
    void testRelativeReportFileLongOption() throws Exception {
        String reportFile = "reportFile.txt";
        Path absoluteReportFile = FileSystems.getDefault().getPath(reportFile).toAbsolutePath();
        // verify the file doesn't exist yet - we will delete the file at the end!
        assertFalse(Files.exists(absoluteReportFile), "Report file must not exist yet!");

        try {
            runCliSuccessfully("--dir", srcDir.toString(), "--rulesets", RULESET_NO_VIOLATIONS, "--report-file", reportFile.toString());
            assertTrue(Files.exists(absoluteReportFile), "Report file should have been created");
        } finally {
            Files.deleteIfExists(absoluteReportFile);
        }
    }

    @Test
    void debugLogging() throws Exception {
        CliExecutionResult result = runCliSuccessfully("--debug", "--dir", srcDir.toString(), "--rulesets", RULESET_NO_VIOLATIONS);
        result.checkStdErr(containsString("[main] INFO net.sourceforge.pmd.cli.commands.internal.AbstractPmdSubcommand - Log level is at TRACE"));
    }

    @Test
    void defaultLogging() throws Exception {
        CliExecutionResult result = runCliSuccessfully("--dir", srcDir.toString(), "--rulesets", RULESET_NO_VIOLATIONS);
        result.checkStdErr(containsString("[main] INFO net.sourceforge.pmd.cli.commands.internal.AbstractPmdSubcommand - Log level is at INFO"));
        result.checkStdErr(not(containsPattern("Adding file .*"))); // not in debug mode
    }

    @Test
    void testDeprecatedRulesetSyntaxOnCommandLine() throws Exception {
        CliExecutionResult result = runCli(CliExitCode.VIOLATIONS_FOUND, "--dir", srcDir.toString(), "--rulesets", "dummy-basic");
        result.checkStdErr(containsString("Ruleset reference 'dummy-basic' uses a deprecated form, use 'rulesets/dummy/basic.xml' instead"));
    }

    @Test
    void testReportToStdoutNotClosing() throws Exception {
        PrintStream originalOut = System.out;
        PrintStream out = new PrintStream(new FilterOutputStream(originalOut) {
            @Override
            public void close() {
                fail("Stream must not be closed");
            }
        });
        try {
            System.setOut(out);
            runCli(VIOLATIONS_FOUND, "--dir", srcDir.toString(), "--rulesets", "rulesets/dummy/basic.xml");
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void testMissingRuleset() throws Exception {
        CliExecutionResult result = runCli(CliExitCode.USAGE_ERROR);
        result.checkStdErr(containsString("Missing required option: '--rulesets=<rulesets>'"));
    }
    
    @Test
    void testMissingSource() throws Exception {
        CliExecutionResult result = runCli(CliExitCode.USAGE_ERROR, "--rulesets", RULESET_NO_VIOLATIONS);
        result.checkStdErr(containsString("Please provide a parameter for source root directory"));
    }

    /**
     * @see <a href="https://github.com/pmd/pmd/issues/3427">[core] Stop printing CLI usage text when exiting due to invalid parameters #3427</a>
     */
    @Test
    void testWrongCliOptionsDoPrintUsage() throws Exception {
        runCli(CliExitCode.USAGE_ERROR, "--invalid", "--rulesets", RULESET_NO_VIOLATIONS, "-d", srcDir.toString())
                .verify(result -> {
                    result.checkStdErr(containsString("Unknown option: '--invalid'"));
                    result.checkStdErr(containsString("Usage: pmd check"));
                    result.checkStdErr(not(containsStringIgnoringCase("Available report formats and")));
                });
    }

    /**
     * See https://sourceforge.net/p/pmd/bugs/1231/
     */
    @Test
    void testWrongRuleset() throws Exception {
        runCli(ERROR, "-d", srcDir.toString(), "-f", "text", "-R", NOT_A_RULESET)
            .verify(result -> result.checkStdErr(
                containsString("Cannot resolve rule/ruleset reference"
                                   + " '" + NOT_A_RULESET + "'")));
    }

    /**
     * See https://sourceforge.net/p/pmd/bugs/1231/
     */
    @Test
    void testWrongRulesetWithRulename() throws Exception {
        runCli(ERROR, "-d", srcDir.toString(), "-f", "text", "-R", NOT_A_RULESET + "/NotARule")
            .verify(result -> result.checkStdErr(
                containsString("Cannot resolve rule/ruleset reference"
                                   + " '" + NOT_A_RULESET + "/NotARule'")));
    }

    /**
     * See https://sourceforge.net/p/pmd/bugs/1231/
     */
    @Test
    void testWrongRulename() throws Exception {
        runCli(OK, "-d", srcDir.toString(), "-f", "text", "-R", RULESET_NO_VIOLATIONS + "/ThisRuleDoesNotExist")
            .verify(result -> result.checkStdErr(
                containsString(
                    "No rules found. Maybe you misspelled a rule name?"
                        + " (" + RULESET_NO_VIOLATIONS + "/ThisRuleDoesNotExist)"
                )
            ));
    }

    @Test
    void changeSourceVersion() throws Exception {
        runCli(OK, "-d", srcDir.toString(), "-f", "text", "-R", RULESET_NO_VIOLATIONS, "--debug",
               "--use-version", "dummy-1.2")
            .verify(result -> result.checkStdErr(
                containsPattern("Adding file .*\\.dummy \\(lang: dummy 1\\.2\\)"))
            );
    }


    @Test
    void exitStatusWithViolationsAndWithoutFailOnViolations() throws Exception {
        runCli(OK, "-d", srcDir.toString(), "-f", "text", "-R", RULESET_WITH_VIOLATION, "--no-fail-on-violation")
            .verify(r -> r.checkStdOut(
                containsString("Violation from ReportAllRootNodes")
            ));
    }

    @Test
    void exitStatusWithNoViolations() throws Exception {
        runCli(OK, "-d", srcDir.toString(), "-f", "text", "-R", RULESET_NO_VIOLATIONS)
            .verify(r -> r.checkStdOut(equalTo("")));
    }

    @Test
    void exitStatusWithViolations() throws Exception {
        runCli(VIOLATIONS_FOUND, "-d", srcDir.toString(), "-f", "text", "-R", RULESET_WITH_VIOLATION)
            .verify(r -> r.checkStdOut(
                containsString("Violation from ReportAllRootNodes")
            ));
    }

    @Test
    void testZipFileAsSource() throws Exception {
        Path zipArchive = createTemporaryZipArchive("sources.zip");
        CliExecutionResult result = runCli(VIOLATIONS_FOUND, "--dir", zipArchive.toString(), "--rulesets", "rulesets/dummy/basic.xml");
        result.checkStdErr(not(containsStringIgnoringCase("Cannot open zip file")));
        String reportPath = IOUtil.normalizePath(zipArchive + "!/someSource.dummy");
        result.checkStdOut(containsString(reportPath + ":1:\tSampleXPathRule:\tTest Rule 2"));
    }

    @Test
    void testJarFileAsSource() throws Exception {
        Path jarArchive = createTemporaryZipArchive("sources.jar");
        CliExecutionResult result = runCli(VIOLATIONS_FOUND, "--dir", jarArchive.toString(), "--rulesets", "rulesets/dummy/basic.xml");
        result.checkStdErr(not(containsStringIgnoringCase("Cannot open zip file")));
        String reportPath = IOUtil.normalizePath(jarArchive + "!/someSource.dummy");
        result.checkStdOut(containsString(reportPath + ":1:\tSampleXPathRule:\tTest Rule 2"));
    }

    private Path createTemporaryZipArchive(String name) throws Exception {
        Path zipArchive = tempRoot().resolve(name);
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(zipArchive))) {
            ZipEntry zipEntry = new ZipEntry("someSource.dummy");
            zipOutputStream.putNextEntry(zipEntry);
            zipOutputStream.write("dummy text".getBytes(StandardCharsets.UTF_8));
            zipOutputStream.closeEntry();
        }
        return zipArchive;
    }

    // utilities
    private Path tempRoot() {
        return tempDir;
    }


    // available in Files on java 11+
    private static void writeString(Path path, String text) throws IOException {
        ByteBuffer encoded = StandardCharsets.UTF_8.encode(text);
        Files.write(path, encoded.array());
    }


    // available in Files on java 11+
    private static String readString(Path path) throws IOException {
        byte[] bytes = Files.readAllBytes(path);
        ByteBuffer buf = ByteBuffer.wrap(bytes);
        return StandardCharsets.UTF_8.decode(buf).toString();
    }
    
    @Override
    protected List<String> cliStandardArgs() {
        return listOf(
            "check", "--no-cache", "--no-progress"
        );
    }
}
