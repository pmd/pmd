/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMD.StatusCode;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.internal.Slf4jSimpleConfiguration;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.MockRule;
import net.sourceforge.pmd.util.IOUtil;

import com.github.stefanbirkner.systemlambda.SystemLambda;

/**
 *
 */
class CoreCliTest {

    @TempDir
    private Path tempDir;

    private static final String DUMMY_RULESET = "net/sourceforge/pmd/cli/FakeRuleset.xml";
    private static final String DUMMY_RULESET_WITH_VIOLATIONS = "net/sourceforge/pmd/cli/FakeRuleset2.xml";
    private static final String STRING_TO_REPLACE = "__should_be_replaced__";

    private Path srcDir;

    @AfterAll
    static void resetLogging() {
        // reset logging in case "--debug" changed the logging properties
        // See also Slf4jSimpleConfigurationForAnt
        Slf4jSimpleConfiguration.reconfigureDefaultLogLevel(null);
    }

    @BeforeEach
    void setup() throws IOException {
        // create a few files
        srcDir = Files.createDirectories(tempRoot().resolve("src"));
        writeString(srcDir.resolve("someSource.dummy"), "dummy text");
    }


    @Test
    void testPreExistingReportFile() throws Exception {
        Path reportFile = tempRoot().resolve("out/reportFile.txt");
        // now we create the file
        Files.createDirectories(reportFile.getParent());
        writeString(reportFile, STRING_TO_REPLACE);

        assertTrue(Files.exists(reportFile), "Report file should exist");

        runPmdSuccessfully("--no-cache", "-d", srcDir, "-R", DUMMY_RULESET, "-r", reportFile);

        assertNotEquals(readString(reportFile), STRING_TO_REPLACE);
    }

    @Test
    void testPreExistingReportFileLongOption() throws Exception {
        Path reportFile = tempRoot().resolve("out/reportFile.txt");
        // now we create the file
        Files.createDirectories(reportFile.getParent());
        writeString(reportFile, STRING_TO_REPLACE);

        assertTrue(Files.exists(reportFile), "Report file should exist");

        runPmdSuccessfully("--no-cache", "--dir", srcDir, "--rulesets", DUMMY_RULESET, "--report-file", reportFile);

        assertNotEquals(readString(reportFile), STRING_TO_REPLACE, "Report file should have been overwritten");
    }

    @Test
    void testNonExistentReportFile() throws Exception {
        Path reportFile = tempRoot().resolve("out/reportFile.txt");

        assertFalse(Files.exists(reportFile), "Report file should not exist");

        try {
            runPmdSuccessfully("--no-cache", "-d", srcDir, "-R", DUMMY_RULESET, "-r", reportFile);
            assertTrue(Files.exists(reportFile), "Report file should have been created");
        } finally {
            Files.deleteIfExists(reportFile);
        }
    }

    @Test
    void testNonExistentReportFileLongOption() throws Exception {
        Path reportFile = tempRoot().resolve("out/reportFile.txt");

        assertFalse(Files.exists(reportFile), "Report file should not exist");

        runPmdSuccessfully("--no-cache", "--dir", srcDir, "--rulesets", DUMMY_RULESET, "--report-file", reportFile);

        assertTrue(Files.exists(reportFile), "Report file should have been created");
    }

    @Test
    void testNoRelativizeWithAbsoluteSrcDir() throws Exception {
        assertTrue(srcDir.isAbsolute(), "srcDir should be absolute");
        String log = SystemLambda.tapSystemErrAndOut(() -> {
            runPmd(StatusCode.VIOLATIONS_FOUND, "--no-cache", "--dir", srcDir, "--rulesets", DUMMY_RULESET_WITH_VIOLATIONS);
        });

        assertThat(log, containsString(srcDir.resolve("someSource.dummy").toString()));
    }

    @Test
    void testNoRelativizeWithRelativeSrcDir() throws Exception {
        // Note, that we can't reliably change the current working directory for the current java process
        // therefore we use the current directory and make sure, we are at the correct place - in pmd-core
        Path cwd = Paths.get(".").toRealPath();
        assertThat(cwd.toString(), endsWith("pmd-core"));
        String relativeSrcDir = "src/test/resources/net/sourceforge/pmd/cli/src";
        assertTrue(Files.isDirectory(cwd.resolve(relativeSrcDir)));

        String log = SystemLambda.tapSystemErrAndOut(() -> {
            runPmd(StatusCode.VIOLATIONS_FOUND, "--no-cache", "--dir", relativeSrcDir, "--rulesets", DUMMY_RULESET_WITH_VIOLATIONS);
        });

        assertThat(log, containsString("\n" + IOUtil.normalizePath(relativeSrcDir + "/somefile.dummy")));
    }

