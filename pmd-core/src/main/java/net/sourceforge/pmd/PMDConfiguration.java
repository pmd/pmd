/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.cache.internal.AnalysisCache;
import net.sourceforge.pmd.cache.internal.FileAnalysisCache;
import net.sourceforge.pmd.cache.internal.NoopAnalysisCache;
import net.sourceforge.pmd.internal.util.ClasspathClassLoader;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.PmdCapableLanguage;
import net.sourceforge.pmd.lang.rule.RulePriority;
import net.sourceforge.pmd.lang.rule.RuleSetLoader;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.renderers.RendererFactory;
import net.sourceforge.pmd.util.AssertionUtil;
import net.sourceforge.pmd.util.log.internal.SimpleMessageReporter;

/**
 * This class contains the details for the runtime configuration of a
 * PMD run. Once configured, use {@link PmdAnalysis#create(PMDConfiguration)}
 * in a try-with-resources to execute the analysis (see {@link PmdAnalysis}).
 *
 * <h2>Rulesets</h2>
 *
 * <ul>
 * <li>You can configure paths to the rulesets to use with {@link #addRuleSet(String)}.
 * These can be file paths or classpath resources.</li>
 * <li>Use {@link #setMinimumPriority(RulePriority)} to control the minimum priority a
 * rule must have to be included. Defaults to the lowest priority, ie all rules are loaded.</li>
 * </ul>
 *
 * <h2>Source files</h2>
 *
 * <ul>
 * <li>The default encoding of source files is the system default as
 * returned by <code>System.getProperty("file.encoding")</code>.
 * You can set it with {@link #setSourceEncoding(Charset)}.</li>
 * <li>The source files to analyze can be given in many ways. See
 * {@link #addInputPath(Path)} {@link #setInputFilePath(Path)}, {@link #setInputUri(URI)}.
 * <li>Files are assigned a language based on their name. The language
 * version of languages can be given with
 * {@link #setDefaultLanguageVersion(LanguageVersion)}.
 * The default language assignment can be overridden with
 * {@link #setForceLanguageVersion(LanguageVersion)}.</li>
 * </ul>
 *
 * <h2>Rendering</h2>
 *
 * <ul>
 * <li>The renderer format to use for Reports. {@link #getReportFormat()}</li>
 * <li>The file to which the Report should render. {@link #getReportFilePath()}</li>
 * <li>Configure the root paths that are used to relativize file names in reports via {@link #addRelativizeRoot(Path)}.
 * This enables to get short names in reports.</li>
 * <li>The initialization properties to use when creating a Renderer instance.
 * {@link #getReportProperties()}</li>
 * <li>An indicator of whether to show suppressed Rule violations in Reports.
 * {@link #isShowSuppressedViolations()}</li>
 * </ul>
 *
 * <h2>Language configuration</h2>
 * <ul>
 * <li>Use {@link #setSuppressMarker(String)} to change the comment marker for suppression comments. Defaults to {@value #DEFAULT_SUPPRESS_MARKER}.</li>
 * <li>See {@link #setClassLoader(ClassLoader)} and {@link #prependAuxClasspath(String)} for
 *  information for how to configure classpath for Java analysis.</li>
 * <li>You can set additional language properties with {@link #getLanguageProperties(Language)}</li>
 * </ul>
 *
 * <h2>Miscellaneous</h2>
 * <ul>
 * <li>Use {@link #setThreads(int)} to control the parallelism of the analysis. Defaults
 * one thread per available processor. {@link #getThreads()}</li>
 * </ul>
 */
public class PMDConfiguration extends AbstractConfiguration {
    private static final LanguageRegistry DEFAULT_REGISTRY = LanguageRegistry.PMD;

    /** The default suppress marker string. */
    public static final String DEFAULT_SUPPRESS_MARKER = "NOPMD";
    private Path reportFile;

    // General behavior options
    private String suppressMarker = DEFAULT_SUPPRESS_MARKER;
    private int threads = Runtime.getRuntime().availableProcessors();
    private ClassLoader classLoader = getClass().getClassLoader();

    // Rule and source file options
    private List<String> ruleSets = new ArrayList<>();
    private RulePriority minimumPriority = RulePriority.LOW;

    // Reporting options
    private String reportFormat;
    private Properties reportProperties = new Properties();
    private boolean showSuppressedViolations = false;

    private AnalysisCache analysisCache = new NoopAnalysisCache();
    private boolean ignoreIncrementalAnalysis;

    public PMDConfiguration() {
        this(DEFAULT_REGISTRY);
    }

    public PMDConfiguration(@NonNull LanguageRegistry languageRegistry) {
        super(languageRegistry, new SimpleMessageReporter(LoggerFactory.getLogger(PmdAnalysis.class)));
    }

    /**
     * Get the suppress marker. This is the source level marker used to indicate
     * a RuleViolation should be suppressed.
     *
     * @return The suppress marker.
     */
    public String getSuppressMarker() {
        return suppressMarker;
    }

