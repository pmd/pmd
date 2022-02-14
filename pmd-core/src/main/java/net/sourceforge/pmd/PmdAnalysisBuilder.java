/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.benchmark.TimeTracker;
import net.sourceforge.pmd.benchmark.TimedOperation;
import net.sourceforge.pmd.benchmark.TimedOperationCategory;
import net.sourceforge.pmd.internal.util.FileCollectionUtil;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionDiscoverer;
import net.sourceforge.pmd.processor.AbstractPMDProcessor;
import net.sourceforge.pmd.processor.MonoThreadProcessor;
import net.sourceforge.pmd.processor.MultiThreadProcessor;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.util.ClasspathClassLoader;
import net.sourceforge.pmd.util.IOUtil;
import net.sourceforge.pmd.util.datasource.DataSource;
import net.sourceforge.pmd.util.document.FileCollector;
import net.sourceforge.pmd.util.log.PmdLogger;
import net.sourceforge.pmd.util.log.SimplePmdLogger;

/**
 * Main programmatic API of PMD. Create and configure a {@link PMDConfiguration},
 * then use {@link #create(PMDConfiguration)} to obtain an instance.
 * You can perform additional configuration on the instance, eg adding
 * files to process, or additional rulesets and renderers. Then, call
 * {@link #performAnalysis()}. Example:
 * <pre>{@code
 *   PMDConfiguration config = new PMDConfiguration();
 *   config.setDefaultLanguageVersion(LanguageRegistry.findLanguageVersionByTerseName("java 11"));
 *   config.setInputPaths("src/main/java");
 *   config.prependClasspath("target/classes");
 *   config.setMinimumPriority(RulePriority.HIGH);
 *   config.setRuleSets("rulesets/java/quickstart.xml");
 *   config.setReportFormat("xml");
 *
 *   try (PmdAnalysisBuilder pmd = PmdAnalysisBuilder.create(config)) {
 *     pmd.performAnalysis();
 *   }
 * }</pre>
 *
 */
public final class PmdAnalysisBuilder implements AutoCloseable {

    private final FileCollector collector;
    private final List<Renderer> renderers = new ArrayList<>();
    private final List<RuleSet> ruleSets = new ArrayList<>();
    private final PMDConfiguration configuration;
    private final SimplePmdLogger logger = new SimplePmdLogger(Logger.getLogger("net.sourceforge.pmd"));

    /**
     * Constructs a new instance. The files paths (input files, filelist,
     * exclude list, etc) given in the configuration are collected into
     * the file collector ({@link #files()}), but more can be added
     * programmatically using the file collector.
     */
    private PmdAnalysisBuilder(PMDConfiguration config) {
        this.configuration = config;
        this.collector = FileCollector.newCollector(
            config.getLanguageVersionDiscoverer(),
            logger
        );
        final Level logLevel = configuration.isDebug() ? Level.FINER : Level.INFO;
        this.logger.getBackend().setLevel(logLevel);
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
    public static PmdAnalysisBuilder create(PMDConfiguration config) {
        PmdAnalysisBuilder builder = new PmdAnalysisBuilder(config);

        // note: do not filter files by language
        // they could be ignored later. The problem is if you call
        // addRuleSet later, then you could be enabling new languages
        // So the files should not be pruned in advance
        FileCollectionUtil.collectFiles(config, builder.files());

        Renderer renderer = config.createRenderer();
        renderer.setReportFile(config.getReportFile());
        builder.addRenderer(renderer);

        final RuleSetLoader ruleSetLoader = RuleSetLoader.fromPmdConfig(config);
        final RuleSets ruleSets = RulesetsFactoryUtils.getRuleSetsWithBenchmark(config.getRuleSets(), ruleSetLoader.toFactory());
        if (ruleSets != null) {
            for (RuleSet ruleSet : ruleSets.getAllRuleSets()) {
                builder.addRuleSet(ruleSet);
            }
        }

        return builder;
    }

    @InternalApi
    static PmdAnalysisBuilder createWithoutCollectingFiles(PMDConfiguration config) {
        return new PmdAnalysisBuilder(config);
    }

    /**
     * Returns the file collector for the analysed sources.
     */
    public FileCollector files() {
        return collector; // todo user can close collector programmatically
    }

    /**
     * Add a new renderer. The given renderer must already be started.
     *
     * @throws NullPointerException If the parameter is null
     */
    public void addRenderer(Renderer renderer) {
        this.renderers.add(Objects.requireNonNull(renderer));
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
     * {@linkplain #files() file collector} are processed. Returns the
     * output report.
     */
    public Report performAnalysis() {
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
                    logger.errorEx("Error while starting renderer " + renderer.getName(), e);
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
                    logger.errorEx("Error while finishing renderer " + renderer.getName(), e);
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
                        logger.trace("Using {0} version ''{1}''", version.getLanguage().getName(), version.getTerseName());
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
