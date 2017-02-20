/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.it;

import java.nio.file.Path;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SystemUtils;

import net.sourceforge.pmd.PMD;

/**
 * Executes PMD from command line. Deals with the differences, when PMD is run on Windows or on Linux.
 *
 * @author Andreas Dangel
 */
public class PMDExecutor {
    private static final String PMD_BIN_PREFIX = "pmd-bin-";
    private static final String SOURCE_DIRECTORY_FLAG = "-d";
    private static final String RULESET_FLAG = "-R";
    private static final String FORMAT_FLAG = "-f";
    private static final String FORMATTER = "text";

    private PMDExecutor() {
        // this is a helper class only
    }

    private static PMDExecutionResult runPMDUnix(Path tempDir, String sourceDirectory, String ruleset) throws Exception {
        String cmd = tempDir.resolve(PMD_BIN_PREFIX + PMD.VERSION + "/bin/run.sh").toAbsolutePath().toString();
        ProcessBuilder pb = new ProcessBuilder(cmd, "pmd", SOURCE_DIRECTORY_FLAG, sourceDirectory, RULESET_FLAG, ruleset, FORMAT_FLAG, FORMATTER);
        pb.redirectErrorStream(true);
        Process process = pb.start();
        String output = IOUtils.toString(process.getInputStream());

        int result = process.waitFor();
        return new PMDExecutionResult(result, output);
    }

    private static PMDExecutionResult runPMDWindows(Path tempDir, String sourceDirectory, String ruleset) throws Exception {
        String cmd = tempDir.resolve(PMD_BIN_PREFIX + PMD.VERSION + "/bin/pmd.bat").toAbsolutePath().toString();
        ProcessBuilder pb = new ProcessBuilder(cmd, SOURCE_DIRECTORY_FLAG, sourceDirectory, RULESET_FLAG, ruleset, FORMAT_FLAG, FORMATTER);
        pb.redirectErrorStream(true);
        Process process = pb.start();
        String output = IOUtils.toString(process.getInputStream());

        int result = process.waitFor();
        return new PMDExecutionResult(result, output);
    }

    /**
     * Executes the PMD found in tempDir against the given sourceDirectory path with the given ruleset.
     *
     * @param tempDir the directory, to which the binary distribution has been extracted
     * @param sourceDirectory the source directory, that PMD should analyze
     * @param ruleset the ruleset, that PMD should execute
     * @return collected result of the execution
     * @throws Exception if the execution fails for any reason (executable not found, ...)
     */
    public static PMDExecutionResult runPMD(Path tempDir, String sourceDirectory, String ruleset) throws Exception {
        if (SystemUtils.IS_OS_WINDOWS) {
            return runPMDWindows(tempDir, sourceDirectory, ruleset);
        } else {
            return runPMDUnix(tempDir, sourceDirectory, ruleset);
        }
    }
}
