/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.cache.internal.AnalysisCache;
import net.sourceforge.pmd.cache.internal.FileAnalysisCache;
import net.sourceforge.pmd.cache.internal.NoopAnalysisCache;
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

    // General behavior options
    private String suppressMarker = DEFAULT_SUPPRESS_MARKER;
    private int threads = Runtime.getRuntime().availableProcessors();
    /**
     * @deprecated Since 7.27.0. This field is only used for fallback behavior.
     */
    @Deprecated
    private ClassLoader classLoader = null;
    private String auxClasspath = null;

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
     * Get the ClassLoader being used by PMD when analyzing code, e.g. Java.
     *
     * <p>Since 7.27.0, this method will only return the classloader, that has been
     * set explicitly via {@link #setClassLoader(ClassLoader)}. Instead of setting a classloader,
     * the auxClasspath should be configured via {@link #setAuxClasspath(String)} or {@link #prependAuxClasspath(String)}.</p>
     *
     * <p>See also the notes on {@link #setClassLoader(ClassLoader)} regarding closing and supported types.</p>
     *
     * @return The ClassLoader being used
     * @deprecated Since 7.27.0. Use {@link #getAuxClasspath()} instead.
     */
    @Deprecated
    public ClassLoader getClassLoader() {
        if (classLoader == null && auxClasspath == null) {
            // preserve old behavior for default classloader
            return PMDConfiguration.class.getClassLoader();
        }
        return classLoader;
    }

    /**
     * Set the ClassLoader being used by PMD when analyzing code, e.g. Java. Setting a
     * value of <code>null</code> will cause the default ClassLoader to be used.
     *
     * <p>Since 7.27.0, a classloader should not be set anymore. Instead, the auxClasspath should be configured
     * via {@link #setAuxClasspath(String)} or {@link #prependAuxClasspath(String)}. For
     * backwards compatibility, if setting a classloader here, it will still be used.</p>
     *
     * <p>Note 1: The classloader might keep open file references to jar files if it is not closed.
     * A {@link java.net.URLClassLoader} is closeable and any given classloader that is {@link java.io.Closeable}
     * will be closed, when PMD is called via {@link PmdAnalysis}. In other cases, e.g. not using PmdAnalysis,
     * you need to close the classloader yourself.</p>
     *
     * <p>Note 2: Only subtypes of {@link java.net.URLClassLoader} are compatible with PMD, as for the
     * <a href="https://docs.pmd-code.org/latest/pmd_userdocs_incremental_analysis.html">incremental analysis</a>
     * we need to figure out the actual URLs of the classpath to check the validity of our cache.</p>
     *
     * @param classLoader
     *            The ClassLoader to use
     * @deprecated Since 7.27.0. Use {@link #prependAuxClasspath(String)} or {@link #setAuxClasspath(String)} instead.
     */
    @Deprecated
    public void setClassLoader(ClassLoader classLoader) {
        if (auxClasspath != null) {
            throw new IllegalStateException("Can't mix setClassLoader with setAuxClasspath or prependAuxClasspath!");
        }
        if (classLoader != null && !(classLoader instanceof URLClassLoader)) {
            getReporter().warn("Unsupported classloader for auxClasspath detected: " + classLoader.getClass() + ". "
                    + "Only " + URLClassLoader.class.getName() + " is supported.");
        }
        this.classLoader = classLoader;
    }

    /**
     * Prepend the specified classpath like string to the currently set auxClasspath of
     * the configuration.
     *
     * <p>Use {@link #setAuxClasspath(String)} if you don't want any fallbacks (neither to PMD's runtime
     * classpath nor to the current JVM runtime) and only access classes on the given auxClasspath.</p>
     *
     * <p>Specify the JVM's platform classpath yourself explicitly (e.g. adding {@code jrt-fs.jar})
     * in order to not fall back to the current JVM runtime.</p>
     *
     * <p>If the classpath String looks like a URL to a file (i.e. starts with
     * <code>file://</code>) the file will be read with each line representing
     * an entry on the classpath.</p>
     *
     * <p>You can specify multiple class paths separated by `:` on Unix-systems or `;` under Windows.
     * See {@link File#pathSeparator}.
     *
     * @param classpath The additional classpath entries to be prepended.
     *
     * @throws IllegalArgumentException if the given classpath is invalid (e.g. does not exist)
     * @see PMDConfiguration#setAuxClasspath(String)
     */
    public void prependAuxClasspath(String classpath) {
        if (classpath == null) {
            return;
        }
        if (classLoader != null) {
            throw new IllegalStateException("Can't mix setClasspath with prependAuxClasspath!");
        }
        verifyAuxClasspath(classpath); // throws IllegalArgumentException...

        if (auxClasspath == null) {
            auxClasspath = classpath;
        } else {
            auxClasspath = classpath + File.pathSeparator + auxClasspath;
        }
    }

    /**
     * Checks whether any referenced file on the classpath exists.
     *
     * <p>Valid classpath formats (under Unix):
     * <ul>
     *     <li>/absolute/file.jar:relative/file.jar</li>
     *     <li>file:relative/classpath.txt</li>
     *     <li>file:/absolute/classpath.txt</li>
     * </ul>
     * </p>
     *
     * @param classpath
     * @throws IllegalArgumentException if the given classpath is invalid (e.g. does not exist).
     */
    private void verifyAuxClasspath(String classpath) {
        if (classpath == null) {
            return;
        }

        List<Path> notExistingFiles = new ArrayList<>();
        if (classpath.startsWith("file:")) {
            try {
                URI uri = new URI(classpath);
                String uriPath = uri.getPath();
                if (uriPath == null) {
                    // to support relative paths, only the scheme specific part is available
                    uriPath = uri.getSchemeSpecificPart();
                }
                Path path = Paths.get(uriPath);

                try (Stream<String> lines = Files.lines(path, Charset.defaultCharset())) {
                    notExistingFiles.addAll(lines
                            .map(String::trim)
                            .filter(s -> !s.startsWith("#"))
                            .map(Paths::get)
                            .filter(p -> !Files.exists(p))
                            .collect(Collectors.toList()));
                }
            } catch (IOException | URISyntaxException e) {
                throw new IllegalArgumentException(e);
            }
        } else {
            StringTokenizer toker = new StringTokenizer(classpath, File.pathSeparator);
            while (toker.hasMoreTokens()) {
                String token = toker.nextToken();
                Path path = Paths.get(token);
                if (!Files.exists(path)) {
                    notExistingFiles.add(path);
                }
            }
        }
        if (!notExistingFiles.isEmpty()) {
            throw new IllegalArgumentException("Invalid classpath - not existing files: " + notExistingFiles);
        }
    }

    /**
     * Uses the specified classpath like string as the auxClasspath of
     * the configuration.
     *
     * <p>If the classpath String looks like a URL to a file (i.e. starts with
     * <code>file://</code>) the file will be read with each line representing
     * an entry on the classpath.</p>
     *
     * <p>You can specify multiple class paths separated by `:` on Unix-systems or `;` under Windows.
     * See {@link File#pathSeparator}.
     *
     * @param classpath The classpath entries to be used.
     *
     * @throws IllegalArgumentException if the given classpath is invalid (e.g. does not exist)
     * @see PMDConfiguration#prependAuxClasspath(String)
     * @since 7.27.0
     */
    public void setAuxClasspath(String classpath) {
        if (classLoader != null) {
            throw new IllegalStateException("Can't mix setClasspath with setAuxClasspath!");
        }
        verifyAuxClasspath(classpath);
        auxClasspath = classpath;
    }

    /**
     * Gets the currently set auxClasspath.
     *
     * @return the configured auxClasspath. Might be {@code null}.
     * @see #setAuxClasspath(String)
     * @see #prependAuxClasspath(String)
     * @since 7.27.0
     */
    public String getAuxClasspath() {
        if (classLoader != null) {
            throw new IllegalStateException("Can't mix setClasspath with getAuxClasspath!");
        }
        return auxClasspath;
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
     * @internalApi None of this is published API, and compatibility can be broken anytime! Use this only at your own risk.
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
     * @internalApi None of this is published API, and compatibility can be broken anytime! Use this only at your own risk.
     * Use {@link #setAnalysisCacheLocation(String)} to configure a cache.
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

    @Override
    protected void checkLanguageIsAcceptable(Language lang) throws UnsupportedOperationException {
        if (!(lang instanceof PmdCapableLanguage)) {
            throw new UnsupportedOperationException("Language " + lang.getId() + " does not support analysis with PMD and cannot be used in a PMDConfiguration. "
                + "You may be able to use it with CPD though.");
        }
    }
}
