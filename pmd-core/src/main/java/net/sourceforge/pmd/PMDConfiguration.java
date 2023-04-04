/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.annotation.DeprecatedUntil700;
import net.sourceforge.pmd.cache.AnalysisCache;
import net.sourceforge.pmd.cache.FileAnalysisCache;
import net.sourceforge.pmd.cache.NoopAnalysisCache;
import net.sourceforge.pmd.cli.PmdParametersParseResult;
import net.sourceforge.pmd.internal.util.ClasspathClassLoader;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionDiscoverer;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.renderers.RendererFactory;
import net.sourceforge.pmd.util.AssertionUtil;
import net.sourceforge.pmd.util.log.MessageReporter;
import net.sourceforge.pmd.util.log.internal.SimpleMessageReporter;

/**
 * This class contains the details for the runtime configuration of a PMD run.
 * You can either create one and set individual fields, or mimic a CLI run by
 * using {@link PmdParametersParseResult#extractParameters(String...) extractParameters}.
 *
 * <p>There are several aspects to the configuration of PMD.
 *
 * <p>The aspects related to generic PMD behavior:</p>
 * <ul>
 * <li>Suppress marker is used in source files to suppress a RuleViolation,
 * defaults to {@value DEFAULT_SUPPRESS_MARKER}. {@link #getSuppressMarker()}</li>
 * <li>The number of threads to create when invoking on multiple files, defaults
 * one thread per available processor. {@link #getThreads()}</li>
 * <li>A ClassLoader to use when loading classes during Rule processing (e.g.
 * during type resolution), defaults to ClassLoader of the Configuration class.
 * {@link #getClassLoader()}</li>
 * <li>A means to configure a ClassLoader using a prepended classpath String,
 * instead of directly setting it programmatically.
 * {@link #prependAuxClasspath(String)}</li>
 * <li>A LanguageVersionDiscoverer instance, which defaults to using the default
 * LanguageVersion of each Language. Means are provided to change the
 * LanguageVersion for each Language.
 * {@link #getLanguageVersionDiscoverer()}</li>
 * </ul>
 *
 * <p>The aspects related to Rules and Source files are:</p>
 * <ul>
 * <li>RuleSets URIs: {@link #getRuleSetPaths()}</li>
 * <li>A minimum priority threshold when loading Rules from RuleSets, defaults
 * to {@link RulePriority#LOW}. {@link #getMinimumPriority()}</li>
 * <li>The character encoding of source files, defaults to the system default as
 * returned by <code>System.getProperty("file.encoding")</code>.
 * {@link #getSourceEncoding()}</li>
 * <li>A list of input paths to process for source files. This
 * may include files, directories, archives (e.g. ZIP files), etc.
 * {@link #getInputPathList()}</li>
 * <li>A flag which controls, whether {@link RuleSetLoader#enableCompatibility(boolean)} filter
 * should be used or not: #isRuleSetFactoryCompatibilityEnabled;
 * </ul>
 *
 * <ul>
 * <li>The renderer format to use for Reports. {@link #getReportFormat()}</li>
 * <li>The file to which the Report should render. {@link #getReportFile()}</li>
 * <li>Configure the root paths that are used to relativize file names in reports via {@link #addRelativizeRoot(Path)}.
 * This enables to get short names in reports.</li>
 * <li>The initialization properties to use when creating a Renderer instance.
 * {@link #getReportProperties()}</li>
 * <li>An indicator of whether to show suppressed Rule violations in Reports.
 * {@link #isShowSuppressedViolations()}</li>
 * </ul>
 *
 * <p>The aspects related to special PMD behavior are:</p>
 * <ul>
 * <li>An indicator of whether PMD should log debug information.
 * {@link #isDebug()}</li>
 * <li>An indicator of whether PMD should perform stress testing behaviors, such
 * as randomizing the order of file processing. {@link #isStressTest()}</li>
 * <li>An indicator of whether PMD should log benchmarking information.
 * {@link #isBenchmark()}</li>
 * </ul>
 */
public class PMDConfiguration extends AbstractConfiguration {
    private static final LanguageRegistry DEFAULT_REGISTRY = LanguageRegistry.PMD;

    /** The default suppress marker string. */
    public static final String DEFAULT_SUPPRESS_MARKER = "NOPMD";

