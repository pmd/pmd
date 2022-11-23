/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import net.sourceforge.pmd.Report.GlobalReportBuilderListener;
import net.sourceforge.pmd.benchmark.TimeTracker;
import net.sourceforge.pmd.benchmark.TimedOperation;
import net.sourceforge.pmd.benchmark.TimedOperationCategory;
import net.sourceforge.pmd.cache.AnalysisCacheListener;
import net.sourceforge.pmd.cache.NoopAnalysisCache;
import net.sourceforge.pmd.cli.internal.ProgressBarListener;
import net.sourceforge.pmd.internal.LogMessages;
import net.sourceforge.pmd.internal.util.AssertionUtil;
import net.sourceforge.pmd.internal.util.FileCollectionUtil;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionDiscoverer;
import net.sourceforge.pmd.lang.document.FileCollector;
import net.sourceforge.pmd.lang.document.TextFile;
import net.sourceforge.pmd.processor.AbstractPMDProcessor;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.reporting.GlobalAnalysisListener;
import net.sourceforge.pmd.reporting.ReportStats;
import net.sourceforge.pmd.reporting.ReportStatsListener;
import net.sourceforge.pmd.util.ClasspathClassLoader;
import net.sourceforge.pmd.util.IOUtil;
import net.sourceforge.pmd.util.StringUtil;
import net.sourceforge.pmd.util.log.MessageReporter;

/**
 * Main programmatic API of PMD. Create and configure a {@link PMDConfiguration},
 * then use {@link #create(PMDConfiguration)} to obtain an instance.
 * You can perform additional configuration on the instance, eg adding
 * files to process, or additional rulesets and renderers. Then, call
 * {@link #performAnalysis()}. Example:
 * <pre>{@code
 *   PMDConfiguration config = new PMDConfiguration();
 *   config.setDefaultLanguageVersion(LanguageRegistry.findLanguageByTerseName("java").getVersion("11"));
 *   config.setInputPaths("src/main/java");
 *   config.prependClasspath("target/classes");
 *   config.setMinimumPriority(RulePriority.HIGH);
 *   config.addRuleSet("rulesets/java/quickstart.xml");
 *   config.setReportFormat("xml");
 *   config.setReportFile("target/pmd-report.xml");
 *
 *   try (PmdAnalysis pmd = PmdAnalysis.create(config)) {
 *     // note: don't use `config` once a PmdAnalysis has been created.
 *     // optional: add more rulesets
 *     pmd.addRuleSet(pmd.newRuleSetLoader().loadFromResource("custom-ruleset.xml"));
 *     // optional: add more files
 *     pmd.files().addFile(Paths.get("src", "main", "more-java", "ExtraSource.java"));
 *     // optional: add more renderers
 *     pmd.addRenderer(renderer);
 *
 *     pmd.performAnalysis();
 *   }
 * }</pre>
 *
 */
