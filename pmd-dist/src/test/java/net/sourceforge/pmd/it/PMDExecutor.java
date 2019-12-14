/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.it;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SystemUtils;

import net.sourceforge.pmd.PMDVersion;

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
    private static final String REPORTFILE_FLAG = "-r";

    private PMDExecutor() {
        // this is a helper class only
    }

    private static ExecutionResult runPMDUnix(Path tempDir, Path reportFile, String ... arguments) throws Exception {
        String cmd = tempDir.resolve(PMD_BIN_PREFIX + PMDVersion.VERSION + "/bin/run.sh").toAbsolutePath().toString();
        List<String> args = new ArrayList<>();
        args.add("pmd");
        args.addAll(Arrays.asList(arguments));
        return runPMD(cmd, args, reportFile);
    }

    private static ExecutionResult runPMDWindows(Path tempDir, Path reportFile, String ... arguments) throws Exception {
        String cmd = tempDir.resolve(PMD_BIN_PREFIX + PMDVersion.VERSION + "/bin/pmd.bat").toAbsolutePath().toString();
        return runPMD(cmd, Arrays.asList(arguments), reportFile);
    }

    private static ExecutionResult runPMD(String cmd, List<String> arguments, Path reportFile) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.command().addAll(arguments);
        pb.redirectErrorStream(false);
        final Process process = pb.start();
        final ExecutionResult.Builder result = new ExecutionResult.Builder();

        Thread outputReader = new Thread(new Runnable() {
            @Override
            public void run() {
                String output;
                try {
                    output = IOUtils.toString(process.getInputStream(), StandardCharsets.UTF_8);
                    result.withOutput(output);
                } catch (IOException e) {
                    result.withOutput("Exception occurred: " + e.toString());
                }
            }
        });
        outputReader.start();
        Thread errorReader = new Thread(new Runnable() {
            @Override
            public void run() {
                String error;
                try {
                    error = IOUtils.toString(process.getErrorStream(), StandardCharsets.UTF_8);
                    result.withErrorOutput(error);
                } catch (IOException e) {
                    result.withErrorOutput("Exception occurred: " + e.toString());
                }
            }
        });
        errorReader.start();

        int exitCode = process.waitFor();
        outputReader.join(TimeUnit.MINUTES.toMillis(5));
        errorReader.join(TimeUnit.MINUTES.toMillis(5));

        String report = null;
        if (reportFile != null) {
            report = IOUtils.toString(reportFile.toUri(), StandardCharsets.UTF_8);
        }
        return result.withExitCode(exitCode).withReport(report).build();
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
    public static ExecutionResult runPMDRules(Path tempDir, String sourceDirectory, String ruleset) throws Exception {
        Path reportFile = Files.createTempFile("pmd-it-report", "txt");
        reportFile.toFile().deleteOnExit();

        if (SystemUtils.IS_OS_WINDOWS) {
            return runPMDWindows(tempDir, reportFile, SOURCE_DIRECTORY_FLAG, sourceDirectory, RULESET_FLAG, ruleset,
                    FORMAT_FLAG, FORMATTER, REPORTFILE_FLAG, reportFile.toAbsolutePath().toString());
        } else {
            return runPMDUnix(tempDir, reportFile, SOURCE_DIRECTORY_FLAG, sourceDirectory, RULESET_FLAG, ruleset,
                    FORMAT_FLAG, FORMATTER, REPORTFILE_FLAG, reportFile.toAbsolutePath().toString());
        }
    }

    /**
     * Executes PMD found in tempDir with the given command line arguments.
     * @param tempDir the directory, to which the binary distribution has been extracted
     * @param arguments the arguments to execute PMD with
     * @return collected result of the execution
     * @throws Exception if the execution fails for any reason (executable not found, ...)
     */
    public static ExecutionResult runPMD(Path tempDir, String ... arguments) throws Exception {
        if (SystemUtils.IS_OS_WINDOWS) {
            return runPMDWindows(tempDir, null, arguments);
        } else {
            return runPMDUnix(tempDir, null, arguments);
        }
    }
}