    // General behavior options
    private String suppressMarker = DEFAULT_SUPPRESS_MARKER;
    private int threads = Runtime.getRuntime().availableProcessors();
    private ClassLoader classLoader = getClass().getClassLoader();
    private final LanguageVersionDiscoverer languageVersionDiscoverer;
    private LanguageVersion forceLanguageVersion;
    private MessageReporter reporter = new SimpleMessageReporter(LoggerFactory.getLogger(PMD.class));

    // Rule and source file options
    private List<String> ruleSets = new ArrayList<>();
    private RulePriority minimumPriority = RulePriority.LOW;
    private @NonNull List<Path> inputPaths = new ArrayList<>();
    private URI inputUri;
    private Path inputFilePath;
    private Path ignoreFilePath;
    private boolean ruleSetFactoryCompatibilityEnabled = true;

    // Reporting options
    private String reportFormat;
    private Path reportFile;
    private Properties reportProperties = new Properties();
    private boolean showSuppressedViolations = false;
    private boolean failOnViolation = true;

    @Deprecated
    private boolean stressTest;
    @Deprecated
    private boolean benchmark;
    private AnalysisCache analysisCache = new NoopAnalysisCache();
    private boolean ignoreIncrementalAnalysis;
    private final LanguageRegistry langRegistry;
    private final List<Path> relativizeRoots = new ArrayList<>();
    private final Map<Language, LanguagePropertyBundle> langProperties = new HashMap<>();

    public PMDConfiguration() {
        this(DEFAULT_REGISTRY);
    }

