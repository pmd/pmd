/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import net.sourceforge.pmd.ReportStatsListener.ReportStats;
import net.sourceforge.pmd.benchmark.TextTimingReportRenderer;
import net.sourceforge.pmd.benchmark.TimeTracker;
import net.sourceforge.pmd.benchmark.TimingReport;
import net.sourceforge.pmd.benchmark.TimingReportRenderer;
import net.sourceforge.pmd.cache.NoopAnalysisCache;
import net.sourceforge.pmd.cli.PMDCommandLineInterface;
import net.sourceforge.pmd.cli.PmdParametersParseResult;
import net.sourceforge.pmd.cli.internal.CliMessages;
import net.sourceforge.pmd.internal.Slf4jSimpleConfiguration;
import net.sourceforge.pmd.util.log.PmdLogger;
import net.sourceforge.pmd.util.log.SimplePmdLogger;

/**
 * Entry point for PMD's CLI. Use {@link #runPmd(PMDConfiguration)}
 * or {@link #runPmd(String...)} to mimic a CLI run. This class is
 * not a supported programmatic API anymore, use {@link PmdAnalysis}
 * for fine control over the PMD integration and execution.
 *
 * <p><strong>Warning:</strong> This class is not intended to be instantiated or subclassed. It will
 * be made final in PMD7.
 */
public final class PMD {

    // not final, in order to re-initialize logging
    private static Logger log = LoggerFactory.getLogger(PMD.class);

    /**
     * The line delimiter used by PMD in outputs. Usually the platform specific
     * line separator.
     *
     * @deprecated Use {@link System#lineSeparator()}
     */
    @Deprecated
    public static final String EOL = System.lineSeparator();

    /**
     * The default suppress marker string.
     *
     * @deprecated Use {@link PMDConfiguration#DEFAULT_SUPPRESS_MARKER}
     */
    @Deprecated
    public static final String SUPPRESS_MARKER = PMDConfiguration.DEFAULT_SUPPRESS_MARKER;

    private PMD() {
    }


    private static ReportStatsListener.ReportStats runAndReturnStats(PmdAnalysis pmd) {
        if (pmd.getRulesets().isEmpty()) {
            return ReportStats.empty();
        }

        @SuppressWarnings("PMD.CloseResource")
        ReportStatsListener listener = new ReportStatsListener();

        pmd.addListener(listener);

        try {
            pmd.performAnalysis();
        } catch (Exception e) {
            pmd.getLog().errorEx("Exception during processing", e);
            ReportStats stats = listener.getResult();
            printErrorDetected(1 + stats.getNumErrors());
            return stats; // should have been closed
        }
        ReportStats stats = listener.getResult();

        if (stats.getNumViolations() > 0) {
            printErrorDetected(stats.getNumViolations());
        }

        return stats;
    }


    static void encourageToUseIncrementalAnalysis(final PMDConfiguration configuration) {
        if (!configuration.isIgnoreIncrementalAnalysis()
            && configuration.getAnalysisCache() instanceof NoopAnalysisCache
            && log.isWarnEnabled()) {
            final String version =
                PMDVersion.isUnknown() || PMDVersion.isSnapshot() ? "latest" : "pmd-" + PMDVersion.VERSION;
            log.warn("This analysis could be faster, please consider using Incremental Analysis: "
                            + "https://pmd.github.io/{}/pmd_userdocs_incremental_analysis.html", version);
        }
    }

    /**
     * Entry to invoke PMD as command line tool. Note that this will
     * invoke {@link System#exit(int)}.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        StatusCode exitCode = runPmd(args);
        PMDCommandLineInterface.setStatusCodeOrExit(exitCode.toInt());
    }

    /**
     * Parses the command line arguments and executes PMD. Returns the
     * status code without exiting the VM. Note that the arguments parsing
     * may itself fail and produce a {@link StatusCode#ERROR}. This will
     * print the error message to standard error.
     *
     * @param args command line arguments
     *
     * @return the status code. Note that {@link PMDConfiguration#isFailOnViolation()}
     *     (flag {@code --failOnViolation}) may turn an {@link StatusCode#OK} into a {@link
     *     StatusCode#VIOLATIONS_FOUND}.
     */
    public static StatusCode runPmd(String... args) {
        PmdParametersParseResult parseResult = PmdParametersParseResult.extractParameters(args);

        if (!parseResult.getDeprecatedOptionsUsed().isEmpty()) {
            Entry<String, String> first = parseResult.getDeprecatedOptionsUsed().entrySet().iterator().next();
            log.warn("Some deprecated options were used on the command-line, including {}", first.getKey());
            log.warn("Consider replacing it with {}", first.getValue());
        }

        if (parseResult.isVersion()) {
            System.out.println("PMD " + PMDVersion.VERSION);
            return StatusCode.OK;
        } else if (parseResult.isHelp()) {
            PMDCommandLineInterface.printJcommanderUsageOnConsole();
            System.out.println(PMDCommandLineInterface.buildUsageText());
            return StatusCode.OK;
        } else if (parseResult.isError()) {
            System.err.println(parseResult.getError().getMessage());
            System.err.println(CliMessages.runWithHelpFlagMessage());
            return StatusCode.ERROR;
        }
        return runPmd(parseResult.toConfiguration());
    }

