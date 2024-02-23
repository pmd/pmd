/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.dist;

import java.nio.file.Path;
import java.util.Arrays;

/**
 * Executes CPD from command line. Deals with the differences, when CPD is run on Windows or on Linux.
 *
 * @author Andreas Dangel
 */
public class CpdExecutor {
    private CpdExecutor() {
        // this is a helper class only
    }

    /**
     * Executes CPD found in tempDir with the given command line arguments.
     * @param tempDir the directory, to which the binary distribution has been extracted
     * @param arguments the arguments to execute CPD with
     * @return collected result of the execution
     * @throws Exception if the execution fails for any reason (executable not found, ...)
     */
    public static ExecutionResult runCpd(Path tempDir, String... arguments) throws Exception {
        return PMDExecutor.runCommand(tempDir, "cpd", Arrays.asList(arguments));
    }
}
