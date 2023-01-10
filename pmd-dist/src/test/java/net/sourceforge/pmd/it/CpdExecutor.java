/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.it;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;

import java.nio.file.Path;
import java.util.List;

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

    /**
     * Executes CPD found in tempDir with the given command line arguments.
     * @param tempDir the directory, to which the binary distribution has been extracted
     * @param arguments the arguments to execute CPD with
     * @return collected result of the execution
     * @throws Exception if the execution fails for any reason (executable not found, ...)
     */
    public static ExecutionResult runCpd(Path tempDir, String... arguments) throws Exception {
        String cmd;
        List<String> args;
        if (SystemUtils.IS_OS_WINDOWS) {
            cmd = "/bin/pmd.bat";
        } else {
            cmd = "/bin/pmd";
        }
        args = listOf("cpd", arguments);
        cmd = tempDir.resolve(PMD_BIN_PREFIX + PMDVersion.VERSION + cmd).toAbsolutePath().toString();
        return PMDExecutor.runCommand(cmd, args, null);
    }
}
