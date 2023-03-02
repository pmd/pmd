/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.benchmark.TimeTracker;
import net.sourceforge.pmd.benchmark.TimedOperation;
import net.sourceforge.pmd.benchmark.TimedOperationCategory;
import net.sourceforge.pmd.internal.util.FileCollectionUtil;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionDiscoverer;
import net.sourceforge.pmd.lang.document.FileCollector;
import net.sourceforge.pmd.processor.AbstractPMDProcessor;
import net.sourceforge.pmd.processor.MonoThreadProcessor;
import net.sourceforge.pmd.processor.MultiThreadProcessor;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.util.ClasspathClassLoader;
import net.sourceforge.pmd.util.IOUtil;
import net.sourceforge.pmd.util.datasource.DataSource;
import net.sourceforge.pmd.util.log.MessageReporter;
import net.sourceforge.pmd.util.log.MessageReporter.Level;
import net.sourceforge.pmd.util.log.internal.SimpleMessageReporter;

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

    private final FileCollector collector;
    private final List<Renderer> renderers = new ArrayList<>();
    private final List<RuleSet> ruleSets = new ArrayList<>();
    private final PMDConfiguration configuration;
    private final SimpleMessageReporter reporter = new SimpleMessageReporter(Logger.getLogger("net.sourceforge.pmd"));

    private boolean closed;

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
            reporter
        );
        for (Path path : config.getRelativizeRoots()) {
            this.collector.relativizeWith(path);
        }
        final Level logLevel = configuration.isDebug() ? Level.TRACE : Level.INFO;
        this.reporter.setLevel(logLevel);

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

    @InternalApi
    static PmdAnalysis createWithoutCollectingFiles(PMDConfiguration config) {
        return new PmdAnalysis(config);
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
        RuleSetLoader loader = RuleSetLoader.fromPmdConfig(configuration);
        loader.setReporter(this.reporter);
        return loader;
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
     * Add several renderers at once.
     *
     * @throws NullPointerException If the parameter is null, or any of its items is null.
     */
    public void addRenderers(Collection<Renderer> renderers) {
        for (Renderer r : renderers) {
            addRenderer(r);
        }
    }

    /**
     * Add a new ruleset.
     *
     * @throws NullPointerException If the parameter is null
     */
    public void addRuleSet(RuleSet ruleSet) {
        this.ruleSets.add(Objects.requireNonNull(ruleSet));
    }

    /**
     * Add several rulesets at once.
     *
     * @throws NullPointerException If the parameter is null, or any of its items is null.
     */
    public void addRuleSets(Collection<RuleSet> ruleSets) {
        for (RuleSet rs : ruleSets) {
            addRuleSet(rs);
        }
    }

    public List<RuleSet> getRulesets() {
        return Collections.unmodifiableList(ruleSets);
    }


    /**
     * Run PMD with the current state of this instance. This will start
     * and finish the registered renderers. All files collected in the
     * {@linkplain #files() file collector} are processed. This does not
     * return a report, for compatibility with PMD 7. Note that this does
     * not throw, errors are instead accumulated into a {@link MessageReporter}.
     */
    public void performAnalysis() {
        performAnalysisAndCollectReport();
    }

    /**
     * Run PMD with the current state of this instance. This will start
     * and finish the registered renderers. All files collected in the
     * {@linkplain #files() file collector} are processed. Returns the
     * output report. Note that this does not throw, errors are instead
     * accumulated into a {@link MessageReporter}.
     */
    // TODO PMD 7 @DeprecatedUntil700
    public Report performAnalysisAndCollectReport() {
        try (FileCollector files = collector) {
            files.filterLanguages(getApplicableLanguages());
            List<DataSource> dataSources = FileCollectionUtil.collectorToDataSource(files);
            startRenderers();
            Report report = performAnalysisImpl(dataSources);
            finishRenderers();
            return report;
        }
    }


    Report performAnalysisImpl(List<DataSource> sortedFiles) {
        try (TimedOperation ignored = TimeTracker.startOperation(TimedOperationCategory.FILE_PROCESSING)) {
            PMD.encourageToUseIncrementalAnalysis(configuration);
            Report report = new Report();
            report.addListener(configuration.getAnalysisCache());

            RuleContext ctx = new RuleContext();
            ctx.setReport(report);
            newFileProcessor(configuration).processFiles(new RuleSets(ruleSets), sortedFiles, ctx, renderers);
            configuration.getAnalysisCache().persist();
            return report;
        }
    }

    private void startRenderers() {
        try (TimedOperation ignored = TimeTracker.startOperation(TimedOperationCategory.REPORTING)) {
            for (Renderer renderer : renderers) {
                try {
                    renderer.start();
                } catch (IOException e) {
                    reporter.errorEx("Error while starting renderer " + renderer.getName(), e);
                }
            }
        }
    }

    private void finishRenderers() {
        try (TimedOperation ignored = TimeTracker.startOperation(TimedOperationCategory.REPORTING)) {
            for (Renderer renderer : renderers) {
                try {
                    renderer.end();
                    renderer.flush();
                } catch (IOException e) {
                    reporter.errorEx("Error while finishing renderer " + renderer.getName(), e);
                }
            }
        }
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
                        reporter.trace("Using {0} version ''{1}''", version.getLanguage().getName(), version.getTerseName());
                    }
                }
            }
        }
        return languages;
    }


    private static AbstractPMDProcessor newFileProcessor(final PMDConfiguration configuration) {
        return configuration.getThreads() > 0 ? new MultiThreadProcessor(configuration)
                                              : new MonoThreadProcessor(configuration);
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
