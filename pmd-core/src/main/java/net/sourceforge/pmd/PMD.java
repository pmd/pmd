/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.pmd.annotation.InternalApi;
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
import net.sourceforge.pmd.internal.util.FileCollectionUtil;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.document.FileCollector;
import net.sourceforge.pmd.processor.AbstractPMDProcessor;
import net.sourceforge.pmd.processor.MonoThreadProcessor;
import net.sourceforge.pmd.processor.MultiThreadProcessor;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.util.database.DBMSMetadata;
import net.sourceforge.pmd.util.database.DBURI;
import net.sourceforge.pmd.util.database.SourceObject;
import net.sourceforge.pmd.util.datasource.DataSource;
import net.sourceforge.pmd.util.datasource.ReaderDataSource;
import net.sourceforge.pmd.util.log.ScopedLogHandlersManager;
import net.sourceforge.pmd.util.log.internal.SimpleMessageReporter;

/**
 * Entry point for PMD's CLI. Use {@link #runPmd(PMDConfiguration)}
 * or {@link #runPmd(String...)} to mimic a CLI run. This class is
 * not a supported programmatic API anymore, use {@link PmdAnalysis}
 * for fine control over the PMD integration and execution.
 *
 * <p><strong>Warning:</strong> This class is not intended to be instantiated or subclassed. It will
 * be made final in PMD7.
 */
public class PMD {

    private static final Logger LOG = Logger.getLogger(PMD.class.getName());

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

    /**
     * Contains the configuration with which this PMD instance has been created.
     *
     * @deprecated this configuration field is unused and will be removed. This class
     * {@link PMD} should not be instantiated or subclassed. If you need a PMDConfiguration,
     * create a {@link PMDConfiguration} instance directly.
     */
    @Deprecated
    protected final PMDConfiguration configuration;

    private final SourceCodeProcessor rulesetsFileProcessor;

    /**
     * Constant that contains always the current version of PMD.
     * @deprecated Use {@link PMDVersion#VERSION} instead.
     */
    @Deprecated // to be removed with PMD 7.0.0.
    public static final String VERSION = PMDVersion.VERSION;

    /**
     * Create a PMD instance using a default Configuration. Changes to the
     * configuration may be required.
     *
     * @deprecated Just use the static methods, and maintain your {@link PMDConfiguration} separately.
     */
    @Deprecated
    public PMD() {
        this(new PMDConfiguration());
    }

    /**
     * Create a PMD instance using the specified Configuration.
     *
     * @param configuration The runtime Configuration of PMD to use.
     *
     * @deprecated Just use the static methods, and maintain your {@link PMDConfiguration} separately.
     */
    @Deprecated
    public PMD(PMDConfiguration configuration) {
        this.configuration = configuration;
        this.rulesetsFileProcessor = new SourceCodeProcessor(configuration);
    }

    /**
     * Parses the given string as a database uri and returns a list of
     * datasources.
     *
     * @param uriString
     *            the URI to parse
     * @return list of data sources
     * @throws PMDException
     *             if the URI couldn't be parsed
     * @see DBURI
     *
     * @deprecated Will be hidden as part of the parsing of {@link PMD#getApplicableFiles(PMDConfiguration, Set)}
     */
    @Deprecated
    public static List<DataSource> getURIDataSources(String uriString) throws PMDException {
        List<DataSource> dataSources = new ArrayList<>();

        try {
            DBURI dbUri = new DBURI(uriString);
            DBMSMetadata dbmsMetadata = new DBMSMetadata(dbUri);
            LOG.log(Level.FINE, "DBMSMetadata retrieved");
            List<SourceObject> sourceObjectList = dbmsMetadata.getSourceObjectList();
            LOG.log(Level.FINE, "Located {0} database source objects", sourceObjectList.size());
            for (SourceObject sourceObject : sourceObjectList) {
                String falseFilePath = sourceObject.getPseudoFileName();
                LOG.log(Level.FINEST, "Adding database source object {0}", falseFilePath);

                try {
                    dataSources.add(new ReaderDataSource(dbmsMetadata.getSourceCode(sourceObject), falseFilePath));
                } catch (SQLException ex) {
                    if (LOG.isLoggable(Level.WARNING)) {
                        LOG.log(Level.WARNING, "Cannot get SourceCode for " + falseFilePath + "  - skipping ...", ex);
                    }
                }
            }
        } catch (URISyntaxException e) {
            throw new PMDException("Cannot get DataSources from DBURI - \"" + uriString + "\"", e);
        } catch (SQLException e) {
            throw new PMDException(
                    "Cannot get DataSources from DBURI, couldn't access the database - \"" + uriString + "\"", e);
        } catch (ClassNotFoundException e) {
            throw new PMDException(
                    "Cannot get DataSources from DBURI, probably missing database jdbc driver - \"" + uriString + "\"",
                    e);
        } catch (Exception e) {
            throw new PMDException("Encountered unexpected problem with URI \"" + uriString + "\"", e);
        }
        return dataSources;
    }

