/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.cli.internal.ExecutionResult;
import net.sourceforge.pmd.internal.Slf4jSimpleConfiguration;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.MockRule;
import net.sourceforge.pmd.util.IOUtil;

import com.github.stefanbirkner.systemlambda.SystemLambda;

class PmdCliTest extends BaseCliTest {

    @TempDir
    private Path tempDir;

    private static final String DUMMY_RULESET = "net/sourceforge/pmd/cli/FakeRuleset.xml";
    private static final String DUMMY_RULESET_WITH_VIOLATIONS = "net/sourceforge/pmd/cli/FakeRuleset2.xml";
    private static final String STRING_TO_REPLACE = "__should_be_replaced__";

    private Path srcDir;

    @AfterEach
    void resetLogging() {
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

        runCliSuccessfully("-d", srcDir.toString(), "-R", DUMMY_RULESET, "-r", reportFile.toString());

        assertNotEquals(readString(reportFile), STRING_TO_REPLACE);
    }

    @Test
    void testPreExistingReportFileLongOption() throws Exception {
        Path reportFile = tempRoot().resolve("out/reportFile.txt");
        // now we create the file
        Files.createDirectories(reportFile.getParent());
        writeString(reportFile, STRING_TO_REPLACE);

        assertTrue(Files.exists(reportFile), "Report file should exist");

        runCliSuccessfully("--dir", srcDir.toString(), "--rulesets", DUMMY_RULESET, "--report-file", reportFile.toString());

        assertNotEquals(readString(reportFile), STRING_TO_REPLACE, "Report file should have been overwritten");
    }

    @Test
    void testNonExistentReportFile() throws Exception {
        Path reportFile = tempRoot().resolve("out/reportFile.txt");

        assertFalse(Files.exists(reportFile), "Report file should not exist");

        try {
            runCliSuccessfully("-d", srcDir.toString(), "-R", DUMMY_RULESET, "-r", reportFile.toString());
            assertTrue(Files.exists(reportFile), "Report file should have been created");
        } finally {
            Files.deleteIfExists(reportFile);
        }
    }

    @Test
    void testNonExistentReportFileLongOption() throws Exception {
        Path reportFile = tempRoot().resolve("out/reportFile.txt");

        assertFalse(Files.exists(reportFile), "Report file should not exist");

        runCliSuccessfully("--dir", srcDir.toString(), "--rulesets", DUMMY_RULESET, "--report-file", reportFile.toString());

        assertTrue(Files.exists(reportFile), "Report file should have been created");
    }

