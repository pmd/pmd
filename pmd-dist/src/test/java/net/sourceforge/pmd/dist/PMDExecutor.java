/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.dist;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.SystemUtils;

import net.sourceforge.pmd.internal.util.IOUtil;

/**
 * Executes PMD from command line. Deals with the differences, when PMD is run on Windows or on Linux.
 *
 * @author Andreas Dangel
 */
public class PMDExecutor {
    private static final String SOURCE_DIRECTORY_FLAG = "-d";
    private static final String RULESET_FLAG = "-R";
    private static final String FORMAT_FLAG = "-f";
    private static final String FORMATTER = "text";
    private static final String REPORTFILE_FLAG = "-r";
    private static final String NO_PROGRESSBAR_FLAG = "--no-progress";

    private PMDExecutor() {
        // this is a helper class only
    }

    static ExecutionResult runCommand(Path tempDir, String cmd, List<String> arguments) throws Exception {
        return runCommand(tempDir, cmd, arguments, null);
    }

    static ExecutionResult runCommand(Path tempDir, String cmd, List<String> arguments, Path reportFile) throws Exception {
        final String pmdScript;
        if (SystemUtils.IS_OS_WINDOWS) {
            pmdScript = tempDir.resolve(AbstractBinaryDistributionTest.PMD_BIN_PREFIX + "/bin/pmd.bat").toAbsolutePath().toString();
        } else {
            pmdScript = tempDir.resolve(AbstractBinaryDistributionTest.PMD_BIN_PREFIX + "/bin/pmd").toAbsolutePath().toString();
        }
        ProcessBuilder pb = new ProcessBuilder(pmdScript);
        pb.command().add(cmd);
        pb.command().addAll(arguments);

        pb.redirectErrorStream(false);
        
        // Ensure no ANSI output so tests can properly look at it
        pb.environment().put("PMD_JAVA_OPTS", "-Dpicocli.ansi=false");
        
        final Process process = pb.start();
        final ExecutionResult.Builder result = new ExecutionResult.Builder();

        Thread outputReader = new Thread(new Runnable() {
            @Override
            public void run() {
                String output;
                try {
                    output = IOUtil.readToString(process.getInputStream(), StandardCharsets.UTF_8);
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
                    error = IOUtil.readToString(process.getErrorStream(), StandardCharsets.UTF_8);
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
            try (Reader reader = Files.newBufferedReader(reportFile, StandardCharsets.UTF_8)) {
                report = IOUtil.readToString(reader);
            }
        }
        return result.withExitCode(exitCode).withReport(report).build();
    }

    /**
     * Executes the PMD found in tempDir against the given sourceDirectory path with the given ruleset.
     *
     * @param reportFile the file to write the report to
     * @param tempDir the directory, to which the binary distribution has been extracted
     * @param sourceDirectory the source directory, that PMD should analyze
     * @param ruleset the ruleset, that PMD should execute
     * @return collected result of the execution
     * @throws Exception if the execution fails for any reason (executable not found, ...)
     */
    public static ExecutionResult runPMDRules(Path reportFile, Path tempDir, String sourceDirectory, String ruleset) throws Exception {
        return runPMDRules(reportFile, tempDir, sourceDirectory, ruleset, FORMATTER);
    }

    public static ExecutionResult runPMDRules(Path reportFile, Path tempDir, String sourceDirectory, String ruleset, String formatter) throws Exception {
        return runPMD(reportFile, tempDir, SOURCE_DIRECTORY_FLAG, sourceDirectory, RULESET_FLAG, ruleset,
                    FORMAT_FLAG, formatter, NO_PROGRESSBAR_FLAG);
    }

    /**
     * Executes PMD found in tempDir with the given command line arguments.
     * @param reportFile The location where to store the result. If null, the report will be discarded.
     * @param tempDir the directory, to which the binary distribution has been extracted
     * @param arguments the arguments to execute PMD with
     * @return collected result of the execution
     * @throws Exception if the execution fails for any reason (executable not found, ...)
     */
    public static ExecutionResult runPMD(Path reportFile, Path tempDir, String... arguments) throws Exception {
        List<String> args = new ArrayList<>();
        if (reportFile != null) {
            args.add(REPORTFILE_FLAG);
            args.add(reportFile.toString());
        }
        args.addAll(Arrays.asList(arguments));

        return runCommand(tempDir, "check", args, reportFile);
    }
}