    @Test
    void testNoRelativizeWithRelativeSrcDirParent() throws Exception {
        // Note, that we can't reliably change the current working directory for the current java process
        // therefore we use the current directory and make sure, we are at the correct place - in pmd-core
        Path cwd = Paths.get(".").toRealPath();
        assertThat(cwd.toString(), endsWith("pmd-core"));
        String relativeSrcDir = IOUtil.normalizePath("src/test/resources/net/sourceforge/pmd/cli/src");
        assertTrue(Files.isDirectory(cwd.resolve(relativeSrcDir)));

        // use the parent directory
        String relativeSrcDirWithParent = relativeSrcDir + File.separator + "..";

        String log = SystemLambda.tapSystemErrAndOut(() -> {
            runPmd(StatusCode.VIOLATIONS_FOUND, "--no-cache", "--dir", relativeSrcDirWithParent, "--rulesets", DUMMY_RULESET_WITH_VIOLATIONS);
        });

        assertThat(log, containsString("\n" + relativeSrcDirWithParent + IOUtil.normalizePath("/src/somefile.dummy")));
    }

    @Test
    void testRelativizeWithRootRelativeSrcDir() throws Exception {
        // Note, that we can't reliably change the current working directory for the current java process
        // therefore we use the current directory and make sure, we are at the correct place - in pmd-core
        Path cwd = Paths.get(".").toRealPath();
        assertThat(cwd.toString(), endsWith("pmd-core"));
        String relativeSrcDir = "src/test/resources/net/sourceforge/pmd/cli/src";
        assertTrue(Files.isDirectory(cwd.resolve(relativeSrcDir)));

        String root = cwd.getRoot().toString();

        String log = SystemLambda.tapSystemErrAndOut(() -> {
            runPmd(StatusCode.VIOLATIONS_FOUND, "--no-cache", "--dir", relativeSrcDir, "--rulesets", DUMMY_RULESET_WITH_VIOLATIONS, "--relativize-paths-with", root);
        });

        String absoluteSrcPath = cwd.resolve(relativeSrcDir).resolve("somefile.dummy").toString();
        assertThat(log, containsString("\n" + absoluteSrcPath));
    }

    @Test
    void testRelativizeWith() throws Exception {
        String log = SystemLambda.tapSystemOut(() -> {
            runPmd(StatusCode.VIOLATIONS_FOUND, "--no-cache", "--dir", srcDir, "--rulesets", DUMMY_RULESET_WITH_VIOLATIONS, "-z", srcDir.getParent());
        });

        assertThat(log, not(containsString(srcDir.resolve("someSource.dummy").toString())));
        assertThat(log, startsWith(IOUtil.normalizePath("src/someSource.dummy")));
    }

    @Test
    void testRelativizeWithSymLink() throws Exception {
        // srcDir = /tmp/junit123/src
        // symlinkedSrcDir = /tmp/junit123/sources -> /tmp/junit123/src
        Path symlinkedSrcDir = Files.createSymbolicLink(tempRoot().resolve("sources"), srcDir);
        String log = SystemLambda.tapSystemOut(() -> {
            runPmd(StatusCode.VIOLATIONS_FOUND, "--no-cache", "--dir", symlinkedSrcDir, "--rulesets", DUMMY_RULESET_WITH_VIOLATIONS, "-z", symlinkedSrcDir);
        });

        assertThat(log, not(containsString(srcDir.resolve("someSource.dummy").toString())));
        assertThat(log, not(containsString(symlinkedSrcDir.resolve("someSource.dummy").toString())));
        assertThat(log, startsWith("someSource.dummy"));
    }

