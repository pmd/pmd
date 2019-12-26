/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.it;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SystemUtils;

import net.sourceforge.pmd.PMDVersion;

/**
 * Executes CPD from command line. Deals with the differences, when CPD is run on Windows or on Linux.
 *
 * @author Andreas Dangel
 */
public class CpdExecutor {
    private static final String PMD_BIN_PREFIX = "pmd-bin-";

    private CpdExecutor() {
        // this is a helper class only
    }

    private static ExecutionResult runCpdUnix(Path tempDir, String ... arguments) throws Exception {
        String cmd = tempDir.resolve(PMD_BIN_PREFIX + PMDVersion.VERSION + "/bin/run.sh").toAbsolutePath().toString();
        ProcessBuilder pb = new ProcessBuilder(cmd, "cpd");
        pb.command().addAll(Arrays.asList(arguments));
        pb.redirectErrorStream(true);
        Process process = pb.start();
        String output = IOUtils.toString(process.getInputStream(), StandardCharsets.UTF_8);

        int result = process.waitFor();
        return new ExecutionResult(result, output, null, null);
    }

    private static ExecutionResult runCpdWindows(Path tempDir, String ... arguments) throws Exception {
        String cmd = tempDir.resolve(PMD_BIN_PREFIX + PMDVersion.VERSION + "/bin/cpd.bat").toAbsolutePath().toString();
        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.command().addAll(Arrays.asList(arguments));
        pb.redirectErrorStream(true);
        Process process = pb.start();
        String output = IOUtils.toString(process.getInputStream(), StandardCharsets.UTF_8);

        int result = process.waitFor();
        return new ExecutionResult(result, output, null, null);
    }

    /**
     * Executes CPD found in tempDir with the given command line arguments.
     * @param tempDir the directory, to which the binary distribution has been extracted
     * @param arguments the arguments to execute CPD with
     * @return collected result of the execution
     * @throws Exception if the execution fails for any reason (executable not found, ...)
     */
    public static ExecutionResult runCpd(Path tempDir, String ... arguments) throws Exception {
        if (SystemUtils.IS_OS_WINDOWS) {
            return runCpdWindows(tempDir, arguments);
        } else {
            return runCpdUnix(tempDir, arguments);
        }
    }
}
