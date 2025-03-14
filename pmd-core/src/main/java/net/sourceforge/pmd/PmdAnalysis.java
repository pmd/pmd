/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static net.sourceforge.pmd.lang.document.InternalApiBridge.newCollector;
import static net.sourceforge.pmd.lang.rule.InternalApiBridge.loadRuleSetsWithoutException;
import static net.sourceforge.pmd.lang.rule.InternalApiBridge.ruleSetApplies;
import static net.sourceforge.pmd.util.CollectionUtil.listOf;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import net.sourceforge.pmd.benchmark.TimeTracker;
import net.sourceforge.pmd.benchmark.TimedOperation;
import net.sourceforge.pmd.benchmark.TimedOperationCategory;
import net.sourceforge.pmd.cache.internal.AnalysisCacheListener;
import net.sourceforge.pmd.cache.internal.NoopAnalysisCache;
import net.sourceforge.pmd.internal.LogMessages;
import net.sourceforge.pmd.internal.util.ClasspathClassLoader;
import net.sourceforge.pmd.internal.util.FileCollectionUtil;
import net.sourceforge.pmd.internal.util.IOUtil;
import net.sourceforge.pmd.lang.InternalApiBridge;
import net.sourceforge.pmd.lang.JvmLanguagePropertyBundle;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageProcessor.AnalysisTask;
import net.sourceforge.pmd.lang.LanguageProcessorRegistry;
import net.sourceforge.pmd.lang.LanguageProcessorRegistry.LanguageTerminationException;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionDiscoverer;
import net.sourceforge.pmd.lang.document.FileCollector;
import net.sourceforge.pmd.lang.document.TextFile;
import net.sourceforge.pmd.lang.rule.Rule;
import net.sourceforge.pmd.lang.rule.RuleSet;
import net.sourceforge.pmd.lang.rule.RuleSetLoader;
import net.sourceforge.pmd.lang.rule.internal.RuleSets;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.reporting.ConfigurableFileNameRenderer;
import net.sourceforge.pmd.reporting.FileAnalysisListener;
import net.sourceforge.pmd.reporting.GlobalAnalysisListener;
import net.sourceforge.pmd.reporting.ListenerInitializer;
import net.sourceforge.pmd.reporting.Report;
import net.sourceforge.pmd.reporting.Report.GlobalReportBuilderListener;
import net.sourceforge.pmd.reporting.ReportStats;
import net.sourceforge.pmd.reporting.ReportStatsListener;
import net.sourceforge.pmd.util.AssertionUtil;
import net.sourceforge.pmd.util.StringUtil;
import net.sourceforge.pmd.util.log.PmdReporter;