    /**
     * Helper method to get a configured parser for the requested language. The
     * parser is configured based on the given {@link PMDConfiguration}.
     *
     * @param languageVersion
     *            the requested language
     * @param configuration
     *            the given configuration
     * @return the pre-configured parser
     *
     * @deprecated This is internal
     */
    @Deprecated
    @InternalApi
    public static Parser parserFor(LanguageVersion languageVersion, PMDConfiguration configuration) {

        // TODO Handle Rules having different parser options.
        LanguageVersionHandler languageVersionHandler = languageVersion.getLanguageVersionHandler();
        if (languageVersionHandler == null) {
            throw new IllegalArgumentException(MessageFormat.format("Language version ''{0}'' does not support parsing!", languageVersion.getTerseName()));
        }
        ParserOptions options = languageVersionHandler.getDefaultParserOptions();
        if (configuration != null) {
            options.setSuppressMarker(configuration.getSuppressMarker());
        }
        return languageVersionHandler.getParser(options);
    }

    /**
     * Get the runtime configuration. The configuration can be modified to
     * affect how PMD behaves.
     *
     * @return The configuration.
     * @see PMDConfiguration
     *
     * @deprecated Don't create a PMD instance just to create a {@link PMDConfiguration}
     */
    @Deprecated
    public PMDConfiguration getConfiguration() {
        return configuration;
    }

    /**
     * Gets the source code processor.
     *
     * @return SourceCodeProcessor
     * @deprecated Source code processor is internal
     */
    @Deprecated
    public SourceCodeProcessor getSourceCodeProcessor() {
        return rulesetsFileProcessor;
    }

    /**
     * This method is the main entry point for command line usage.
     *
     * @param configuration the configuration to use
     *
     * @return number of violations found. Returns -1 in case of error.
     *
     * @deprecated Use {@link #runPmd(PMDConfiguration)}. Note that the return
     *     value of doPMD changed in PMD 6.44.0 to return -1 in case of error.
     *     Previously zero was returned in that case.
     */
    @Deprecated
    @InternalApi
    public static int doPMD(PMDConfiguration configuration) {
        LOG.fine("Current classpath:\n" + System.getProperty("java.class.path"));
        try (PmdAnalysis pmd = PmdAnalysis.create(configuration)) {
            if (pmd.getRulesets().isEmpty()) {
                return pmd.getReporter().numErrors() > 0 ? -1 : 0;
            }
            try {
                Report report = pmd.performAnalysisAndCollectReport();

                if (!report.getProcessingErrors().isEmpty()) {
                    printErrorDetected(report.getProcessingErrors().size());
                }

                return report.getViolations().size();
            } catch (Exception e) {
                pmd.getReporter().errorEx("Exception during processing", e);
                printErrorDetected(1);
                return -1;
            }
        }
    }

    /**
     * Creates a new rule context, initialized with a new, empty report.
     *
     * @param sourceCodeFilename
     *            the source code filename
     * @param sourceCodeFile
     *            the source code file
     * @return the rule context
     *
     * @deprecated Not useful
     */
    @Deprecated
    public static RuleContext newRuleContext(String sourceCodeFilename, File sourceCodeFile) {

        RuleContext context = new RuleContext();
        context.setSourceCodeFile(sourceCodeFile);
        context.setReport(new Report());
        return context;
    }

    /**
     * Run PMD on a list of files using multiple threads - if more than one is
     * available
     *
     * @param configuration
     *            Configuration
     * @param ruleSetFactory
     *            RuleSetFactory
     * @param files
     *            List of {@link DataSource}s
     * @param ctx
     *            RuleContext
     * @param renderers
     *            List of {@link Renderer}s
     *
     * @deprecated Use {@link PmdAnalysis}
     */
    @Deprecated
    public static void processFiles(final PMDConfiguration configuration, final RuleSetFactory ruleSetFactory,
                                    final List<DataSource> files, final RuleContext ctx, final List<Renderer> renderers) {
        // Note that this duplicates the other routine, because the old behavior was
        // that we parsed rulesets (a second time) inside the processor execution.
        // To not mess up error handling, we keep this behavior.

        encourageToUseIncrementalAnalysis(configuration);
        sortFiles(configuration, files);
        // Make sure the cache is listening for analysis results
        ctx.getReport().addListener(configuration.getAnalysisCache());

        final RuleSetFactory silentFactory = ruleSetFactory.toLoader().warnDeprecated(false).toFactory();
        newFileProcessor(configuration).processFiles(silentFactory, files, ctx, renderers);
        configuration.getAnalysisCache().persist();
    }

