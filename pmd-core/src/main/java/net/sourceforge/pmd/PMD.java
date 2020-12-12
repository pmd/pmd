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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
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
import net.sourceforge.pmd.cli.PMDParameters;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageFilenameFilter;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionDiscoverer;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.ast.Parser;
import net.sourceforge.pmd.processor.AbstractPMDProcessor;
import net.sourceforge.pmd.processor.MonoThreadProcessor;
import net.sourceforge.pmd.processor.MultiThreadProcessor;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.util.ClasspathClassLoader;
import net.sourceforge.pmd.util.FileUtil;
import net.sourceforge.pmd.util.IOUtil;
import net.sourceforge.pmd.util.database.DBMSMetadata;
import net.sourceforge.pmd.util.database.DBURI;
import net.sourceforge.pmd.util.database.SourceObject;
import net.sourceforge.pmd.util.datasource.DataSource;
import net.sourceforge.pmd.util.datasource.ReaderDataSource;
import net.sourceforge.pmd.util.log.ScopedLogHandlersManager;

/**
 * This is the main class for interacting with PMD. The primary flow of all Rule
 * process is controlled via interactions with this class. A command line
 * interface is supported, as well as a programmatic API for integrating PMD
 * with other software such as IDEs and Ant.
 */
public class PMD {

    private static final Logger LOG = Logger.getLogger(PMD.class.getName());

    /**
     * The line delimiter used by PMD in outputs. Usually the platform specific
     * line separator.
     */
    public static final String EOL = System.getProperty("line.separator", "\n");

    /** The default suppress marker string. */
    public static final String SUPPRESS_MARKER = "NOPMD";

    /**
     * Contains the configuration with which this PMD instance has been created.
     */
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
     * @return number of violations found.
     */
    public static int doPMD(final PMDConfiguration configuration) {

        // Load the RuleSets
        final RuleSetLoader ruleSetFactory = RuleSetLoader.fromPmdConfig(configuration);
        final RuleSets ruleSets = new RuleSets(getRuleSetsWithBenchmark(configuration.getRuleSetPaths(), ruleSetFactory));

        final Set<Language> languages = getApplicableLanguages(configuration, ruleSets);
        final List<DataSource> files = getApplicableFiles(configuration, languages);

        try {
            Renderer renderer;
            final List<Renderer> renderers;
            try (TimedOperation to = TimeTracker.startOperation(TimedOperationCategory.REPORTING)) {
                renderer = configuration.createRenderer();
                renderers = Collections.singletonList(renderer);
                renderer.setReportFile(configuration.getReportFile());
                renderer.start();
            }

            Report report;
            try (TimedOperation to = TimeTracker.startOperation(TimedOperationCategory.FILE_PROCESSING)) {
                report = processFiles(configuration, Arrays.asList(ruleSets.getAllRuleSets()), files, renderers);
            }

            try (TimedOperation rto = TimeTracker.startOperation(TimedOperationCategory.REPORTING)) {
                renderer.end();
                renderer.flush();
                return report.getViolations().size();
            }
        } catch (final Exception e) {
            String message = e.getMessage();
            if (message == null) {
                LOG.log(Level.SEVERE, "Exception during processing", e);
            } else {
                LOG.severe(message);
            }
            LOG.log(Level.FINE, "Exception during processing", e);
            LOG.info(PMDCommandLineInterface.buildUsageText());
            return PMDCommandLineInterface.NO_ERRORS_STATUS;
        } finally {
            /*
             * Make sure it's our own classloader before attempting to close it....
             * Maven + Jacoco provide us with a cloaseable classloader that if closed
             * will throw a ClassNotFoundException.
             */
            if (configuration.getClassLoader() instanceof ClasspathClassLoader) {
                IOUtil.tryCloseClassLoader(configuration.getClassLoader());
            }
        }
    }

