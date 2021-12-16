/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static net.sourceforge.pmd.util.CollectionUtil.map;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.pmd.Report.GlobalReportBuilderListener;
import net.sourceforge.pmd.benchmark.TextTimingReportRenderer;
import net.sourceforge.pmd.benchmark.TimeTracker;
import net.sourceforge.pmd.benchmark.TimedOperation;
import net.sourceforge.pmd.benchmark.TimedOperationCategory;
import net.sourceforge.pmd.benchmark.TimingReport;
import net.sourceforge.pmd.benchmark.TimingReportRenderer;
import net.sourceforge.pmd.cache.NoopAnalysisCache;
import net.sourceforge.pmd.cli.PMDCommandLineInterface;
import net.sourceforge.pmd.cli.PmdParametersParseResult;
import net.sourceforge.pmd.internal.util.AssertionUtil;
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
     *
     * @return number of violations found.
     *
     * @deprecated Use {@link #runPmd(PMDConfiguration)}.
     */
    @Deprecated
    public static int doPMD(final PMDConfiguration configuration) {

        // Load the RuleSets
        final RuleSetLoader ruleSetFactory = RuleSetLoader.fromPmdConfig(configuration);
        final List<RuleSet> ruleSets = getRuleSetsWithBenchmark(configuration.getRuleSetPaths(), ruleSetFactory);

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
     */
    @SuppressWarnings("PMD.CloseResource")
    public static Report processFiles(PMDConfiguration configuration,
                                      List<RuleSet> ruleSets,
                                      List<DataSource> files,
                                      List<Renderer> renderers) throws Exception {


        GlobalAnalysisListener rendererListeners = createComposedRendererListener(renderers);
        GlobalReportBuilderListener reportBuilder = new GlobalReportBuilderListener();

        List<GlobalAnalysisListener> allListeners = listOf(reportBuilder, rendererListeners);

        try (GlobalAnalysisListener listener = GlobalAnalysisListener.tee(allListeners)) {
            processFiles(configuration, ruleSets, files, listener);
        }

        return reportBuilder.getResult();
    }

    private static GlobalAnalysisListener createComposedRendererListener(List<Renderer> renderers) throws Exception {
        if (renderers.isEmpty()) {
            return GlobalAnalysisListener.noop();
        }

        List<GlobalAnalysisListener> rendererListeners = new ArrayList<>(renderers.size());
        for (Renderer renderer : renderers) {
            try {
                rendererListeners.add(renderer.newListener());
            } catch (IOException ioe) {
                // close listeners so far, throw their close exception or the ioe
                IOUtil.ensureClosed(rendererListeners, ioe);
                throw AssertionUtil.shouldNotReachHere("ensureClosed should have thrown");
            }
        }
        return GlobalAnalysisListener.tee(rendererListeners);
    }


    /**
     * Run PMD using the given configuration. This replaces the other overload.
     *
     * @param configuration Configuration for the run. Note that the files,
     *                      and rulesets, are ignored, as they are supplied
     *                      as parameters
     * @param ruleSets      Parsed rulesets
     * @param files         Files to process, will be closed by this method.
     * @param listener      Listener to which analysis events are forwarded.
     *                      The listener is NOT closed by this routine and should
     *                      be closed by the caller.
     *
     * @throws Exception If there was a problem when opening or closing the renderers
     */
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
            IOUtil.ensureClosed(inputFiles, ex);
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


    private static List<TextFile> sortFiles(final PMDConfiguration configuration, Collection<? extends TextFile> files) {
        // the input collection may be unmodifiable
        List<TextFile> result = new ArrayList<>(files);
        if (configuration.isStressTest()) {
            // randomize processing order
            Collections.shuffle(result);
        } else {
            result.sort(Comparator.comparing(TextFile::getPathId));
        }

        return result;
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
            System.out.println(PMDCommandLineInterface.buildUsageText());
            System.err.println(parseResult.getError().getMessage());
            return StatusCode.ERROR;
        }
        return runPmd(parseResult.toConfiguration());
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
            if (violations > 0 && configuration.isFailOnViolation()) {
                status = StatusCode.VIOLATIONS_FOUND;
            } else {
                status = StatusCode.OK;
            }
        } catch (Exception e) {
            System.out.println(PMDCommandLineInterface.buildUsageText());
            System.out.println();
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

    private static class AcceptAllFilenames implements FilenameFilter {
        @Override
        public boolean accept(File dir, String name) {
            return true;
        }
    }
}