    @Test
    void testRelativizeWithSymLinkParent() throws Exception {
        // srcDir = /tmp/junit123/src
        // symlinkedSrcDir = /tmp/junit-relativize-with-123 -> /tmp/junit123/src
        Path tempPath = Files.createTempDirectory("junit-relativize-with-");
        Files.delete(tempPath);
        Path symlinkedSrcDir = Files.createSymbolicLink(tempPath, srcDir);
        // relativizing against parent of symlinkedSrcDir: /tmp
        String log = SystemLambda.tapSystemOut(() -> {
            runPmd(StatusCode.VIOLATIONS_FOUND, "--no-cache", "--dir", symlinkedSrcDir, "--rulesets", DUMMY_RULESET_WITH_VIOLATIONS, "-z", symlinkedSrcDir.getParent());
        });

        assertThat(log, not(containsString(srcDir.resolve("someSource.dummy").toString())));
        assertThat(log, not(containsString(symlinkedSrcDir.resolve("someSource.dummy").toString())));
        // base path is symlinkedSrcDir without /tmp: e.g. junit-relativize-with-123
        String basePath = symlinkedSrcDir.getParent().relativize(symlinkedSrcDir).toString();
        assertThat(log, startsWith(basePath + File.separator + "someSource.dummy"));
    }

    @Test
    void testRelativizeWithMultiple() throws Exception {
        String log = SystemLambda.tapSystemOut(() -> {
            runPmd(StatusCode.VIOLATIONS_FOUND, "--no-cache", "--dir", srcDir, "--rulesets", DUMMY_RULESET_WITH_VIOLATIONS, "-z", srcDir.getParent(), srcDir);
        });

        assertThat(log, not(containsString(srcDir.resolve("someSource.dummy").toString())));
        assertThat(log, startsWith("someSource.dummy"));
    }

    @Test
    void testRelativizeWithFileIsError() throws Exception {
        String log = SystemLambda.tapSystemErr(() -> {
            runPmd(StatusCode.ERROR, "--no-cache", "--dir", srcDir, "--rulesets", DUMMY_RULESET_WITH_VIOLATIONS, "-z", srcDir.resolve("someSource.dummy"));
        });

        assertThat(log, containsString(
            "Expected a directory path for option --relativize-paths-with, found a file: "
            + srcDir.resolve("someSource.dummy")));
    }

    @Test
    void testDeprecationWarningForShortNames() throws Exception {
        String log = runPmdSuccessfully("--no-cache", "--dir", srcDir, "--rulesets", DUMMY_RULESET, "--short-names");

        assertThat(log, containsString("Some deprecated options were used on the command-line, including --short-names"));
        assertThat(log, containsString("Consider replacing it with --relativize-paths-with"));
    }

    @Test
    void testFileCollectionWithUnknownFiles() throws Exception {
        Path reportFile = tempRoot().resolve("out/reportFile.txt");
        Files.createFile(srcDir.resolve("foo.not_analysable"));
        assertFalse(Files.exists(reportFile), "Report file should not exist");

        // restoring system properties: --debug might change logging properties
        SystemLambda.restoreSystemProperties(() -> {
            runPmdSuccessfully("--no-cache", "--dir", srcDir, "--rulesets", DUMMY_RULESET, "--report-file", reportFile, "--debug");
        });

        assertTrue(Files.exists(reportFile), "Report file should have been created");
        String reportText = IOUtil.readToString(Files.newBufferedReader(reportFile, StandardCharsets.UTF_8));
        assertThat(reportText, not(containsStringIgnoringCase("error")));
    }