public final class PmdAnalysis implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(PmdAnalysis.class);

    private final FileCollector collector;
    private final List<Renderer> renderers = new ArrayList<>();
    private final List<GlobalAnalysisListener> listeners = new ArrayList<>();
    private final List<RuleSet> ruleSets = new ArrayList<>();
    private final PMDConfiguration configuration;
    private final MessageReporter reporter;

    private boolean closed;

    /**
     * Constructs a new instance. The files paths (input files, filelist,
     * exclude list, etc) given in the configuration are collected into
     * the file collector ({@link #files()}), but more can be added
     * programmatically using the file collector.
     */
    private PmdAnalysis(PMDConfiguration config) {
        this.configuration = config;
        this.reporter = config.getReporter();
        this.collector = FileCollector.newCollector(
            config.getLanguageVersionDiscoverer(),
            reporter
        );
    }

    /**
     * Constructs a new instance from a configuration.
     *
     * <ul>
     * <li> The files paths (input files, filelist,
     * exclude list, etc) are explored and the files to analyse are
     * collected into the file collector ({@link #files()}).
     * More can be added programmatically using the file collector.
     * <li>The rulesets given in the configuration are loaded ({@link PMDConfiguration#getRuleSets()})
     * <li>A renderer corresponding to the parameters of the configuration
     * is created and added (but not started).
     * </ul>
     */
    public static PmdAnalysis create(PMDConfiguration config) {
        PmdAnalysis pmd = new PmdAnalysis(config);

        // note: do not filter files by language
        // they could be ignored later. The problem is if you call
        // addRuleSet later, then you could be enabling new languages
        // So the files should not be pruned in advance
        FileCollectionUtil.collectFiles(config, pmd.files());

        if (config.getReportFormat() != null) {
            Renderer renderer = config.createRenderer(true);
            pmd.addRenderer(renderer);
        }

        if (!config.getRuleSetPaths().isEmpty()) {
            final RuleSetLoader ruleSetLoader = pmd.newRuleSetLoader();
            final List<RuleSet> ruleSets = ruleSetLoader.loadRuleSetsWithoutException(config.getRuleSetPaths());
            pmd.addRuleSets(ruleSets);
        }
        return pmd;
    }

    // test only
    List<RuleSet> rulesets() {
        return ruleSets;
    }

    // test only
    List<Renderer> renderers() {
        return renderers;
    }


    /**
     * Returns the file collector for the analysed sources.
     */
    public FileCollector files() {
        return collector; // todo user can close collector programmatically
    }

    /**
     * Returns a new ruleset loader, which can be used to create new
     * rulesets (add them then with {@link #addRuleSet(RuleSet)}).
     *
     * <pre>{@code
     * try (PmdAnalysis pmd = create(config)) {
     *     pmd.addRuleSet(pmd.newRuleSetLoader().loadFromResource("custom-ruleset.xml"));
     * }
     * }</pre>
     */
    public RuleSetLoader newRuleSetLoader() {
        return RuleSetLoader.fromPmdConfig(configuration);
    }

    /**
     * Add a new renderer. The given renderer must not already be started,
     * it will be started by {@link #performAnalysis()}.
     *
     * @throws NullPointerException If the parameter is null
     */
    public void addRenderer(Renderer renderer) {
        AssertionUtil.requireParamNotNull("renderer", renderer);
        this.renderers.add(renderer);
    }

    /**
     * Add several renderers at once.
     *
     * @throws NullPointerException If the parameter is null, or any of its items is null.
     */
    public void addRenderers(Collection<Renderer> renderers) {
        renderers.forEach(this::addRenderer);
    }

    /**
     * Add a new listener. As per the contract of {@link GlobalAnalysisListener},
     * this object must be ready for interaction. However, nothing will
     * be done with the listener until {@link #performAnalysis()} is called.
     * The listener will be closed by {@link #performAnalysis()}, or
     * {@link #close()}, whichever happens first.
     *
     * @throws NullPointerException If the parameter is null
     */
    public void addListener(GlobalAnalysisListener listener) {
        AssertionUtil.requireParamNotNull("listener", listener);
        this.listeners.add(listener);
    }

    /**
     * Add several listeners at once.
     *
     * @throws NullPointerException If the parameter is null, or any of its items is null.
     * @see #addListener(GlobalAnalysisListener)
     */
    public void addListeners(Collection<? extends GlobalAnalysisListener> listeners) {
        listeners.forEach(this::addListener);
    }

    /**
     * Add a new ruleset.
     *
     * @throws NullPointerException If the parameter is null
     */
    public void addRuleSet(RuleSet ruleSet) {
        AssertionUtil.requireParamNotNull("rule set", ruleSet);
        this.ruleSets.add(ruleSet);
    }

    /**
     * Add several rulesets at once.
     *
     * @throws NullPointerException If the parameter is null, or any of its items is null.
     */
    public void addRuleSets(Collection<RuleSet> ruleSets) {
        ruleSets.forEach(this::addRuleSet);
    }

    /**
     * Returns an unmodifiable view of the ruleset list. That will be
     * processed.
     */
    public List<RuleSet> getRulesets() {
        return Collections.unmodifiableList(ruleSets);
    }


    /**
     * Run PMD with the current state of this instance. This will start
     * and finish the registered renderers, and close all
     * {@linkplain #addListener(GlobalAnalysisListener) registered listeners}.
     * All files collected in the {@linkplain #files() file collector} are
     * processed. This does not return a report, as the analysis results
     * are consumed by {@link GlobalAnalysisListener} instances (of which
     * Renderers are a special case). Note that this does
     * not throw, errors are instead accumulated into a {@link MessageReporter}.
     */
    public void performAnalysis() {
        performAnalysisImpl(Collections.emptyList());
    }

    /**
     * Run PMD with the current state of this instance. This will start
     * and finish the registered renderers. All files collected in the
     * {@linkplain #files() file collector} are processed. Returns the
     * output report. Note that this does not throw, errors are instead
     * accumulated into a {@link MessageReporter}.
     */
    public Report performAnalysisAndCollectReport() {
        try (GlobalReportBuilderListener reportBuilder = new GlobalReportBuilderListener()) {
            performAnalysisImpl(listOf(reportBuilder)); // closes the report builder
            return reportBuilder.getResultImpl();
        }
    }

    void performAnalysisImpl(List<? extends GlobalReportBuilderListener> extraListeners) {
        try (FileCollector files = collector) {
            files.filterLanguages(getApplicableLanguages());
            performAnalysisImpl(extraListeners, files.getCollectedFiles());
        }
    }

    void performAnalysisImpl(List<? extends GlobalReportBuilderListener> extraListeners, List<TextFile> textFiles) {
        RuleSets rulesets = new RuleSets(this.ruleSets);

        GlobalAnalysisListener listener;
        try {
            @SuppressWarnings("PMD.CloseResource") AnalysisCacheListener cacheListener = new AnalysisCacheListener(configuration.getAnalysisCache(), rulesets, configuration.getClassLoader());
            if (configuration.isProgressBar()) {
                @SuppressWarnings("PMD.CloseResource") ProgressBarListener progressBarListener = new ProgressBarListener(textFiles.size(), System.out::print);
                addListener(progressBarListener);
            }
            listener = GlobalAnalysisListener.tee(listOf(createComposedRendererListener(renderers),
                                                         GlobalAnalysisListener.tee(listeners),
                                                         GlobalAnalysisListener.tee(extraListeners),
                                                         cacheListener));
        } catch (Exception e) {
            reporter.errorEx("Exception while initializing analysis listeners", e);
            throw new RuntimeException("Exception while initializing analysis listeners", e);
        }

        try (TimedOperation ignored = TimeTracker.startOperation(TimedOperationCategory.FILE_PROCESSING)) {

            for (final Rule rule : removeBrokenRules(rulesets)) {
                // todo Just like we throw for invalid properties, "broken rules"
                // shouldn't be a "config error". This is the only instance of
                // config errors...
                listener.onConfigError(new Report.ConfigurationError(rule, rule.dysfunctionReason()));
            }

            encourageToUseIncrementalAnalysis(configuration);
            try (AbstractPMDProcessor processor = AbstractPMDProcessor.newFileProcessor(configuration)) {
                processor.processFiles(rulesets, textFiles, listener);
            }
        } finally {
            try {
                listener.close();
            } catch (Exception e) {
                reporter.errorEx("Exception while initializing analysis listeners", e);
                // todo better exception
                throw new RuntimeException("Exception while initializing analysis listeners", e);
            }
        }
    }


    private static GlobalAnalysisListener createComposedRendererListener(List<Renderer> renderers) throws Exception {
        if (renderers.isEmpty()) {
            return GlobalAnalysisListener.noop();
        }

        List<GlobalAnalysisListener> rendererListeners = new ArrayList<>(renderers.size());
        for (Renderer renderer : renderers) {
            try {
                @SuppressWarnings("PMD.CloseResource")
                GlobalAnalysisListener listener =
                    Objects.requireNonNull(renderer.newListener(), "Renderer should provide non-null listener");
                rendererListeners.add(listener);
            } catch (Exception ioe) {
                // close listeners so far, throw their close exception or the ioe
                IOUtil.ensureClosed(rendererListeners, ioe);
                throw AssertionUtil.shouldNotReachHere("ensureClosed should have thrown");
            }
        }
        return GlobalAnalysisListener.tee(rendererListeners);
    }

    private Set<Language> getApplicableLanguages() {
        final Set<Language> languages = new HashSet<>();
        final LanguageVersionDiscoverer discoverer = configuration.getLanguageVersionDiscoverer();

        for (RuleSet ruleSet : ruleSets) {
            for (final Rule rule : ruleSet.getRules()) {
                final Language ruleLanguage = rule.getLanguage();
                Objects.requireNonNull(ruleLanguage, "Rule has no language " + rule);
                if (!languages.contains(ruleLanguage)) {
                    final LanguageVersion version = discoverer.getDefaultLanguageVersion(ruleLanguage);
                    if (RuleSet.applies(rule, version)) {
                        languages.add(ruleLanguage);
                        LOG.trace("Using {} version ''{}''", version.getLanguage().getName(), version.getTerseName());
                    }
                }
            }
        }
        return languages;
    }

    /**
     * Remove and return the misconfigured rules from the rulesets and log them
     * for good measure.
     */
    private Set<Rule> removeBrokenRules(final RuleSets ruleSets) {
        final Set<Rule> brokenRules = new HashSet<>();
        ruleSets.removeDysfunctionalRules(brokenRules);

        for (final Rule rule : brokenRules) {
            reporter.warn("Removed misconfigured rule: {0} cause: {1}",
                          rule.getName(), rule.dysfunctionReason());
        }

        return brokenRules;
    }


    public MessageReporter getReporter() {
        return reporter;
    }

    @Override
    public void close() {
        if (closed) {
            return;
        }
        closed = true;
        collector.close();

        // close listeners if analysis is not run.
        IOUtil.closeAll(listeners);

        /*
         * Make sure it's our own classloader before attempting to close it....
         * Maven + Jacoco provide us with a cloaseable classloader that if closed
         * will throw a ClassNotFoundException.
         */
        if (configuration.getClassLoader() instanceof ClasspathClassLoader) {
            IOUtil.tryCloseClassLoader(configuration.getClassLoader());
        }
    }

    public ReportStats runAndReturnStats() {
        if (getRulesets().isEmpty()) {
            return ReportStats.empty();
        }

        @SuppressWarnings("PMD.CloseResource")
        ReportStatsListener listener = new ReportStatsListener();

        addListener(listener);

        try {
            performAnalysis();
        } catch (Exception e) {
            getReporter().errorEx("Exception during processing", e);
            ReportStats stats = listener.getResult();
            printErrorDetected(1 + stats.getNumErrors());
            return stats; // should have been closed
        }
        ReportStats stats = listener.getResult();

        if (stats.getNumErrors() > 0) {
            printErrorDetected(stats.getNumErrors());
        }

        return stats;
    }

    static void printErrorDetected(MessageReporter reporter, int errors) {
        String msg = LogMessages.errorDetectedMessage(errors, "PMD");
        // note: using error level here increments the error count of the reporter,
        // which we don't want.
        reporter.info(StringUtil.quoteMessageFormat(msg));
    }

    void printErrorDetected(int errors) {
        printErrorDetected(getReporter(), errors);
    }

    private static void encourageToUseIncrementalAnalysis(final PMDConfiguration configuration) {
        final MessageReporter reporter = configuration.getReporter();

        if (!configuration.isIgnoreIncrementalAnalysis()
            && configuration.getAnalysisCache() instanceof NoopAnalysisCache
            && reporter.isLoggable(Level.WARN)) {
            final String version =
                PMDVersion.isUnknown() || PMDVersion.isSnapshot() ? "latest" : "pmd-" + PMDVersion.VERSION;
            reporter.warn("This analysis could be faster, please consider using Incremental Analysis: "
                            + "https://pmd.github.io/{0}/pmd_userdocs_incremental_analysis.html", version);
        }
    }
}
