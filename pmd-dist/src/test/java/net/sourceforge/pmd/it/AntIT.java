/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.it;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import net.sourceforge.pmd.PMDVersion;
import net.sourceforge.pmd.internal.util.IOUtil;

/**
 * This test calls ant in a fake terminal to make sure we have a {@link java.io.Console} connected.
 * This however only works under linux.
 * <p>
 * See <a href="# https://stackoverflow.com/questions/1401002/how-to-trick-an-application-into-thinking-its-stdout-is-a-terminal-not-a-pipe/20401674#20401674">How to trick an application into thinking its stdout is a terminal, not a pipe</a>.
 */
class AntIT extends AbstractBinaryDistributionTest {

    @Test
    @EnabledOnOs(OS.LINUX)
    void runAnt() throws IOException, InterruptedException {
        String antBasepath = new File("target/ant").getAbsolutePath();
        String pmdHome = tempDir.resolve(PMD_BIN_PREFIX + PMDVersion.VERSION).toAbsolutePath().toString();
        File antTestProjectFolder = prepareAntTestProjectFolder();

        ExecutionResult result = runAnt(antBasepath, pmdHome, antTestProjectFolder);
        result.assertExecutionResult(0, "BUILD SUCCESSFUL");
        result.assertExecutionResult(0, "NoPackage"); // the no package rule
    }


    private File prepareAntTestProjectFolder() throws IOException {
        final Path sourceProjectFolder = new File("src/test/resources/ant-it").toPath();
        final Path projectFolder = Files.createTempDirectory(folder, null);
        Files.walkFileTree(sourceProjectFolder, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                assert !dir.isAbsolute();
                Path target = projectFolder.resolve(sourceProjectFolder.relativize(dir));
                if (!target.toFile().exists()) {
                    target.toFile().mkdir();
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                assert !file.isAbsolute();
                Path target = projectFolder.resolve(sourceProjectFolder.relativize(file));
                Files.copy(file, target);
                return FileVisitResult.CONTINUE;
            }
        });
        return projectFolder.toFile();
    }


    private ExecutionResult runAnt(String antLibPath, String pmdHomePath, File antTestProjectFolder)
            throws IOException, InterruptedException {
        String cmd = System.getenv("JAVA_HOME") + "/bin/java" + " -cp \"" + antLibPath + "/*\""
                + " -jar " + antLibPath + "/ant-launcher.jar -Dpmd.home=" + pmdHomePath;

        // https://stackoverflow.com/questions/1401002/how-to-trick-an-application-into-thinking-its-stdout-is-a-terminal-not-a-pipe/20401674#20401674
        ProcessBuilder pb = new ProcessBuilder("script", "-qfec", cmd, "/dev/null");
        pb.directory(antTestProjectFolder);
        pb.redirectErrorStream(true);

        final ExecutionResult.Builder result = new ExecutionResult.Builder();
        final Process process = pb.start();
        Thread outputReader = new Thread(new Runnable() {
            @Override
            public void run() {
                try (InputStream in = process.getInputStream()) {
                    String output = IOUtil.readToString(process.getInputStream(), StandardCharsets.UTF_8);
                    result.withOutput(output);
                } catch (IOException e) {
                    result.withOutput("Exception occurred: " + e.toString());
                }
            }
        });
        outputReader.start();
        int exitCode = process.waitFor();
        outputReader.join(TimeUnit.SECONDS.toMillis(5));

        result.withExitCode(exitCode);
        return result.build();
    }
}
