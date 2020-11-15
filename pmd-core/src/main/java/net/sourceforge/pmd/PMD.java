/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static net.sourceforge.pmd.util.CollectionUtil.map;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.pmd.annotation.DeprecatedUntil700;
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
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionDiscoverer;
import net.sourceforge.pmd.processor.AbstractPMDProcessor;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.reporting.GlobalAnalysisListener;
import net.sourceforge.pmd.reporting.GlobalAnalysisListener.ViolationCounterListener;
import net.sourceforge.pmd.util.ClasspathClassLoader;
import net.sourceforge.pmd.util.FileUtil;
import net.sourceforge.pmd.util.IOUtil;
import net.sourceforge.pmd.util.datasource.DataSource;
import net.sourceforge.pmd.util.document.TextFile;
import net.sourceforge.pmd.util.log.ScopedLogHandlersManager;

/**
 * This is the main class for interacting with PMD. The primary flow of all Rule
 * process is controlled via interactions with this class. A command line
 * interface is supported, as well as a programmatic API for integrating PMD
 * with other software such as IDEs and Ant.
 */
public final class PMD {

    private static final Logger LOG = Logger.getLogger(PMD.class.getName());

    /**
     * The line delimiter used by PMD in outputs. Usually the platform specific
     * line separator.
     */
    public static final String EOL = System.getProperty("line.separator", "\n");

    /** The default suppress marker string. */
    public static final String SUPPRESS_MARKER = "NOPMD";

    private PMD() {
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

        final List<RuleSet> ruleSets;
        try (TimedOperation ignored = TimeTracker.startOperation(TimedOperationCategory.LOAD_RULES)) {
            ruleSets = RulesetsFactoryUtils.getRuleSets(configuration.getRuleSets(), ruleSetFactory);
        }
        if (ruleSets == null) {
            return PMDCommandLineInterface.NO_ERRORS_STATUS;
        }

        final Set<Language> languages = getApplicableLanguages(configuration, ruleSets);

        try {

            final List<TextFile> files = FileUtil.getApplicableFiles(configuration, languages);
            Renderer renderer = configuration.createRenderer(true);

            @SuppressWarnings("PMD.CloseResource")
            ViolationCounterListener violationCounter = new ViolationCounterListener();

            try (GlobalAnalysisListener listener = GlobalAnalysisListener.tee(listOf(renderer.newListener(),
                                                                                     violationCounter))) {


                try (TimedOperation to = TimeTracker.startOperation(TimedOperationCategory.FILE_PROCESSING)) {
                    processTextFiles(configuration, ruleSets, files, listener);
                }
            }
            return violationCounter.getResult();
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

    /**
     * Run PMD on a list of files using the number of threads specified
     * by the configuration.
     *
     * TODO rulesets should be validated more strictly upon construction.
     *   We shouldn't be removing rules after construction.
     *
     * @param configuration Configuration
     * @param ruleSets      RuleSetFactory
     * @param files         List of {@link DataSource}s
     * @param listener      RuleContext
     *
     * @throws Exception If an exception occurs while closing the data sources
     *                   Todo wrap that into a known exception type
     *
     * @deprecated Use {@link #processTextFiles(PMDConfiguration, List, List, GlobalAnalysisListener)},
     * which uses a list of {@link TextFile} and not the deprecated {@link DataSource}.
     *
     */
    @Deprecated
    @DeprecatedUntil700
    public static void processFiles(PMDConfiguration configuration,
                                    List<RuleSet> ruleSets,
                                    List<DataSource> files,
                                    GlobalAnalysisListener listener) throws Exception {
        List<TextFile> inputFiles = map(files, ds -> TextFile.dataSourceCompat(ds, configuration));

        processTextFiles(configuration, ruleSets, inputFiles, listener);
    }

    /**
     * Run PMD on a list of files using the number of threads specified
     * by the configuration.
     *
     * TODO rulesets should be validated more strictly upon construction.
     * We shouldn't be removing rules after construction.
     *
     * @param configuration Configuration (the input files and rulesets are ignored)
     * @param ruleSets      RuleSetFactory
     * @param inputFiles    List of input files to process
     * @param listener      RuleContext
     *
     * @throws Exception If an exception occurs while closing the data sources
     *                   Todo wrap that into a known exception type
     */
    public static void processTextFiles(PMDConfiguration configuration,
                                        List<RuleSet> ruleSets,
                                        List<TextFile> inputFiles,
                                        GlobalAnalysisListener listener) throws Exception {

        inputFiles = sortFiles(configuration, inputFiles);

        final RuleSets rs = new RuleSets(ruleSets);


        // todo Just like we throw for invalid properties, "broken rules"
        // shouldn't be a "config error". This is the only instance of
        // config errors...

        for (final Rule rule : removeBrokenRules(rs)) {
            listener.onConfigError(new Report.ConfigurationError(rule, rule.dysfunctionReason()));
        }

        encourageToUseIncrementalAnalysis(configuration);


        // Make sure the cache is listening for analysis results
        listener = GlobalAnalysisListener.tee(listOf(listener, configuration.getAnalysisCache()));

        configuration.getAnalysisCache().checkValidity(rs, configuration.getClassLoader());

        Exception ex = null;
        try (AbstractPMDProcessor processor = AbstractPMDProcessor.newFileProcessor(configuration)) {
            processor.processFiles(rs, inputFiles, listener);
        } catch (Exception e) {
            ex = e;
        } finally {
            configuration.getAnalysisCache().persist();

            // in case we analyzed files within Zip Files/Jars, we need to close them after
            // the analysis is finished
            Exception closed = IOUtil.closeAll(inputFiles);

            if (closed != null) {
                if (ex != null) {
                    ex.addSuppressed(closed);
                } else {
                    ex = closed;
                }
            }
        }

        if (ex != null) {
            throw ex;
        }
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
            if (LOG.isLoggable(Level.WARNING)) {
                LOG.log(Level.WARNING,
                        "Removed misconfigured rule: " + rule.getName() + "  cause: " + rule.dysfunctionReason());
            }
        }

        return brokenRules;
    }


    private static List<TextFile> sortFiles(final PMDConfiguration configuration, List<TextFile> files) {
        // the input collection may be unmodifiable
        files = new ArrayList<>(files);
        if (configuration.isStressTest()) {
            // randomize processing order
            Collections.shuffle(files);
        } else {
            files.sort(Comparator.comparing(TextFile::getPathId));
        }
        return files;
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

    private static Set<Language> getApplicableLanguages(final PMDConfiguration configuration, final List<RuleSet> ruleSets) {
        final Set<Language> languages = new HashSet<>();
        final LanguageVersionDiscoverer discoverer = configuration.getLanguageVersionDiscoverer();

        for (final RuleSet ruleSet : ruleSets) {
            for (Rule rule : ruleSet.getRules()) {
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