    private static List<RuleSet> getRuleSetsWithBenchmark(List<String> rulesetPaths, RuleSetLoader factory) {
        try (TimedOperation to = TimeTracker.startOperation(TimedOperationCategory.LOAD_RULES)) {
            List<RuleSet> ruleSets;
            try {
                ruleSets = factory.loadFromResources(rulesetPaths);
                printRuleNamesInDebug(ruleSets);
                if (isEmpty(ruleSets)) {
                    String msg = "No rules found. Maybe you misspelled a rule name? ("
                        + String.join(",", rulesetPaths) + ')';
                    LOG.log(Level.SEVERE, msg);
                    throw new IllegalArgumentException(msg);
                }
            } catch (RuleSetLoadException rsnfe) {
                LOG.log(Level.SEVERE, "Ruleset not found", rsnfe);
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
        if (LOG.isLoggable(Level.FINER)) {
            for (RuleSet rset : rulesets) {
                for (Rule r : rset.getRules()) {
                    LOG.finer("Loaded rule " + r.getName());
                }
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
     */
    public static Report processFiles(final PMDConfiguration configuration,
                                      final List<RuleSet> rulesets,
                                      final Collection<? extends DataSource> files,
                                      final List<Renderer> renderers) {
        encourageToUseIncrementalAnalysis(configuration);
        Report report = new Report();
        report.addListener(configuration.getAnalysisCache());

        List<DataSource> sortedFiles = new ArrayList<>(files);
        sortFiles(configuration, sortedFiles);

        RuleContext ctx = new RuleContext();
        ctx.setReport(report);
        newFileProcessor(configuration).processFiles(new RuleSets(rulesets), sortedFiles, ctx, renderers);
        configuration.getAnalysisCache().persist();
        return report;
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

    private static void encourageToUseIncrementalAnalysis(final PMDConfiguration configuration) {
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
     */
    public static List<DataSource> getApplicableFiles(PMDConfiguration configuration, Set<Language> languages) {
        try (TimedOperation to = TimeTracker.startOperation(TimedOperationCategory.COLLECT_FILES)) {
            return internalGetApplicableFiles(configuration, languages);
        }
    }

    private static List<DataSource> internalGetApplicableFiles(PMDConfiguration configuration,
            Set<Language> languages) {
        LanguageFilenameFilter fileSelector = new LanguageFilenameFilter(languages);
        List<DataSource> files = new ArrayList<>();

        if (null != configuration.getInputPaths()) {
            files.addAll(FileUtil.collectFiles(configuration.getInputPaths(), fileSelector));
        }

        if (null != configuration.getInputUri()) {
            String uriString = configuration.getInputUri();
            try {
                List<DataSource> dataSources = getURIDataSources(uriString);

                files.addAll(dataSources);
            } catch (PMDException ex) {
                LOG.log(Level.SEVERE, "Problem with Input URI", ex);
                throw new RuntimeException("Problem with DBURI: " + uriString, ex);
            }
        }

        if (null != configuration.getInputFilePath()) {
            String inputFilePath = configuration.getInputFilePath();
            File file = new File(inputFilePath);
            try {
                if (!file.exists()) {
                    LOG.log(Level.SEVERE, "Problem with Input File Path", inputFilePath);
                    throw new RuntimeException("Problem with Input File Path: " + inputFilePath);
                } else {
                    String filePaths = FileUtil.readFilelist(new File(inputFilePath));
                    files.addAll(FileUtil.collectFiles(filePaths, fileSelector));
                }
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "Problem with Input File", ex);
                throw new RuntimeException("Problem with Input File Path: " + inputFilePath, ex);
            }

        }

        if (null != configuration.getIgnoreFilePath()) {
            String ignoreFilePath = configuration.getIgnoreFilePath();
            File file = new File(ignoreFilePath);
            try {
                if (!file.exists()) {
                    LOG.log(Level.SEVERE, "Problem with Ignore File Path", ignoreFilePath);
                    throw new RuntimeException("Problem with Ignore File Path: " + ignoreFilePath);
                } else {
                    String filePaths = FileUtil.readFilelist(new File(ignoreFilePath));
                    files.removeAll(FileUtil.collectFiles(filePaths, fileSelector));
                }
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "Problem with Ignore File", ex);
                throw new RuntimeException("Problem with Ignore File Path: " + ignoreFilePath, ex);
            }
        }
        return files;
    }

    private static Set<Language> getApplicableLanguages(final PMDConfiguration configuration, final RuleSets ruleSets) {
        final Set<Language> languages = new HashSet<>();
        final LanguageVersionDiscoverer discoverer = configuration.getLanguageVersionDiscoverer();

        for (final Rule rule : ruleSets.getAllRules()) {
            final Language ruleLanguage = rule.getLanguage();
            if (!languages.contains(ruleLanguage)) {
                final LanguageVersion version = discoverer.getDefaultLanguageVersion(ruleLanguage);
                if (RuleSet.applies(rule, version)) {
                    languages.add(ruleLanguage);
                    if (LOG.isLoggable(Level.FINE)) {
                        LOG.fine("Using " + ruleLanguage.getShortName() + " version: " + version.getShortName());
                    }
                }
            }
        }
        return languages;
    }

    /**
     * Entry to invoke PMD as command line tool. Note that this will invoke {@link System#exit(int)}.
     *
     * @param args
     *            command line arguments
     */
    public static void main(String[] args) {
        PMDCommandLineInterface.run(args);
    }

    /**
     * Parses the command line arguments and executes PMD. Returns the
     * exit code without exiting the VM.
     *
     * @param args
     *            command line arguments
     * @return the exit code, where <code>0</code> means successful execution,
     *         <code>1</code> means error, <code>4</code> means there have been
     *         violations found.
     */
    public static int run(final String[] args) {
        final PMDParameters params = PMDCommandLineInterface.extractParameters(new PMDParameters(), args, "pmd");

        if (params.isBenchmark()) {
            TimeTracker.startGlobalTracking();
        }

        int status = PMDCommandLineInterface.NO_ERRORS_STATUS;
        final PMDConfiguration configuration = params.toConfiguration();

        final Level logLevel = params.isDebug() ? Level.FINER : Level.INFO;
        final ScopedLogHandlersManager logHandlerManager = new ScopedLogHandlersManager(logLevel, new ConsoleHandler());
        final Level oldLogLevel = LOG.getLevel();
        // Need to do this, since the static logger has already been initialized
        // at this point
        LOG.setLevel(logLevel);

        try {
            int violations = PMD.doPMD(configuration);
            if (violations > 0 && configuration.isFailOnViolation()) {
                status = PMDCommandLineInterface.VIOLATIONS_FOUND;
            } else {
                status = PMDCommandLineInterface.NO_ERRORS_STATUS;
            }
        } catch (Exception e) {
            System.out.println(PMDCommandLineInterface.buildUsageText());
            System.out.println();
            System.err.println(e.getMessage());
            status = PMDCommandLineInterface.ERROR_STATUS;
        } finally {
            logHandlerManager.close();
            LOG.setLevel(oldLogLevel);

            if (params.isBenchmark()) {
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
}