/**
 * Main programmatic API of PMD. This is not a CLI entry point, see module
 * {@code pmd-cli} for that.
 *
 * <h2>Usage overview</h2>
 *
 * <p>Create and configure a {@link PMDConfiguration},
 * then use {@link #create(PMDConfiguration)} to obtain an instance.
 * You can perform additional configuration on the instance, e.g. adding
 * files to process, or additional rulesets and renderers. Then, call
 * {@link #performAnalysis()} or one of the related terminal methods.
 *
 * <h2>Simple example</h2>
 *
 * <pre>{@code
 *   PMDConfiguration config = new PMDConfiguration();
 *   config.setDefaultLanguageVersion(LanguageRegistry.findLanguageByTerseName("java").getVersion("11"));
 *   config.addInputPath(Path.of("src/main/java"));
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
 * <h2>Rendering reports</h2>
 *
 * <p>If you just want to render a report to a file like with the CLI, you
 * should use a {@link Renderer}. You can add a custom one with {@link PmdAnalysis#addRenderer(Renderer)}.
 * You can add one of the builtin renderers from its ID using {@link PMDConfiguration#setReportFormat(String)}.
 *
 * <h2>Reports and events</h2>
 *
 * <p>If you want strongly typed access to violations and other analysis events,
 * you can implement and register a {@link GlobalAnalysisListener} with {@link #addListener(GlobalAnalysisListener)}.
 * The listener needs to provide a new {@link FileAnalysisListener} for each file,
 * which will receive events from the analysis. The listener's lifecycle
 * happens only once the analysis is started ({@link #performAnalysis()}).
 *
 * <p>If you want access to all events once the analysis ends instead of processing
 * events as they go, you can obtain a {@link Report} instance from {@link #performAnalysisAndCollectReport()},
 * or use {@link Report.GlobalReportBuilderListener} manually. Keep in
 * mind collecting a report is less memory-efficient than using a listener.
 *
 * <p>If you want to process events in batches, one per file, you can
 * use {@link Report.ReportBuilderListener}. to implement {@link GlobalAnalysisListener#startFileAnalysis(TextFile)}.
 *
 * <p>Listeners can be used alongside renderers.
 *
 * <h2>Specifying the Java classpath</h2>
 *
 * <p>Java rules work better if you specify the path to the compiled classes
 * of the analysed sources. See {@link PMDConfiguration#prependAuxClasspath(String)}.
 *
 * <h2>Customizing message output</h2>
 *
 * <p>The analysis reports messages like meta warnings and errors through a
 * {@link PmdReporter} instance. To override how those messages are output,
 * you can set it in {@link PMDConfiguration#setReporter(PmdReporter)}.
 * By default, it forwards messages to SLF4J.
 *
 */