    /**
     * Set the suppress marker.
     *
     * @param suppressMarker
     *            The suppress marker to use.
     */
    public void setSuppressMarker(String suppressMarker) {
        Objects.requireNonNull(suppressMarker, "Suppress marker was null");
        this.suppressMarker = suppressMarker;
    }

    /**
     * Get the number of threads to use when processing Rules.
     *
     * @return The number of threads.
     */
    public int getThreads() {
        return threads;
    }

    /**
     * Set the number of threads to use when processing Rules.
     *
     * @param threads
     *            The number of threads.
     */
    public void setThreads(int threads) {
        this.threads = threads;
    }

    /**
     * Get the ClassLoader being used by PMD when processing Rules.
     *
     * @return The ClassLoader being used
     */
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    /**
     * Set the ClassLoader being used by PMD when processing Rules. Setting a
     * value of <code>null</code> will cause the default ClassLoader to be used.
     *
     * @param classLoader
     *            The ClassLoader to use
     */
    public void setClassLoader(ClassLoader classLoader) {
        if (classLoader == null) {
            this.classLoader = getClass().getClassLoader();
        } else {
            this.classLoader = classLoader;
        }
    }

    /**
     * Prepend the specified classpath like string to the current ClassLoader of
     * the configuration. If no ClassLoader is currently configured, the
     * ClassLoader used to load the {@link PMDConfiguration} class will be used
     * as the parent ClassLoader of the created ClassLoader.
     *
     * <p>If the classpath String looks like a URL to a file (i.e. starts with
     * <code>file://</code>) the file will be read with each line representing
     * an entry on the classpath.</p>
     *
     * <p>You can specify multiple class paths separated by `:` on Unix-systems or `;` under Windows.
     * See {@link File#pathSeparator}.
     *
     * @param classpath The prepended classpath.
     *
     * @throws IllegalArgumentException if the given classpath is invalid (e.g. does not exist)
     * @see PMDConfiguration#setClassLoader(ClassLoader)
     */
    public void prependAuxClasspath(String classpath) {
        try {
            if (classLoader == null) {
                classLoader = PMDConfiguration.class.getClassLoader();
            }
            if (classpath != null) {
                classLoader = new ClasspathClassLoader(classpath, classLoader);
            }
        } catch (IOException e) {
            // Note: IOExceptions shouldn't appear anymore, they should already be converted
            // to IllegalArgumentException in ClasspathClassLoader.
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Returns the list of ruleset URIs.
     *
     * @see RuleSetLoader#loadFromResource(String)
     */
    public @NonNull List<@NonNull String> getRuleSetPaths() {
        return ruleSets;
    }

    /**
     * Sets the list of ruleset paths to load when starting the analysis.
     *
     * @param ruleSetPaths A list of ruleset paths, understandable by {@link RuleSetLoader#loadFromResource(String)}.
     *
     * @throws NullPointerException If the parameter is null
     */
    public void setRuleSets(@NonNull List<@NonNull String> ruleSetPaths) {
        AssertionUtil.requireParamNotNull("ruleSetPaths", ruleSetPaths);
        AssertionUtil.requireContainsNoNullValue("ruleSetPaths", ruleSetPaths);
        this.ruleSets = new ArrayList<>(ruleSetPaths);
    }

    /**
     * Add a new ruleset paths to load when starting the analysis.
     * This list is initially empty.
     *
     * @param rulesetPath A ruleset path, understandable by {@link RuleSetLoader#loadFromResource(String)}.
     *
     * @throws NullPointerException If the parameter is null
     */
    public void addRuleSet(@NonNull String rulesetPath) {
        AssertionUtil.requireParamNotNull("rulesetPath", rulesetPath);
        this.ruleSets.add(rulesetPath);
    }

    /**
     * Get the minimum priority threshold when loading Rules from RuleSets.
     *
     * @return The minimum priority threshold.
     */
    public RulePriority getMinimumPriority() {
        return minimumPriority;
    }

    /**
     * Set the minimum priority threshold when loading Rules from RuleSets.
     *
     * @param minimumPriority
     *            The minimum priority.
     */
    public void setMinimumPriority(RulePriority minimumPriority) {
        this.minimumPriority = minimumPriority;
    }


    /**
     * Create a Renderer instance based upon the configured reporting options.
     * No writer is created.
     *
     * @return renderer
     */
    public Renderer createRenderer() {
        return createRenderer(false);
    }

    /**
     * Create a Renderer instance based upon the configured reporting options.
     * If withReportWriter then we'll configure it with a writer for the
     * reportFile specified.
     *
     * @param withReportWriter
     *            whether to configure a writer or not
     * @return A Renderer instance.
     */
    public Renderer createRenderer(boolean withReportWriter) {
        Renderer renderer = RendererFactory.createRenderer(reportFormat, reportProperties);
        renderer.setShowSuppressedViolations(showSuppressedViolations);
        if (withReportWriter) {
            renderer.setReportFile(getReportFilePath() != null ? getReportFilePath().toString() : null);
        }
        return renderer;
    }

    /**
     * Get the report format.
     *
     * @return The report format.
     */
    public String getReportFormat() {
        return reportFormat;
    }

    /**
     * Set the report format. This should be a name of a Renderer.
     *
     * @param reportFormat
     *            The report format.
     *
     * @see Renderer
     */
    public void setReportFormat(String reportFormat) {
        this.reportFormat = reportFormat;
    }

    /**
     * Get whether the report should show suppressed violations.
     *
     * @return <code>true</code> if showing suppressed violations,
     *         <code>false</code> otherwise.
     */
    public boolean isShowSuppressedViolations() {
        return showSuppressedViolations;
    }

    /**
     * Set whether the report should show suppressed violations.
     *
     * @param showSuppressedViolations
     *            <code>true</code> if showing suppressed violations,
     *            <code>false</code> otherwise.
     */
    public void setShowSuppressedViolations(boolean showSuppressedViolations) {
        this.showSuppressedViolations = showSuppressedViolations;
    }

    /**
     * Get the Report properties. These are used to create the Renderer.
     *
     * @return The report properties.
     */
    public Properties getReportProperties() {
        return reportProperties;
    }

    /**
     * Set the Report properties. These are used to create the Renderer.
     *
     * @param reportProperties
     *            The Report properties to set.
     */
    public void setReportProperties(Properties reportProperties) {
        this.reportProperties = reportProperties;
    }

    /**
     * Retrieves the currently used analysis cache. Will never be null.
     *
     * @return The currently used analysis cache. Never null.
     *
     * @apiNote This is internal API.
     */
    AnalysisCache getAnalysisCache() {
        // Make sure we are not null
        if (analysisCache == null || isIgnoreIncrementalAnalysis() && !(analysisCache instanceof NoopAnalysisCache)) {
            // sets a noop cache
            setAnalysisCache(new NoopAnalysisCache());
        }

        return analysisCache;
    }

    /**
     * Sets the analysis cache to be used. Setting a
     * value of {@code null} will cause a Noop AnalysisCache to be used.
     * If incremental analysis was explicitly disabled ({@link #isIgnoreIncrementalAnalysis()}),
     * then this method is a noop.
     *
     * @param cache The analysis cache to be used.
     *
     * @apiNote This is internal API. Use {@link #setAnalysisCacheLocation(String)} to configure a cache.
     */
    void setAnalysisCache(final AnalysisCache cache) {
        // the doc says it's a noop if incremental analysis was disabled,
        // but it's actually the getter that enforces that
        this.analysisCache = cache == null ? new NoopAnalysisCache() : cache;
    }

    /**
     * Sets the location of the analysis cache to be used. This will automatically configure
     * and appropriate AnalysisCache implementation. Setting a
     * value of {@code null} will cause a Noop AnalysisCache to be used.
     * If incremental analysis was explicitly disabled ({@link #isIgnoreIncrementalAnalysis()}),
     * then this method is a noop.
     *
     * @param cacheLocation The location of the analysis cache to be used. Use {@code null}
     *                      to disable the cache.
     */
    public void setAnalysisCacheLocation(final String cacheLocation) {
        setAnalysisCache(cacheLocation == null
                         ? new NoopAnalysisCache()
                         : new FileAnalysisCache(new File(cacheLocation)));
    }


    /**
     * Sets whether the user has explicitly disabled incremental analysis or not.
     * If so, incremental analysis is not used, and all suggestions to use it are
     * disabled. The analysis cached location is ignored, even if it's specified.
     *
     * @param noCache Whether to ignore incremental analysis or not
     */
    public void setIgnoreIncrementalAnalysis(boolean noCache) {
        // see #getAnalysisCache for the implementation.
        this.ignoreIncrementalAnalysis = noCache;
    }


    /**
     * Returns whether incremental analysis was explicitly disabled by the user
     * or not.
     *
     * @return {@code true} if incremental analysis is explicitly disabled
     */
    public boolean isIgnoreIncrementalAnalysis() {
        return ignoreIncrementalAnalysis;
    }

    /**
     * Get the file to which the report should render.
     *
     * @return The file to which to render.
     */
    public Path getReportFilePath() {
        return reportFile;
    }

    /**
     * Set the file to which the report should render.
     *
     * @param reportFile the file to set
     */
    public void setReportFile(Path reportFile) {
        this.reportFile = reportFile;
    }

    @Override
    protected void checkLanguageIsAcceptable(Language lang) throws UnsupportedOperationException {
        if (!(lang instanceof PmdCapableLanguage)) {
            throw new UnsupportedOperationException("Language " + lang.getId() + " does not support analysis with PMD and cannot be used in a PMDConfiguration. "
                + "You may be able to use it with CPD though.");
        }
    }
}