    /**
     * Run PMD using the given configuration. This replaces the other overload.
     *
     * @param configuration Configuration for the run. Note that the files,
     *                      and rulesets, are ignored, as they are supplied
     *                      as parameters
     * @param rulesets      Parsed rulesets
     * @param files         Files to process, will be closed by this method.
     * @param renderers     Renderers that render the report
     *
     * @return Report in which violations are accumulated
     *
     * @throws RuntimeException If processing fails
     *
     * @deprecated Use {@link PmdAnalysis}
     */
    @Deprecated
    public static Report processFiles(final PMDConfiguration configuration,
                                      final List<RuleSet> rulesets,
                                      final Collection<? extends DataSource> files,
                                      final List<Renderer> renderers) {
        try (PmdAnalysis builder = PmdAnalysis.createWithoutCollectingFiles(configuration)) {
            builder.addRuleSets(rulesets);
            builder.addRenderers(renderers);
            List<DataSource> sortedFiles = new ArrayList<>(files);
            sortFiles(configuration, sortedFiles);
            return builder.performAnalysisImpl(sortedFiles);
        }
    }

    private static void sortFiles(final PMDConfiguration configuration, final List<DataSource> files) {
        if (configuration.isStressTest()) {
            // randomize processing order
            Collections.shuffle(files);
        } else {
            final boolean useShortNames = configuration.isReportShortNames();
            final String inputPaths = configuration.getInputPaths();
            Collections.sort(files, new Comparator<DataSource>() {
                @Override
                public int compare(DataSource left, DataSource right) {
                    String leftString = left.getNiceFileName(useShortNames, inputPaths);
                    String rightString = right.getNiceFileName(useShortNames, inputPaths);
                    return leftString.compareTo(rightString);
                }
            });
        }
    }

    static void encourageToUseIncrementalAnalysis(final PMDConfiguration configuration) {
        if (!configuration.isIgnoreIncrementalAnalysis()
            && configuration.getAnalysisCache() instanceof NoopAnalysisCache
            && LOG.isLoggable(Level.WARNING)) {
            final String version =
                PMDVersion.isUnknown() || PMDVersion.isSnapshot() ? "latest" : "pmd-" + PMDVersion.VERSION;
            LOG.warning("This analysis could be faster, please consider using Incremental Analysis: "
                            + "https://pmd.github.io/" + version + "/pmd_userdocs_incremental_analysis.html");
        }
    }

    /*
     * Check if multithreaded support is available. ExecutorService can also
     * be disabled if threadCount is not positive, e.g. using the
     * "-threads 0" command line option.
     */
    private static AbstractPMDProcessor newFileProcessor(final PMDConfiguration configuration) {
        return configuration.getThreads() > 0 ? new MultiThreadProcessor(configuration) : new MonoThreadProcessor(configuration);
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
            FileCollector collector = FileCollectionUtil.collectFiles(configuration, languages, new SimpleMessageReporter(LOG));
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
    public static int run(String[] args) {
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
            LOG.warning("Some deprecated options were used on the command-line, including " + first.getKey());
            LOG.warning("Consider replacing it with " + first.getValue());
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
        LOG.severe(msg);
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

        final Level logLevel = configuration.isDebug() ? Level.FINER : Level.INFO;
        final ScopedLogHandlersManager logHandlerManager = new ScopedLogHandlersManager(logLevel, new ConsoleHandler());
        final Level oldLogLevel = LOG.getLevel();
        // Need to do this, since the static logger has already been initialized
        // at this point
        LOG.setLevel(logLevel);

        StatusCode status;
        try {
            int violations = PMD.doPMD(configuration);
            if (violations < 0) {
                status = StatusCode.ERROR;
            } else if (violations > 0 && configuration.isFailOnViolation()) {
                status = StatusCode.VIOLATIONS_FOUND;
            } else {
                status = StatusCode.OK;
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            status = StatusCode.ERROR;
        } finally {
            logHandlerManager.close();
            LOG.setLevel(oldLogLevel);

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