    @Test
    void testNonExistentReportFileDeprecatedOptions() throws Exception {
        Path reportFile = tempRoot().resolve("out/reportFile.txt");

        assertFalse(Files.exists(reportFile), "Report file should not exist");

        String log = runPmdSuccessfully("-no-cache", "-dir", srcDir, "-rulesets", DUMMY_RULESET, "-reportfile", reportFile);

        assertTrue(Files.exists(reportFile), "Report file should have been created");
        assertTrue(log.contains("Some deprecated options were used on the command-line, including -rulesets"));
        assertTrue(log.contains("Consider replacing it with --rulesets (or -R)"));
        // only one parameter is logged
        assertFalse(log.contains("Some deprecated options were used on the command-line, including -reportfile"));
        assertFalse(log.contains("Consider replacing it with --report-file"));
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
            runPmdSuccessfully("--no-cache", "-d", srcDir, "-R", DUMMY_RULESET, "-r", reportFile);
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
            runPmdSuccessfully("--no-cache", "--dir", srcDir, "--rulesets", DUMMY_RULESET, "--report-file", reportFile);
            assertTrue(Files.exists(absoluteReportFile), "Report file should have been created");
        } finally {
            Files.deleteIfExists(absoluteReportFile);
        }
    }

    @Test
    void debugLogging() throws Exception {
        // restoring system properties: --debug might change logging properties
        SystemLambda.restoreSystemProperties(() -> {
            String log = runPmdSuccessfully("--debug", "--no-cache", "--dir", srcDir, "--rulesets", DUMMY_RULESET);
            assertThat(log, containsString("[main] INFO net.sourceforge.pmd.PMD - Log level is at TRACE"));
        });
    }

    @Test
    void defaultLogging() throws Exception {
        String log = runPmdSuccessfully("--no-cache", "--dir", srcDir, "--rulesets", DUMMY_RULESET);
        assertThat(log, containsString("[main] INFO net.sourceforge.pmd.PMD - Log level is at INFO"));
    }

    @Test
    void testDeprecatedRulesetSyntaxOnCommandLine() throws Exception {
        String log = SystemLambda.tapSystemErrAndOut(() -> {
            runPmd(StatusCode.VIOLATIONS_FOUND, "--no-cache", "--dir", srcDir, "--rulesets", "dummy-basic");
        });
        assertThat(log, containsString("Ruleset reference 'dummy-basic' uses a deprecated form, use 'rulesets/dummy/basic.xml' instead"));
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
            SystemLambda.tapSystemErrAndOut(() -> {
                runPmd(StatusCode.VIOLATIONS_FOUND, "--no-cache", "--dir", srcDir, "--rulesets", "dummy-basic");
            });
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void testWrongCliOptionsDoNotPrintUsage() throws Exception {
        String[] args = {"-invalid"};
        PmdParametersParseResult params = PmdParametersParseResult.extractParameters(args);
        assertTrue(params.isError(), "Expected invalid args");

        String log = SystemLambda.tapSystemErrAndOut(() -> {
            StatusCode code = PMD.runPmd(args);
            assertEquals(StatusCode.ERROR, code);
        });
        assertThat(log, not(containsStringIgnoringCase("Available report formats and")));
    }

    @Test
    void testZipFileAsSource() throws Exception {
        Path zipArchive = createTemporaryZipArchive("sources.zip");
        String log = SystemLambda.tapSystemErrAndOut(() -> {
            runPmd(StatusCode.VIOLATIONS_FOUND, "--no-cache", "--dir", zipArchive, "--rulesets", "rulesets/dummy/basic.xml");
        });
        assertThat(log, not(containsStringIgnoringCase("Cannot open zip file")));
        String reportPath = IOUtil.normalizePath(zipArchive.toString() + "!/someSource.dummy");
        assertThat(log, containsString(reportPath + ":1:\tSampleXPathRule:\tTest Rule 2"));
    }

    @Test
    void testJarFileAsSource() throws Exception {
        Path jarArchive = createTemporaryZipArchive("sources.jar");
        String log = SystemLambda.tapSystemErrAndOut(() -> {
            runPmd(StatusCode.VIOLATIONS_FOUND, "--no-cache", "--dir", jarArchive, "--rulesets", "rulesets/dummy/basic.xml");
        });
        assertThat(log, not(containsStringIgnoringCase("Cannot open zip file")));
        String reportPath = IOUtil.normalizePath(jarArchive.toString() + "!/someSource.dummy");
        assertThat(log, containsString(reportPath + ":1:\tSampleXPathRule:\tTest Rule 2"));
    }

    private Path createTemporaryZipArchive(String name) throws Exception {
        Path zipArchive = tempRoot().resolve(name);
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipArchive.toFile()))) {
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


    private static String runPmdSuccessfully(Object... args) throws Exception {
        return SystemLambda.tapSystemErrAndOut(() -> {
            runPmd(StatusCode.OK, args);
        });
    }

    private static String[] argsToString(Object... args) {
        String[] result = new String[args.length];
        for (int i = 0; i < args.length; i++) {
            result[i] = args[i].toString();
        }
        return result;
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

    private static void runPmd(StatusCode expectedExitCode, Object... args) {
        StatusCode actualExitCode = PMD.runPmd(argsToString(args));
        assertEquals(expectedExitCode, actualExitCode, "Exit code");
    }

    public static class FooRule extends MockRule {
        @Override
        public void apply(Node node, RuleContext ctx) {
            ctx.addViolation(node);
        }
    }
}