    private static void printErrorDetected(int errors) {
        String msg = CliMessages.errorDetectedMessage(errors, "PMD");
        log.error(msg);
    }

    /**
     * Execute PMD from a configuration. Returns the status code without
     * exiting the VM. This is the main entry point to run a full PMD run
     * with a manually created configuration.
     *
     * @param configuration Configuration to run
     *
     * @return the status code. Note that {@link PMDConfiguration#isFailOnViolation()}
     *     (flag {@code --failOnViolation}) may turn an {@link StatusCode#OK} into a {@link
     *     StatusCode#VIOLATIONS_FOUND}.
     */
    public static StatusCode runPmd(PMDConfiguration configuration) {
        if (configuration.isBenchmark()) {
            TimeTracker.startGlobalTracking();
        }

        // only reconfigure logging, if debug flag was used on command line
        // otherwise just use whatever is in conf/simplelogger.properties which happens automatically
        if (configuration.isDebug()) {
            Slf4jSimpleConfiguration.reconfigureDefaultLogLevel(Level.TRACE);
            // need to reload the logger with the new configuration
            log = LoggerFactory.getLogger(PMD.class);
        }
        PmdLogger pmdLogger = new SimplePmdLogger(log);
        // always install java.util.logging to slf4j bridge
        Slf4jSimpleConfiguration.installJulBridge();
        // logging, mostly for testing purposes
        Level defaultLogLevel = Slf4jSimpleConfiguration.getDefaultLogLevel();
        log.atLevel(defaultLogLevel).log("Log level is at {}", defaultLogLevel);

        try {
            PmdAnalysis pmd;
            try {
                pmd = PmdAnalysis.create(configuration, pmdLogger);
            } catch (Exception e) {
                pmdLogger.errorEx("Could not initialize analysis", e);
                return StatusCode.ERROR;
            }
            try {
                ReportStats stats;
                stats = PMD.runAndReturnStats(pmd);
                if (stats.getNumErrors() > 0 || pmdLogger.numErrors() > 0) {
                    return StatusCode.ERROR;
                } else if (stats.getNumViolations() > 0 && configuration.isFailOnViolation()) {
                    return StatusCode.VIOLATIONS_FOUND;
                } else {
                    return StatusCode.OK;
                }
            } finally {
                pmd.close();
            }

        } catch (Exception e) {
            pmdLogger.errorEx("Exception while running PMD.", e);
            printErrorDetected(1);
            return StatusCode.ERROR;
        } finally {
            finishBenchmarker(configuration);
        }
    }

    private static void finishBenchmarker(PMDConfiguration configuration) {
        if (configuration.isBenchmark()) {
            final TimingReport timingReport = TimeTracker.stopGlobalTracking();

            // TODO get specified report format from config
            final TimingReportRenderer renderer = new TextTimingReportRenderer();
            try {
                // Don't close this writer, we don't want to close stderr
                @SuppressWarnings("PMD.CloseResource")
                final Writer writer = new OutputStreamWriter(System.err);
                renderer.render(timingReport, writer);
            } catch (final IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    /**
     * Represents status codes that are used as exit codes during CLI runs.
     *
     * @see #runPmd(String[])
     */
    public enum StatusCode {
        /** No errors, no violations. This is exit code {@code 0}. */
        OK(0),
        /**
         * Errors were detected, PMD may have not run to the end.
         * This is exit code {@code 1}.
         */
        ERROR(1),
        /**
         * No errors, but PMD found violations. This is exit code {@code 4}.
         * This is only returned if {@link PMDConfiguration#isFailOnViolation()}
         * is set (CLI flag {@code --failOnViolation}).
         */
        VIOLATIONS_FOUND(4);

        private final int code;

        StatusCode(int code) {
            this.code = code;
        }

        /** Returns the exit code as used in CLI. */
        public int toInt() {
            return this.code;
        }

    }

}