    @Test
    void testFileCollectionWithUnknownFiles() throws Exception {
        Path reportFile = tempRoot().resolve("out/reportFile.txt");
        Files.createFile(srcDir.resolve("foo.not_analysable"));
        assertFalse(Files.exists(reportFile), "Report file should not exist");

        // restoring system properties: --debug might change logging properties
        SystemLambda.restoreSystemProperties(() -> {
            runCliSuccessfully("--dir", srcDir.toString(), "--rulesets", DUMMY_RULESET, "--report-file", reportFile.toString(), "--debug");
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
            runCliSuccessfully("-d", srcDir.toString(), "-R", DUMMY_RULESET, "-r", reportFile.toString());
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
            runCliSuccessfully("--dir", srcDir.toString(), "--rulesets", DUMMY_RULESET, "--report-file", reportFile.toString());
            assertTrue(Files.exists(absoluteReportFile), "Report file should have been created");
        } finally {
            Files.deleteIfExists(absoluteReportFile);
        }
    }

    @Test
    void debugLogging() throws Exception {
        // restoring system properties: --debug might change logging properties
        SystemLambda.restoreSystemProperties(() -> {
            String log = runCliSuccessfully("--debug", "--dir", srcDir.toString(), "--rulesets", DUMMY_RULESET);
            assertThat(log, containsString("[main] INFO net.sourceforge.pmd.cli.commands.internal.AbstractPmdSubcommand - Log level is at TRACE"));
        });
    }

    @Test
    void defaultLogging() throws Exception {
        String log = runCliSuccessfully("--dir", srcDir.toString(), "--rulesets", DUMMY_RULESET);
        assertThat(log, containsString("[main] INFO net.sourceforge.pmd.cli.commands.internal.AbstractPmdSubcommand - Log level is at INFO"));
    }

    @Test
    void testDeprecatedRulesetSyntaxOnCommandLine() throws Exception {
        String log = runCli(ExecutionResult.VIOLATIONS_FOUND, "--dir", srcDir.toString(), "--rulesets", "dummy-basic");
        assertThat(log, containsString("Ruleset reference 'dummy-basic' uses a deprecated form, use 'rulesets/dummy/basic.xml' instead"));
    }

    @Test
    void testMissingRuleset() throws Exception {
        final String log = runCli(ExecutionResult.USAGE_ERROR);
        assertThat(log, containsString("Missing required option: '--rulesets=<rulesets>'"));
    }
    
    @Test
    void testMissingSource() throws Exception {
        final String log = runCli(ExecutionResult.USAGE_ERROR, "--rulesets", DUMMY_RULESET);
        assertThat(log, containsString("Please provide a parameter for source root directory"));
    }
    
    @Test
    void testWrongCliOptionsDoPrintUsage() throws Exception {
        final String log = runCli(ExecutionResult.USAGE_ERROR, "--invalid", "--rulesets", DUMMY_RULESET, "-d", srcDir.toString());
        assertThat(log, containsString("Unknown option: '--invalid'"));
        assertThat(log, containsString("Usage: pmd check"));
    }

    @Test
    void testNoRelativizeWithAbsoluteSrcDir() throws Exception {
        assertTrue(srcDir.isAbsolute(), "srcDir should be absolute");
        String log = runCli(ExecutionResult.VIOLATIONS_FOUND, "--dir", srcDir.toString(), "--rulesets",
                DUMMY_RULESET_WITH_VIOLATIONS);

        assertThat(log, containsString(srcDir.resolve("someSource.dummy").toString()));
    }

    @Test
    void testNoRelativizeWithRelativeSrcDir() throws Exception {
        // Note, that we can't reliably change the current working directory for the current java process
        // therefore we use the current directory and make sure, we are at the correct place - in pmd-core
        Path cwd = Paths.get(".").toRealPath();
        assertThat(cwd.toString(), endsWith("pmd-cli"));
        String relativeSrcDir = "src/test/resources/net/sourceforge/pmd/cli/src";
        assertTrue(Files.isDirectory(cwd.resolve(relativeSrcDir)));

        String log = runCli(ExecutionResult.VIOLATIONS_FOUND, "--dir", relativeSrcDir, "--rulesets",
                DUMMY_RULESET_WITH_VIOLATIONS);

        assertThat(log, containsString("\n" + IOUtil.normalizePath(relativeSrcDir + "/somefile.dummy")));
    }

    @Test
    void testNoRelativizeWithRelativeSrcDirParent() throws Exception {
        // Note, that we can't reliably change the current working directory for the current java process
        // therefore we use the current directory and make sure, we are at the correct place - in pmd-core
        Path cwd = Paths.get(".").toRealPath();
        assertThat(cwd.toString(), endsWith("pmd-cli"));
        String relativeSrcDir = IOUtil.normalizePath("src/test/resources/net/sourceforge/pmd/cli/src");
        assertTrue(Files.isDirectory(cwd.resolve(relativeSrcDir)));

        // use the parent directory
        String relativeSrcDirWithParent = relativeSrcDir + File.separator + "..";

        String log = runCli(ExecutionResult.VIOLATIONS_FOUND, "--dir", relativeSrcDirWithParent, "--rulesets",
                DUMMY_RULESET_WITH_VIOLATIONS);

        assertThat(log, containsString("\n" + relativeSrcDirWithParent + IOUtil.normalizePath("/src/somefile.dummy")));
    }

    @Test
    void testRelativizeWithRootRelativeSrcDir() throws Exception {
        // Note, that we can't reliably change the current working directory for the current java process
        // therefore we use the current directory and make sure, we are at the correct place - in pmd-core
        Path cwd = Paths.get(".").toRealPath();
        assertThat(cwd.toString(), endsWith("pmd-cli"));
        String relativeSrcDir = "src/test/resources/net/sourceforge/pmd/cli/src";
        assertTrue(Files.isDirectory(cwd.resolve(relativeSrcDir)));

        String root = cwd.getRoot().toString();

        String log = runCli(ExecutionResult.VIOLATIONS_FOUND, "--dir", relativeSrcDir, "--rulesets",
                DUMMY_RULESET_WITH_VIOLATIONS, "--relativize-paths-with", root);

        String absoluteSrcPath = cwd.resolve(relativeSrcDir).resolve("somefile.dummy").toString();
        assertThat(log, containsString("\n" + absoluteSrcPath));
    }

    @Test
    void testRelativizeWith() throws Exception {
        final String log = runCli(ExecutionResult.VIOLATIONS_FOUND, "--dir", srcDir.toString(), "--rulesets",
                DUMMY_RULESET_WITH_VIOLATIONS, "-z", srcDir.getParent().toString());
        assertThat(log, not(containsString(srcDir.resolve("someSource.dummy").toString())));
        assertThat(log, containsString("\n" + IOUtil.normalizePath("src/someSource.dummy")));
    }

    @Test
    void testRelativizeWithSymLink() throws Exception {
        // srcDir = /tmp/junit123/src
        // symlinkedSrcDir = /tmp/junit123/sources -> /tmp/junit123/src
        Path symlinkedSrcDir = Files.createSymbolicLink(tempRoot().resolve("sources"), srcDir);
        String log = runCli(ExecutionResult.VIOLATIONS_FOUND, "--dir", symlinkedSrcDir.toString(), "--rulesets",
                DUMMY_RULESET_WITH_VIOLATIONS, "-z", symlinkedSrcDir.toString());

        assertThat(log, not(containsString(srcDir.resolve("someSource.dummy").toString())));
        assertThat(log, not(containsString(symlinkedSrcDir.resolve("someSource.dummy").toString())));
        assertThat(log, containsString("\nsomeSource.dummy"));
    }

    @Test
    void testRelativizeWithSymLinkParent() throws Exception {
        // srcDir = /tmp/junit123/src
        // symlinkedSrcDir = /tmp/junit-relativize-with-123 -> /tmp/junit123/src
        Path tempPath = Files.createTempDirectory("junit-relativize-with-");
        Files.delete(tempPath);
        Path symlinkedSrcDir = Files.createSymbolicLink(tempPath, srcDir);
        // relativizing against parent of symlinkedSrcDir: /tmp
        String log = runCli(ExecutionResult.VIOLATIONS_FOUND, "--dir", symlinkedSrcDir.toString(), "--rulesets",
                DUMMY_RULESET_WITH_VIOLATIONS, "-z", symlinkedSrcDir.getParent().toString());

        assertThat(log, not(containsString(srcDir.resolve("someSource.dummy").toString())));
        assertThat(log, not(containsString(symlinkedSrcDir.resolve("someSource.dummy").toString())));
        // base path is symlinkedSrcDir without /tmp: e.g. junit-relativize-with-123
        String basePath = symlinkedSrcDir.getParent().relativize(symlinkedSrcDir).toString();
        assertThat(log, containsString("\n" + basePath + File.separator + "someSource.dummy"));
    }

    @Test
    void testRelativizeWithMultiple() throws Exception {
        String log = runCli(ExecutionResult.VIOLATIONS_FOUND, "--dir", srcDir.toString(), "--rulesets",
                DUMMY_RULESET_WITH_VIOLATIONS, "-z", srcDir.getParent().toString(), "-z", srcDir.toString());

        assertThat(log, not(containsString(srcDir.resolve("someSource.dummy").toString())));
        assertThat(log, containsString("\nsomeSource.dummy"));
    }

    @Test
    void testRelativizeWithFileIsError() throws Exception {
        String log = runCli(ExecutionResult.USAGE_ERROR, "--dir", srcDir.toString(), "--rulesets",
                DUMMY_RULESET_WITH_VIOLATIONS, "-z", srcDir.resolve("someSource.dummy").toString());

        assertThat(log, containsString(
                "Expected a directory path for option '--relativize-paths-with', found a file: "
                        + srcDir.resolve("someSource.dummy")));
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
        final List<String> argList = new ArrayList<>();
        
        // Always run against dummy language without logging not cache to remove all logging noise
        argList.add("check");
        argList.add("--use-version");
        argList.add("dummy-1.0");
        argList.add("--no-cache");
        argList.add("--no-progress");
        
        return argList;
    }

    public static class FooRule extends MockRule {
        @Override
        public void apply(Node node, RuleContext ctx) {
            ctx.addViolation(node);
        }
    }
}
