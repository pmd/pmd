/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import net.sourceforge.pmd.benchmark.TextTimingReportRenderer;
import net.sourceforge.pmd.benchmark.TimeTracker;
import net.sourceforge.pmd.benchmark.TimedOperation;
import net.sourceforge.pmd.benchmark.TimedOperationCategory;
import net.sourceforge.pmd.benchmark.TimingReport;
import net.sourceforge.pmd.benchmark.TimingReportRenderer;
import net.sourceforge.pmd.cache.NoopAnalysisCache;
import net.sourceforge.pmd.cli.PMDCommandLineInterface;
import net.sourceforge.pmd.cli.PmdParametersParseResult;
import net.sourceforge.pmd.cli.internal.CliMessages;
import net.sourceforge.pmd.internal.Slf4jSimpleConfiguration;
import net.sourceforge.pmd.internal.util.FileCollectionUtil;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.document.FileCollector;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.util.database.DBMSMetadata;
import net.sourceforge.pmd.util.database.DBURI;
import net.sourceforge.pmd.util.database.SourceObject;
import net.sourceforge.pmd.util.datasource.DataSource;
import net.sourceforge.pmd.util.datasource.ReaderDataSource;
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


    /**
     * Parses the given string as a database uri and returns a list of
     * datasources.
     *
     * @param uriString the URI to parse
     *
     * @return list of data sources
     *
     * @throws IOException if the URI couldn't be parsed
     * @see DBURI
     *
     * @deprecated Will be hidden as part of the parsing of {@link PMD#getApplicableFiles(PMDConfiguration, Set)}
     */
    @Deprecated
    public static List<DataSource> getURIDataSources(String uriString) throws IOException {
        List<DataSource> dataSources = new ArrayList<>();

        try {
            DBURI dbUri = new DBURI(uriString);
            DBMSMetadata dbmsMetadata = new DBMSMetadata(dbUri);
            log.debug("DBMSMetadata retrieved");
            List<SourceObject> sourceObjectList = dbmsMetadata.getSourceObjectList();
            log.debug("Located {} database source objects", sourceObjectList.size());
            for (SourceObject sourceObject : sourceObjectList) {
                String falseFilePath = sourceObject.getPseudoFileName();
                log.trace("Adding database source object {}", falseFilePath);

                try {
                    dataSources.add(new ReaderDataSource(dbmsMetadata.getSourceCode(sourceObject), falseFilePath));
                } catch (SQLException ex) {
                    log.warn("Cannot get SourceCode for {} - skipping ...", falseFilePath, ex);
                }
            }
        } catch (URISyntaxException e) {
            throw new IOException("Cannot get DataSources from DBURI - \"" + uriString + "\"", e);
        } catch (SQLException e) {
            throw new IOException("Cannot get DataSources from DBURI, couldn't access the database - \"" + uriString + "\"", e);
        } catch (ClassNotFoundException e) {
            throw new IOException("Cannot get DataSources from DBURI, probably missing database jdbc driver - \"" + uriString + "\"", e);
        } catch (Exception e) {
            throw new IOException("Encountered unexpected problem with URI \"" + uriString + "\"", e);
        }
        return dataSources;
    }

    /**
     * This method is the main entry point for command line usage.
     *
     * @param configuration the configuration to use
     *
     * @return number of violations found.
     *
     * @deprecated Use {@link #runPmd(PMDConfiguration)}.
     */
    @Deprecated
    @InternalApi
    public static int doPMD(PMDConfiguration configuration) {
        try (PmdAnalysis pmd = PmdAnalysis.create(configuration)) {
            if (pmd.getRulesets().isEmpty()) {
                return PMDCommandLineInterface.NO_ERRORS_STATUS;
            }
            try {
                Report report = pmd.performAnalysisAndCollectReport();

                Report report = reportBuilder.getResult();
            if (!report.getProcessingErrors().isEmpty()) {
                printErrorDetected(report.getProcessingErrors().size());
            }

                return report.getViolations().size();
            } catch (Exception e) {
                pmd.getLog().errorEx("Exception during processing", e);
                printErrorDetected(1);
                return PMDCommandLineInterface.NO_ERRORS_STATUS; // fixme?
            }
        }
    }

    private static List<RuleSet> getRuleSetsWithBenchmark(List<String> rulesetPaths, RuleSetLoader factory) {
        try (TimedOperation ignored = TimeTracker.startOperation(TimedOperationCategory.LOAD_RULES)) {
            List<RuleSet> ruleSets;
            try {
                ruleSets = factory.loadFromResources(rulesetPaths);
                printRuleNamesInDebug(ruleSets);
                if (isEmpty(ruleSets)) {
                    String msg = "No rules found. Maybe you misspelled a rule name? ("
                        + String.join(",", rulesetPaths) + ')';
                    log.error(msg);
                    throw new IllegalArgumentException(msg);
                }
            } catch (RuleSetLoadException rsnfe) {
                log.error("Ruleset not found", rsnfe);
                throw rsnfe;
            }
            return ruleSets;
        }
    }

    private static boolean isEmpty(List<RuleSet> rsets) {
        return rsets.stream().noneMatch(it -> it.size() > 0);
    }

    /**
     * If in debug modus, print the names of the rules.
     *
     * @param rulesets the RuleSets to print
     */
    private static void printRuleNamesInDebug(List<RuleSet> rulesets) {
        if (log.isDebugEnabled()) {
            for (RuleSet rset : rulesets) {
                for (Rule r : rset.getRules()) {
                    log.debug("Loaded rule {}", r.getName());
                }
            }
        }
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
     * @throws RuntimeException If processing fails
     *
     * @deprecated Use {@link PmdAnalysis}
     */
    @Deprecated
    public static Report processFiles(final PMDConfiguration configuration,
                                      final List<RuleSet> rulesets,
                                      final Collection<? extends DataSource> files,
                                      final List<Renderer> renderers) {
        @SuppressWarnings("PMD.CloseResource")
        PmdAnalysis builder = PmdAnalysis.createWithoutCollectingFiles(configuration);
        for (RuleSet ruleset : rulesets) {
            builder.addRuleSet(ruleset);
        }
        for (Renderer renderer : renderers) {
            builder.addRenderer(renderer);
        }
        List<DataSource> sortedFiles = new ArrayList<>(files);
        sortFiles(configuration, sortedFiles);

        return builder.performAnalysisImpl(sortedFiles);
    }


    /**
     * Remove and return the misconfigured rules from the rulesets and log them
     * for good measure.
     *
     * @param ruleSets RuleSets to prune of broken rules.
     *
     * @return Set<Rule>
     */
    private static Set<Rule> removeBrokenRules(final RuleSets ruleSets) {
        final Set<Rule> brokenRules = new HashSet<>();
        ruleSets.removeDysfunctionalRules(brokenRules);

        for (final Rule rule : brokenRules) {
            log.warn("Removed misconfigured rule: {} cause: {}", rule.getName(), rule.dysfunctionReason());
        }

        return brokenRules;
    }


    private static List<DataSource> sortFiles(final PMDConfiguration configuration, Collection<? extends DataSource> files) {
        // the input collection may be unmodifiable
        List<DataSource> result = new ArrayList<>(files);

        if (configuration.isStressTest()) {
            // randomize processing order
            Collections.shuffle(result);
        } else {
            final boolean useShortNames = configuration.isReportShortNames();
            final String inputPaths = configuration.getInputPaths();
            result.sort((left, right) -> {
                String leftString = left.getNiceFileName(useShortNames, inputPaths);
                String rightString = right.getNiceFileName(useShortNames, inputPaths);
                return leftString.compareTo(rightString);
            });
        }

        return result;
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
     * Determines all the files, that should be analyzed by PMD.
     *
     * @param configuration
     *            contains either the file path or the DB URI, from where to
     *            load the files
     * @param languages
     *            used to filter by file extension
     * @return List of {@link DataSource} of files
     *
     * @deprecated This may leak resources and should not be used directly.
     *     Use {@link PmdAnalysis}.
     */
    @Deprecated
    public static List<DataSource> getApplicableFiles(PMDConfiguration configuration, Set<Language> languages) {
        try (TimedOperation to = TimeTracker.startOperation(TimedOperationCategory.COLLECT_FILES)) {
            @SuppressWarnings("PMD.CloseResource") // if we close the collector, data sources become invalid
            FileCollector collector = FileCollectionUtil.collectFiles(configuration, languages, new SimplePmdLogger(LOG));
            return FileCollectionUtil.collectorToDataSource(collector);
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
     * exit code without exiting the VM.
     *
     * @param args command line arguments
     *
     * @return the exit code, where <code>0</code> means successful execution,
     *     <code>1</code> means error, <code>4</code> means there have been
     *     violations found.
     *
     * @deprecated Use {@link #runPmd(String...)}.
     */
    @Deprecated
    public static int run(final String[] args) {
        return runPmd(args).toInt();
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
            Slf4jSimpleConfiguration.reconfigureDefaultLogLevel(Level.DEBUG);
            // need to reload the logger with the new configuration
            log = LoggerFactory.getLogger(PMD.class);
        }
        // always install java.util.logging to slf4j bridge
        Slf4jSimpleConfiguration.installJulBridge();
        // logging, mostly for testing purposes
        Level defaultLogLevel = Slf4jSimpleConfiguration.getDefaultLogLevel();
        log.atLevel(defaultLogLevel).log("Log level is at {}", defaultLogLevel);

        StatusCode status;
        try {
            int violations = PMD.doPMD(configuration);
            if (violations > 0 && configuration.isFailOnViolation()) {
                status = StatusCode.VIOLATIONS_FOUND;
            } else {
                status = StatusCode.OK;
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            status = StatusCode.ERROR;
        } finally {
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
        return status;
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
