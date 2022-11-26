/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import net.sourceforge.pmd.Report.GlobalReportBuilderListener;
import net.sourceforge.pmd.benchmark.TextTimingReportRenderer;
import net.sourceforge.pmd.benchmark.TimeTracker;
import net.sourceforge.pmd.benchmark.TimingReport;
import net.sourceforge.pmd.benchmark.TimingReportRenderer;
import net.sourceforge.pmd.cli.PMDCommandLineInterface;
import net.sourceforge.pmd.cli.PmdParametersParseResult;
import net.sourceforge.pmd.internal.LogMessages;
import net.sourceforge.pmd.internal.Slf4jSimpleConfiguration;
import net.sourceforge.pmd.lang.document.TextFile;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.reporting.ReportStats;
import net.sourceforge.pmd.util.datasource.DataSource;
import net.sourceforge.pmd.util.log.MessageReporter;
import net.sourceforge.pmd.util.log.internal.SimpleMessageReporter;

/**
 * Entry point for PMD's CLI. Use {@link #runPmd(PMDConfiguration)}
 * or {@link #runPmd(String...)} to mimic a CLI run. This class is
 * not a supported programmatic API anymore, use {@link PmdAnalysis}
 * for fine control over the PMD integration and execution.
 *
 * <p><strong>Warning:</strong> This class is not intended to be instantiated or subclassed. It will
 * be made final in PMD7.
 *
 * @deprecated This class is to be removed in PMD 7 in favor of a unified PmdCli entry point. {@link PmdAnalysis} should be used for non-CLI use-cases.
 */
@Deprecated
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

    /**
     * Run PMD using the given configuration. This replaces the other overload.
     *
     * @param configuration Configuration for the run. Note that the files,
     *                      and rulesets, are ignored, as they are supplied
     *                      as parameters
     * @param ruleSets      Parsed rulesets
     * @param files         Files to process, will be closed by this method.
     * @param renderers     Renderers that render the report (may be empty)
     *
     * @return Report in which violations are accumulated
     *
     * @throws Exception If there was a problem when opening or closing the renderers
     * @deprecated Use {@link PmdAnalysis}
     */
    @Deprecated
    public static Report processFiles(PMDConfiguration configuration,
                                      List<RuleSet> ruleSets,
                                      Collection<? extends DataSource> files,
                                      List<Renderer> renderers) throws Exception {

        try (PmdAnalysis pmd = PmdAnalysis.create(configuration)) {
            pmd.addRuleSets(ruleSets);
            pmd.addRenderers(renderers);
            @SuppressWarnings("PMD.CloseResource")
            GlobalReportBuilderListener reportBuilder = new GlobalReportBuilderListener();
            List<TextFile> sortedFiles = files.stream()
                                              .map(ds -> TextFile.dataSourceCompat(ds, configuration))
                                              .sorted(Comparator.comparing(TextFile::getPathId))
                                              .collect(Collectors.toList());
            pmd.performAnalysisImpl(listOf(reportBuilder), sortedFiles);
            return reportBuilder.getResult();
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

        // todo these warnings/errors should be output on a PmdRenderer
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
            System.err.println(LogMessages.runWithHelpFlagMessage());
            return StatusCode.ERROR;
        }

        PMDConfiguration configuration = Objects.requireNonNull(
            parseResult.toConfiguration()
        );
        MessageReporter pmdReporter = setupMessageReporter(configuration);
        configuration.setReporter(pmdReporter);

        return runPmd(configuration);
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

        MessageReporter pmdReporter = configuration.getReporter();
        try {
            PmdAnalysis pmd;
            try {
                pmd = PmdAnalysis.create(configuration);
            } catch (Exception e) {
                pmdReporter.errorEx("Could not initialize analysis", e);
                return StatusCode.ERROR;
            }
            try {
                log.debug("Current classpath:\n{}", System.getProperty("java.class.path"));
                ReportStats stats = pmd.runAndReturnStats();
                if (pmdReporter.numErrors() > 0) {
                    // processing errors are ignored
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
            pmdReporter.errorEx("Exception while running PMD.", e);
            PmdAnalysis.printErrorDetected(pmdReporter, 1);
            return StatusCode.ERROR;
        } finally {
            finishBenchmarker(configuration);
        }
    }

    private static @NonNull MessageReporter setupMessageReporter(PMDConfiguration configuration) {
        // only reconfigure logging, if debug flag was used on command line
        // otherwise just use whatever is in conf/simplelogger.properties which happens automatically
        if (configuration.isDebug()) {
            Slf4jSimpleConfiguration.reconfigureDefaultLogLevel(Level.TRACE);
            // need to reload the logger with the new configuration
            log = LoggerFactory.getLogger(PMD.class);
        }
        // create a top-level reporter
        // TODO CLI errors should also be reported through this
        // TODO this should not use the logger as backend, otherwise without
        //  slf4j implementation binding, errors are entirely ignored.
        MessageReporter pmdReporter = new SimpleMessageReporter(log);
        // always install java.util.logging to slf4j bridge
        Slf4jSimpleConfiguration.installJulBridge();
        // logging, mostly for testing purposes
        Level defaultLogLevel = Slf4jSimpleConfiguration.getDefaultLogLevel();
        log.info("Log level is at {}", defaultLogLevel);
        return pmdReporter;
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
     * @deprecated This class is to be removed in PMD 7 in favor of a unified PmdCli entry point.
     */
    @Deprecated
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
