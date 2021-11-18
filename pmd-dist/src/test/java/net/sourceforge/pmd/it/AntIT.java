/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.it;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SystemUtils;
import org.junit.Assume;
import org.junit.Test;

import net.sourceforge.pmd.PMDVersion;

/**
 * This test calls ant in a fake terminal to make sure we have a {@link java.io.Console} connected.
 * This however only works under linux.
 * <p>
 * See <a href="# https://stackoverflow.com/questions/1401002/how-to-trick-an-application-into-thinking-its-stdout-is-a-terminal-not-a-pipe/20401674#20401674">How to trick an application into thinking its stdout is a terminal, not a pipe</a>.
 */
public class AntIT extends AbstractBinaryDistributionTest {

    @Test
    public void runAnt() throws IOException, InterruptedException {
        Assume.assumeTrue(SystemUtils.IS_OS_LINUX);

        String antBasepath = new File("target/ant").getAbsolutePath();
        String pmdHome = tempDir.resolve(PMD_BIN_PREFIX + PMDVersion.VERSION).toAbsolutePath().toString();
        File antTestProjectFolder = prepareAntTestProjectFolder();

        ExecutionResult result = runAnt(antBasepath, pmdHome, antTestProjectFolder);
        result.assertExecutionResult(0, "BUILD SUCCESSFUL");
        result.assertExecutionResult(0, "NoPackage"); // the no package rule
    }


    private File prepareAntTestProjectFolder() throws IOException {
        File sourceProjectFolder = new File("src/test/resources/ant-it");
        File projectFolder = folder.newFolder();
        FileUtils.copyDirectory(sourceProjectFolder, projectFolder);
        return projectFolder;
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
                    String output = IOUtils.toString(process.getInputStream(), StandardCharsets.UTF_8);
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