public final class PmdAnalysis implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(PmdAnalysis.class);

    private final FileCollector collector;
    private final List<Renderer> renderers = new ArrayList<>();
    private final List<GlobalAnalysisListener> listeners = new ArrayList<>();
    private final List<RuleSet> ruleSets = new ArrayList<>();
    private final PMDConfiguration configuration;
    private final PmdReporter reporter;

    private final Map<Language, LanguagePropertyBundle> langProperties = new HashMap<>();
    private boolean closed;
    private final ConfigurableFileNameRenderer fileNameRenderer = new ConfigurableFileNameRenderer();

    /**
     * Constructs a new instance. The files paths (input files, filelist,
     * exclude list, etc) given in the configuration are collected into
     * the file collector ({@link #files()}), but more can be added
     * programmatically using the file collector.
     */
    private PmdAnalysis(PMDConfiguration config) {
        this.configuration = config;
        this.reporter = config.getReporter();
        this.collector = newCollector(
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
     * <li>The rulesets given in the configuration are loaded ({@link PMDConfiguration#getRuleSetPaths()})
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
            final List<RuleSet> ruleSets = loadRuleSetsWithoutException(ruleSetLoader, config.getRuleSetPaths());
            pmd.addRuleSets(ruleSets);
        }

        for (Language language : config.getLanguageRegistry()) {
            LanguagePropertyBundle props = config.getLanguageProperties(language);
            assert props.getLanguage().equals(language);
            pmd.langProperties.put(language, props);

            LanguageVersion forcedVersion = config.getForceLanguageVersion();
            if (forcedVersion != null && forcedVersion.getLanguage().equals(language)) {
                props.setLanguageVersion(forcedVersion.getVersion());
            }

            // TODO replace those with actual language properties when the
            //  CLI syntax is implemented. #2947
            props.setProperty(LanguagePropertyBundle.SUPPRESS_MARKER, config.getSuppressMarker());
            if (props instanceof JvmLanguagePropertyBundle) {
                ((JvmLanguagePropertyBundle) props).setClassLoader(config.getClassLoader());
            }
        }

        for (Path path : config.getRelativizeRoots()) {
            pmd.fileNameRenderer.relativizeWith(path);
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
     * Returns a mutable bundle of language properties that are associated
     * to the given language (always the same for a given language).
     *
     * @param language A language, which must be registered
     */
    public LanguagePropertyBundle getLanguageProperties(Language language) {
        configuration.checkLanguageIsRegistered(language);
        return langProperties.computeIfAbsent(language, Language::newPropertyBundle);
    }


    public ConfigurableFileNameRenderer fileNameRenderer() {
        return fileNameRenderer;
    }

    /**
     * Run PMD with the current state of this instance. This will start
     * and finish the registered renderers, and close all
     * {@linkplain #addListener(GlobalAnalysisListener) registered listeners}.
     * All files collected in the {@linkplain #files() file collector} are
     * processed. This does not return a report, as the analysis results
     * are consumed by {@link GlobalAnalysisListener} instances (of which
     * Renderers are a special case). Note that this does
     * not throw, errors are instead accumulated into a {@link PmdReporter}.
     */
    public void performAnalysis() {
        performAnalysisImpl(Collections.emptyList());
    }

    /**
     * Run PMD with the current state of this instance. This will start
     * and finish the registered renderers. All files collected in the
     * {@linkplain #files() file collector} are processed. Returns the
     * output report. Note that this does not throw, errors are instead
     * accumulated into a {@link PmdReporter}.
     */
    public Report performAnalysisAndCollectReport() {
        try (GlobalReportBuilderListener reportBuilder = new GlobalReportBuilderListener()) {
            performAnalysisImpl(listOf(reportBuilder)); // closes the report builder
            return reportBuilder.getResultImpl();
        }
    }

    void performAnalysisImpl(List<? extends GlobalReportBuilderListener> extraListeners) {
        try (FileCollector files = collector) {
            files.filterLanguages(getApplicableLanguages(false));
            performAnalysisImpl(extraListeners, files.getCollectedFiles());
        }
    }

    void performAnalysisImpl(List<? extends GlobalReportBuilderListener> extraListeners, List<TextFile> textFiles) {
        RuleSets rulesets = new RuleSets(this.ruleSets);

        GlobalAnalysisListener listener;
        try {
            @SuppressWarnings("PMD.CloseResource")
            AnalysisCacheListener cacheListener = new AnalysisCacheListener(configuration.getAnalysisCache(),
                                                                            rulesets,
                                                                            configuration.getClassLoader(),
                                                                            textFiles);
            listener = GlobalAnalysisListener.tee(listOf(createComposedRendererListener(renderers),
                                                         GlobalAnalysisListener.tee(listeners),
                                                         GlobalAnalysisListener.tee(extraListeners),
                                                         cacheListener));
            
            // Initialize listeners
            try (ListenerInitializer initializer = listener.initializer()) {
                initializer.setNumberOfFilesToAnalyze(textFiles.size());
                initializer.setFileNameRenderer(fileNameRenderer());
            }
        } catch (Exception e) {
            reporter.errorEx("Exception while initializing analysis listeners", e);
            throw new RuntimeException("Exception while initializing analysis listeners", e);
        }

        try (TimedOperation ignored = TimeTracker.startOperation(TimedOperationCategory.FILE_PROCESSING)) {
            for (final Rule rule : removeBrokenRules(rulesets)) {
                // todo Just like we throw for invalid properties, "broken rules"
                // shouldn't be a "config error". This is the only instance of
                // config errors...
                // see https://github.com/pmd/pmd/issues/3901
                listener.onConfigError(new Report.ConfigurationError(rule, rule.dysfunctionReason()));
            }

            encourageToUseIncrementalAnalysis(configuration);

            try (LanguageProcessorRegistry lpRegistry = LanguageProcessorRegistry.create(
                // only start the applicable languages (and dependencies)
                new LanguageRegistry(getApplicableLanguages(true)),
                langProperties,
                reporter
            )) {
                // Note the analysis task is shared: all processors see
                // the same file list, which may contain files for other
                // languages.
                AnalysisTask analysisTask = InternalApiBridge.createAnalysisTask(
                    rulesets,
                    textFiles,
                    listener,
                    configuration.getThreads(),
                    configuration.getAnalysisCache(),
                    reporter,
                    lpRegistry
                );

                List<AutoCloseable> analyses = new ArrayList<>();
                try {
                    for (Language lang : lpRegistry.getLanguages()) {
                        analyses.add(lpRegistry.getProcessor(lang).launchAnalysis(analysisTask));
                    }
                } finally {
                    Exception e = IOUtil.closeAll(analyses);
                    if (e != null) {
                        reporter.errorEx("Error while joining analysis", e);
                    }
                }

            } catch (LanguageTerminationException e) {
                reporter.errorEx("Error while closing language processors", e);
            }
        } finally {
            try {
                listener.close();
            } catch (Exception e) {
                reporter.errorEx("Exception while closing analysis listeners", e);
                // todo better exception
                throw new RuntimeException("Exception while closing analysis listeners", e);
            }
        }
    }


    private GlobalAnalysisListener createComposedRendererListener(List<Renderer> renderers) throws Exception {
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
                throw AssertionUtil.shouldNotReachHere("ensureClosed should have thrown", ioe);
            }
        }
        return GlobalAnalysisListener.tee(rendererListeners);
    }

    private Set<Language> getApplicableLanguages(boolean quiet) {
        Set<Language> languages = new HashSet<>();
        LanguageVersionDiscoverer discoverer = configuration.getLanguageVersionDiscoverer();

        for (RuleSet ruleSet : ruleSets) {
            for (Rule rule : ruleSet.getRules()) {
                Language ruleLanguage = rule.getLanguage();
                Objects.requireNonNull(ruleLanguage, "Rule has no language " + rule);
                if (!languages.contains(ruleLanguage)) {
                    LanguageVersion version = discoverer.getDefaultLanguageVersion(ruleLanguage);
                    if (ruleSetApplies(rule, version)) {
                        configuration.checkLanguageIsRegistered(ruleLanguage);
                        languages.add(ruleLanguage);
                        if (!quiet) {
                            LOG.trace("Using {} version ''{}''", version.getLanguage().getName(), version.getTerseName());
                        }
                    }
                }
            }
        }

        // collect all dependencies, they shouldn't be filtered out
        LanguageRegistry reg = configuration.getLanguageRegistry();
        boolean changed;
        do {
            changed = false;
            for (Language lang : new HashSet<>(languages)) {
                for (String depId : lang.getDependencies()) {
                    Language depLang = reg.getLanguageById(depId);
                    if (depLang == null) {
                        // todo maybe report all then throw
                        throw new IllegalStateException(
                            "Language " + lang.getId() + " has unsatisfied dependencies: "
                                + depId + " is not found in " + reg
                        );
                    }
                    changed |= languages.add(depLang);
                }
            }
        } while (changed);
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


    public PmdReporter getReporter() {
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

    static void printErrorDetected(PmdReporter reporter, int errors) {
        String msg = LogMessages.errorDetectedMessage(errors, "PMD");
        // note: using error level here increments the error count of the reporter,
        // which we don't want.
        reporter.info(StringUtil.quoteMessageFormat(msg));
    }

    void printErrorDetected(int errors) {
        printErrorDetected(getReporter(), errors);
    }

    private static void encourageToUseIncrementalAnalysis(final PMDConfiguration configuration) {
        final PmdReporter reporter = configuration.getReporter();

        if (!configuration.isIgnoreIncrementalAnalysis()
            && configuration.getAnalysisCache() instanceof NoopAnalysisCache
            && reporter.isLoggable(Level.WARN)) {
            final String version =
                PMDVersion.isUnknown() || PMDVersion.isSnapshot() ? "latest" : "pmd-doc-" + PMDVersion.VERSION;
            reporter.warn("This analysis could be faster, please consider using Incremental Analysis: "
                            + "https://docs.pmd-code.org/{0}/pmd_userdocs_incremental_analysis.html", version);
        }
    }

}
