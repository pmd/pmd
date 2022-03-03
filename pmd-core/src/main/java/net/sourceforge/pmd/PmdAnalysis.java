/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;

import net.sourceforge.pmd.Report.GlobalReportBuilderListener;
import net.sourceforge.pmd.benchmark.TimeTracker;
import net.sourceforge.pmd.benchmark.TimedOperation;
import net.sourceforge.pmd.benchmark.TimedOperationCategory;
import net.sourceforge.pmd.cache.AnalysisCacheListener;
import net.sourceforge.pmd.internal.util.AssertionUtil;
import net.sourceforge.pmd.internal.util.FileCollectionUtil;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionDiscoverer;
import net.sourceforge.pmd.lang.document.FileCollector;
import net.sourceforge.pmd.processor.AbstractPMDProcessor;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.reporting.GlobalAnalysisListener;
import net.sourceforge.pmd.util.ClasspathClassLoader;
import net.sourceforge.pmd.util.IOUtil;
import net.sourceforge.pmd.util.datasource.DataSource;
import net.sourceforge.pmd.util.log.PmdLogger;
import net.sourceforge.pmd.util.log.PmdLogger.Level;
import net.sourceforge.pmd.util.log.SimplePmdLogger;

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
 *   config.setRuleSets("rulesets/java/quickstart.xml");
 *   config.setReportFormat("xml");
 *   config.setReportFile("target/pmd-report.xml");
 *
 *   try (PmdAnalysis pmd = PmdAnalysis.create(config)) {
 *     // optional: add more rulesets
 *     pmd.addRuleSet(RuleSetLoader.fromPmdConfig(configuration).loadFromResource("custom-ruleset.xml"));
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

    private final FileCollector collector;
    private final List<Renderer> renderers = new ArrayList<>();
    private final List<GlobalAnalysisListener> listeners = new ArrayList<>();
    private final List<RuleSet> ruleSets = new ArrayList<>();
    private final PMDConfiguration configuration;
    private final SimplePmdLogger logger = new SimplePmdLogger(Logger.getLogger("net.sourceforge.pmd"));

    /**
     * Constructs a new instance. The files paths (input files, filelist,
     * exclude list, etc) given in the configuration are collected into
     * the file collector ({@link #files()}), but more can be added
     * programmatically using the file collector.
     */
    private PmdAnalysis(PMDConfiguration config) {
        this.configuration = config;
        this.collector = FileCollector.newCollector(
            config.getLanguageVersionDiscoverer(),
            logger
        );
        final Level logLevel = configuration.isDebug() ? Level.TRACE : Level.INFO;
        this.logger.setLevel(logLevel);
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
        PmdAnalysis builder = new PmdAnalysis(config);

        // note: do not filter files by language
        // they could be ignored later. The problem is if you call
        // addRuleSet later, then you could be enabling new languages
        // So the files should not be pruned in advance
        FileCollectionUtil.collectFiles(config, builder.files());

        if (config.getReportFormat() != null) {
            Renderer renderer = config.createRenderer();
            renderer.setReportFile(config.getReportFile());
            builder.addRenderer(renderer);
        }

        final RuleSetLoader ruleSetLoader = RuleSetLoader.fromPmdConfig(config);
        final List<RuleSet> ruleSets = PMD.getRuleSetsWithBenchmark(config.getRuleSetPaths(), ruleSetLoader);
        for (RuleSet ruleSet : ruleSets) {
            builder.addRuleSet(ruleSet);
        }

        return builder;
    }

    /**
     * Returns the file collector for the analysed sources.
     */
    public FileCollector files() {
        return collector; // todo user can close collector programmatically
    }

    /**
     * Add a new renderer. The given renderer must not already be started,
     * it will be started by {@link #performAnalysis()}.
     *
     * @throws NullPointerException If the parameter is null
     */
    public void addRenderer(Renderer renderer) {
        this.renderers.add(Objects.requireNonNull(renderer));
    }

    /**
     * Add a new listener. The given renderer must not already be closed,
     * it will be closed by {@link #performAnalysis()}.
     *
     * @throws NullPointerException If the parameter is null
     */
    public void addListener(GlobalAnalysisListener listener) {
        this.listeners.add(Objects.requireNonNull(listener));
    }

    /**
     * Add a new ruleset.
     *
     * @throws NullPointerException If the parameter is null
     */
    public void addRuleSet(RuleSet ruleSet) {
        this.ruleSets.add(Objects.requireNonNull(ruleSet));
    }

    public List<RuleSet> getRulesets() {
        return Collections.unmodifiableList(ruleSets);
    }


    /**
     * Run PMD with the current state of this instance. This will start
     * and finish the registered renderers. All files collected in the
     * {@linkplain #files() file collector} are processed. This does not
     * return a report, for compatibility with PMD 7.
     */
    public void performAnalysis() {
        performAnalysisImpl(Collections.emptyList());
    }

    /**
     * Run PMD with the current state of this instance. This will start
     * and finish the registered renderers. All files collected in the
     * {@linkplain #files() file collector} are processed. Returns the
     * output report.
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
            List<DataSource> dataSources = FileCollectionUtil.collectorToDataSource(files);
            RuleSets rulesets = new RuleSets(this.ruleSets);

            GlobalAnalysisListener listener;
            try {
                AnalysisCacheListener cacheListener = new AnalysisCacheListener(
                    configuration.getAnalysisCache(),
                    rulesets,
                    configuration.getClassLoader()
                );
                listener = GlobalAnalysisListener.tee(listOf(
                    createComposedRendererListener(renderers),
                    GlobalAnalysisListener.tee(listeners),
                    GlobalAnalysisListener.tee(extraListeners),
                    cacheListener
                ));
            } catch (Exception e) {
                logger.errorEx("Exception while initializing analysis listeners", e);
                throw new RuntimeException("Exception while initializing analysis listeners", e);
            }

            try (TimedOperation ignored = TimeTracker.startOperation(TimedOperationCategory.FILE_PROCESSING)) {
                // todo Just like we throw for invalid properties, "broken rules"
                // shouldn't be a "config error". This is the only instance of
                // config errors...

                if (isEmpty(this.ruleSets)) {
                    if (!configuration.getRuleSetPaths().isEmpty()) {
                        logger.error("No rules found. Maybe you misspelled a rule name? ({0})",
                                     String.join(",", configuration.getRuleSetPaths()));

                    } else {
                        logger.error("No rules found.");
                    }
                    return;
                }

                for (final Rule rule : removeBrokenRules(rulesets)) {
                    listener.onConfigError(new Report.ConfigurationError(rule, rule.dysfunctionReason()));
                }

                PMD.encourageToUseIncrementalAnalysis(configuration);
                try (AbstractPMDProcessor processor = AbstractPMDProcessor.newFileProcessor(configuration)) {
                    processor.processFiles(rulesets, dataSources, listener);
                }
            } finally {
                try {
                    listener.close();
                } catch (Exception e) {
                    logger.errorEx("Exception while initializing analysis listeners", e);
                    throw new RuntimeException("Exception while initializing analysis listeners", e);
                }
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
                rendererListeners.add(renderer.newListener());
            } catch (IOException ioe) {
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
                if (!languages.contains(ruleLanguage)) {
                    final LanguageVersion version = discoverer.getDefaultLanguageVersion(ruleLanguage);
                    if (RuleSet.applies(rule, version)) {
                        languages.add(ruleLanguage);
                        logger.trace("Using {0} version ''{1}''", version.getLanguage().getName(), version.getTerseName());
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
            logger.warning("Removed misconfigured rule: {} cause: {}",
                           rule.getName(), rule.dysfunctionReason());
        }

        return brokenRules;
    }

    private static boolean isEmpty(List<RuleSet> rsets) {
        return rsets.stream().noneMatch(it -> it.size() > 0);
    }


    public PmdLogger getLog() {
        return logger;
    }

    @Override
    public void close() {
        collector.close();

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