    public PMDConfiguration(@NonNull LanguageRegistry languageRegistry) {
        this.langRegistry = Objects.requireNonNull(languageRegistry);
        this.languageVersionDiscoverer = new LanguageVersionDiscoverer(languageRegistry);
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
     * @param classpath
     *            The prepended classpath.
     * @throws IOException
     *             if the given classpath is invalid (e.g. does not exist)
     * @see PMDConfiguration#setClassLoader(ClassLoader)
     * @see ClasspathClassLoader
     *
     * @deprecated Use {@link #prependAuxClasspath(String)}, which doesn't
     * throw a checked {@link IOException}
     */
    @Deprecated
    public void prependClasspath(String classpath) throws IOException {
        try {
            prependAuxClasspath(classpath);
        } catch (IllegalArgumentException e) {
            throw new IOException(e);
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
     * Returns the message reporter that is to be used while running
     * the analysis.
     */
    public @NonNull MessageReporter getReporter() {
        return reporter;
    }

    /**
     * Sets the message reporter that is to be used while running
     * the analysis.
     *
     * @param reporter A non-null message reporter
     */
    public void setReporter(@NonNull MessageReporter reporter) {
        AssertionUtil.requireParamNotNull("reporter", reporter);
        this.reporter = reporter;
    }

    /**
     * Get the LanguageVersionDiscoverer, used to determine the LanguageVersion
     * of a source file.
     *
     * @return The LanguageVersionDiscoverer.
     */
    public LanguageVersionDiscoverer getLanguageVersionDiscoverer() {
        return languageVersionDiscoverer;
    }

    /**
     * Get the LanguageVersion specified by the force-language parameter. This overrides detection based on file
     * extensions
     *
     * @return The LanguageVersion.
     */
    public LanguageVersion getForceLanguageVersion() {
        return forceLanguageVersion;
    }

    /**
     * Is the force-language parameter set to anything?
     *
     * @return true if ${@link #getForceLanguageVersion()} is not null
     */
    public boolean isForceLanguageVersion() {
        return forceLanguageVersion != null;
    }

    /**
     * Set the LanguageVersion specified by the force-language parameter. This overrides detection based on file
     * extensions
     *
     * @param forceLanguageVersion the language version
     */
    public void setForceLanguageVersion(@Nullable LanguageVersion forceLanguageVersion) {
        if (forceLanguageVersion != null) {
            checkLanguageIsRegistered(forceLanguageVersion.getLanguage());
        }
        this.forceLanguageVersion = forceLanguageVersion;
        languageVersionDiscoverer.setForcedVersion(forceLanguageVersion);
    }

    /**
     * Set the given LanguageVersion as the current default for it's Language.
     *
     * @param languageVersion
     *            the LanguageVersion
     */
    public void setDefaultLanguageVersion(LanguageVersion languageVersion) {
        Objects.requireNonNull(languageVersion);
        languageVersionDiscoverer.setDefaultLanguageVersion(languageVersion);
        getLanguageProperties(languageVersion.getLanguage()).setLanguageVersion(languageVersion.getVersion());
    }

    /**
     * Set the given LanguageVersions as the current default for their
     * Languages.
     *
     * @param languageVersions
     *            The LanguageVersions.
     */
    public void setDefaultLanguageVersions(List<LanguageVersion> languageVersions) {
        for (LanguageVersion languageVersion : languageVersions) {
            setDefaultLanguageVersion(languageVersion);
        }
    }

    /**
     * Get the LanguageVersion of the source file with given name. This depends
     * on the fileName extension, and the java version.
     * <p>
     * For compatibility with older code that does not always pass in a correct
     * filename, unrecognized files are assumed to be java files.
     * </p>
     *
     * @param fileName
     *            Name of the file, can be absolute, or simple.
     * @return the LanguageVersion
     */
    // FUTURE Delete this? I can't think of a good reason to keep it around.
    // Failure to determine the LanguageVersion for a file should be a hard
    // error, or simply cause the file to be skipped?
    public @Nullable LanguageVersion getLanguageVersionOfFile(String fileName) {
        LanguageVersion forcedVersion = getForceLanguageVersion();
        if (forcedVersion != null) {
            // use force language if given
            return forcedVersion;
        }

        // otherwise determine by file extension
        return languageVersionDiscoverer.getDefaultLanguageVersionForFile(fileName);
    }

    LanguageRegistry getLanguageRegistry() {
        return langRegistry;
    }

    /**
     * Get the comma separated list of RuleSet URIs.
     *
     * @return The RuleSet URIs.
     *
     * @deprecated Use {@link #getRuleSetPaths()}
     */
    @Deprecated
    @DeprecatedUntil700
    public @Nullable String getRuleSets() {
        if (ruleSets.isEmpty()) {
            return null;
        }
        return String.join(",", ruleSets);
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
     * Set the comma separated list of RuleSet URIs.
     *
     * @param ruleSets the rulesets to set
     *
     * @deprecated Use {@link #setRuleSets(List)} or {@link #addRuleSet(String)}.
     */
    @Deprecated
    @DeprecatedUntil700
    public void setRuleSets(@Nullable String ruleSets) {
        if (ruleSets == null) {
            this.ruleSets = new ArrayList<>();
        } else {
            this.ruleSets = new ArrayList<>(Arrays.asList(ruleSets.split(",")));
        }
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
     * Returns the list of input paths to explore. This is an
     * unmodifiable list.
     */
    public @NonNull List<Path> getInputPathList() {
        return Collections.unmodifiableList(inputPaths);
    }

    /**
     * Set the comma separated list of input paths to process for source files.
     *
     * @param inputPaths The comma separated list.
     *
     * @throws NullPointerException If the parameter is null
     * @deprecated Use {@link #setInputPathList(List)} or {@link #addInputPath(Path)}
     */
    @Deprecated
    public void setInputPaths(String inputPaths) {
        if (inputPaths.isEmpty()) {
            return;
        }
        List<Path> paths = new ArrayList<>();
        for (String s : inputPaths.split(",")) {
            paths.add(Paths.get(s));
        }
        this.inputPaths = paths;
    }

    /**
     * Set the input paths to the given list of paths.
     *
     * @throws NullPointerException If the parameter is null or contains a null value
     */
    public void setInputPathList(final List<Path> inputPaths) {
        AssertionUtil.requireContainsNoNullValue("input paths", inputPaths);
        this.inputPaths = new ArrayList<>(inputPaths);
    }


    /**
     * Add an input path. It is not split on commas.
     *
     * @throws NullPointerException If the parameter is null
     */
    public void addInputPath(@NonNull Path inputPath) {
        Objects.requireNonNull(inputPath);
        this.inputPaths.add(inputPath);
    }

    /** Returns the path to the file list text file. */
    public @Nullable Path getInputFile() {
        return inputFilePath;
    }

    public @Nullable Path getIgnoreFile() {
        return ignoreFilePath;
    }

    /**
     * The input file path points to a single file, which contains a
     * comma-separated list of source file names to process.
     *
     * @param inputFilePath path to the file
     * @deprecated Use {@link #setInputFilePath(Path)}
     */
    @Deprecated
    public void setInputFilePath(String inputFilePath) {
        this.inputFilePath = inputFilePath == null ? null : Paths.get(inputFilePath);
    }

    /**
     * The input file path points to a single file, which contains a
     * comma-separated list of source file names to process.
     *
     * @param inputFilePath path to the file
     */
    public void setInputFilePath(Path inputFilePath) {
        this.inputFilePath = inputFilePath;
    }

    /**
     * The input file path points to a single file, which contains a
     * comma-separated list of source file names to ignore.
     *
     * @param ignoreFilePath path to the file
     * @deprecated Use {@link #setIgnoreFilePath(Path)}
     */
    @Deprecated
    public void setIgnoreFilePath(String ignoreFilePath) {
        this.ignoreFilePath = ignoreFilePath == null ? null : Paths.get(ignoreFilePath);
    }

    /**
     * The input file path points to a single file, which contains a
     * comma-separated list of source file names to ignore.
     *
     * @param ignoreFilePath  path to the file
     */
    public void setIgnoreFilePath(Path ignoreFilePath) {
        this.ignoreFilePath = ignoreFilePath;
    }

    /**
     * Get the input URI to process for source code objects.
     *
     * @return URI
     */
    public URI getUri() {
        return inputUri;
    }

    /**
     * Set the input URI to process for source code objects.
     *
     * @param inputUri a single URI
     * @deprecated Use {@link PMDConfiguration#setInputUri(URI)}
     */
    @Deprecated
    public void setInputUri(String inputUri) {
        this.inputUri = inputUri == null ? null : URI.create(inputUri);
    }

    /**
     * Set the input URI to process for source code objects.
     *
     * @param inputUri a single URI
     */
    public void setInputUri(URI inputUri) {
        this.inputUri = inputUri;
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
            renderer.setReportFile(reportFile == null ? null : reportFile.toString());
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
     * Get the file to which the report should render.
     *
     * @return The file to which to render.
     * @deprecated Use {@link #getReportFilePath()}
     */
    @Deprecated
    public String getReportFile() {
        return reportFile == null ? null : reportFile.toString();
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
     * @deprecated Use {@link #setReportFile(Path)}
     */
    @Deprecated
    public void setReportFile(String reportFile) {
        this.reportFile = reportFile == null ? null : Paths.get(reportFile);
    }

    /**
     * Set the file to which the report should render.
     *
     * @param reportFile the file to set
     */
    public void setReportFile(Path reportFile) {
        this.reportFile = reportFile;
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
     * Return the stress test indicator. If this value is <code>true</code> then
     * PMD will randomize the order of file processing to attempt to shake out
     * bugs.
     *
     * @return <code>true</code> if stress test is enbaled, <code>false</code>
     *         otherwise.
     *
     * @deprecated For removal
     */
    @Deprecated
    public boolean isStressTest() {
        return stressTest;
    }

    /**
     * Set the stress test indicator.
     *
     * @param stressTest
     *            The stree test indicator to set.
     * @see #isStressTest()
     * @deprecated For removal.
     */
    @Deprecated
    public void setStressTest(boolean stressTest) {
        this.stressTest = stressTest;
    }

    /**
     * Return the benchmark indicator. If this value is <code>true</code> then
     * PMD will log benchmark information.
     *
     * @return <code>true</code> if benchmark logging is enbaled,
     *         <code>false</code> otherwise.
     * @deprecated This behavior is down to CLI, not part of the core analysis.
     */
    @Deprecated
    public boolean isBenchmark() {
        return benchmark;
    }

    /**
     * Set the benchmark indicator.
     *
     * @param benchmark
     *            The benchmark indicator to set.
     * @see #isBenchmark()
     * @deprecated This behavior is down to CLI, not part of the core analysis.
     */
    @Deprecated
    public void setBenchmark(boolean benchmark) {
        this.benchmark = benchmark;
    }

    /**
     * Whether PMD should exit with status 4 (the default behavior, true) if
     * violations are found or just with 0 (to not break the build, e.g.).
     *
     * @return failOnViolation
     */
    public boolean isFailOnViolation() {
        return failOnViolation;
    }

    /**
     * Sets whether PMD should exit with status 4 (the default behavior, true)
     * if violations are found or just with 0 (to not break the build, e.g.).
     *
     * @param failOnViolation
     *            failOnViolation
     */
    public void setFailOnViolation(boolean failOnViolation) {
        this.failOnViolation = failOnViolation;
    }

    /**
     * Checks if the rule set factory compatibility feature is enabled.
     *
     * @return true, if the rule set factory compatibility feature is enabled
     *
     * @see RuleSetLoader#enableCompatibility(boolean)
     */
    public boolean isRuleSetFactoryCompatibilityEnabled() {
        return ruleSetFactoryCompatibilityEnabled;
    }

    /**
     * Sets the rule set factory compatibility feature enabled/disabled.
     *
     * @param ruleSetFactoryCompatibilityEnabled {@code true} if the feature should be enabled
     *
     * @see RuleSetLoader#enableCompatibility(boolean)
     */
    public void setRuleSetFactoryCompatibilityEnabled(boolean ruleSetFactoryCompatibilityEnabled) {
        this.ruleSetFactoryCompatibilityEnabled = ruleSetFactoryCompatibilityEnabled;
    }

    /**
     * Retrieves the currently used analysis cache. Will never be null.
     *
     * @return The currently used analysis cache. Never null.
     */
    public AnalysisCache getAnalysisCache() {
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
     */
    public void setAnalysisCache(final AnalysisCache cache) {
        // the doc says it's a noop if incremental analysis was disabled,
        // but it's actually the getter that enforces that
        this.analysisCache = cache == null ? new NoopAnalysisCache() : cache;
    }

    /**
     * Sets the location of the analysis cache to be used. This will automatically configure
     * and appropriate AnalysisCache implementation.
     *
     * @param cacheLocation The location of the analysis cache to be used.
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
     * Set the path used to shorten paths output in the report.
     * The path does not need to exist. If it exists, it must point
     * to a directory and not a file. See {@link #getRelativizeRoots()}
     * for the interpretation.
     *
     * <p>If several paths are added, the shortest paths possible are
     * built.
     *
     * @param path A path
     *
     * @throws IllegalArgumentException If the path points to a file, and not a directory
     * @throws NullPointerException If the path is null
     */
    public void addRelativizeRoot(Path path) {
        // Note: the given path is not further modified or resolved. E.g. there is no special handling for symlinks.
        // The goal is, that if the user inputs a path, PMD should output in terms of that path, not it's resolution.
        this.relativizeRoots.add(Objects.requireNonNull(path));

        if (Files.isRegularFile(path)) {
            throw new IllegalArgumentException("Relativize root should be a directory: " + path);
        }
    }


    /**
     * Add several paths to shorten paths that are output in the report.
     * See {@link #addRelativizeRoot(Path)}.
     *
     * @param paths A list of non-null paths
     *
     * @throws IllegalArgumentException If any path points to a file, and not a directory
     * @throws NullPointerException     If the list, or any path in the list is null
     */
    public void addRelativizeRoots(List<Path> paths) {
        for (Path path : paths) {
            addRelativizeRoot(path);
        }
    }

    /**
     * Returns the paths used to shorten paths output in the report.
     * <ul>
     * <li>If the list is empty, then paths are not touched
     * <li>If the list is non-empty, then source file paths are relativized with all the items in the list.
     * The shortest of these relative paths is taken as the display name of the file.
     * </ul>
     */
    public List<Path> getRelativizeRoots() {
        return Collections.unmodifiableList(relativizeRoots);
    }

    /**
     * Returns a mutable bundle of language properties that are associated
     * to the given language (always the same for a given language).
     *
     * @param language A language, which must be registered
     */
    public @NonNull LanguagePropertyBundle getLanguageProperties(Language language) {
        checkLanguageIsRegistered(language);
        return langProperties.computeIfAbsent(language, Language::newPropertyBundle);
    }

    void checkLanguageIsRegistered(Language language) {
        if (!langRegistry.getLanguages().contains(language)) {
            throw new IllegalArgumentException(
                "Language '" + language.getId() + "' is not registered in " + getLanguageRegistry());
        }
    }
}
