/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionDiscoverer;
import net.sourceforge.pmd.util.AssertionUtil;
import net.sourceforge.pmd.util.log.PmdReporter;

/**
 * Base configuration class for both PMD and CPD.
 *
 * @author Brian Remedios
 */
public abstract class AbstractConfiguration {

    private final List<Path> relativizeRoots = new ArrayList<>();
    private URI inputUri;
    private Charset sourceEncoding = Charset.forName(System.getProperty("file.encoding"));
    private final Map<Language, LanguagePropertyBundle> langProperties = new HashMap<>();
    private final LanguageRegistry langRegistry;
    private PmdReporter reporter;
    private final LanguageVersionDiscoverer languageVersionDiscoverer;
    private LanguageVersion forceLanguageVersion;
    private @NonNull List<Path> inputPaths = new ArrayList<>();
    private Path inputFilePath;
    private Path ignoreFilePath;
    private List<Path> excludes = new ArrayList<>();
    private boolean collectRecursive = true;
    private boolean failOnViolation = true;
    private boolean failOnError = true;


    protected AbstractConfiguration(LanguageRegistry languageRegistry, PmdReporter messageReporter) {
        this.langRegistry = Objects.requireNonNull(languageRegistry);
        this.languageVersionDiscoverer = new LanguageVersionDiscoverer(languageRegistry);
        this.reporter = Objects.requireNonNull(messageReporter);
    }

    /**
     * Get the character encoding of source files.
     *
     * @return The character encoding.
     */
    public Charset getSourceEncoding() {
        return sourceEncoding;
    }

    /**
     * Set the character encoding of source files.
     *
     * @param sourceEncoding
     *            The character encoding.
     */
    public void setSourceEncoding(Charset sourceEncoding) {
        this.sourceEncoding = Objects.requireNonNull(sourceEncoding);
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
        checkLanguageIsAcceptable(language);
    }

    public LanguageRegistry getLanguageRegistry() {
        return langRegistry;
    }

    /**
     * Returns the message reporter that is to be used while running
     * the analysis.
     */
    public @NonNull PmdReporter getReporter() {
        return reporter;
    }

    /**
     * Sets the message reporter that is to be used while running
     * the analysis.
     *
     * @param reporter A non-null message reporter
     */
    public void setReporter(@NonNull PmdReporter reporter) {
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
     * Make it so that the only extensions that are considered are those
     * of the given language. This is different from {@link #setForceLanguageVersion(LanguageVersion)}
     * because that one will assign the given language version to all files
     * irrespective of extension. This method, on the other hand, will
     * ignore files that do not match the given language.
     *
     * @param lang A language
     */
    public void setOnlyRecognizeLanguage(Language lang) {
        AssertionUtil.requireParamNotNull("language", lang);
        checkLanguageIsRegistered(lang);
        this.languageVersionDiscoverer.onlyRecognizeLanguages(LanguageRegistry.singleton(lang));
    }

    /**
     * Set the given LanguageVersion as the current default for it's Language.
     *
     * @param languageVersion the LanguageVersion
     */
    public void setDefaultLanguageVersion(LanguageVersion languageVersion) {
        Objects.requireNonNull(languageVersion);
        checkLanguageIsRegistered(languageVersion.getLanguage());

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
     * Check that it is correct to use the given language with this configuration.
     *
     * @throws UnsupportedOperationException if the language isn't supported.
     */
    protected void checkLanguageIsAcceptable(Language lang) throws UnsupportedOperationException {
        // do nothing
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
     * @throws NullPointerException     If the path is null
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
     */
    public void setInputUri(URI inputUri) {
        this.inputUri = inputUri;
    }

    /**
     * Returns the list of input paths to explore. This is an
     * unmodifiable list.
     */
    public @NonNull List<Path> getInputPathList() {
        return Collections.unmodifiableList(inputPaths);
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

    /** Returns the path to the file list include file. */
    public @Nullable Path getInputFile() {
        return inputFilePath;
    }

    /** Returns the path to the file list exclude file. */
    public @Nullable Path getIgnoreFile() {
        return ignoreFilePath;
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
     * @param ignoreFilePath  path to the file
     */
    public void setIgnoreFilePath(Path ignoreFilePath) {
        this.ignoreFilePath = ignoreFilePath;
    }

    public List<Path> getExcludes() {
        return excludes;
    }

    public void setExcludes(List<Path> excludes) {
        this.excludes = Objects.requireNonNull(excludes);
    }

    public boolean collectFilesRecursively() {
        return collectRecursive;
    }

    public void collectFilesRecursively(boolean collectRecursive) {
        this.collectRecursive = collectRecursive;
    }

    /**
     * Whether PMD should exit with status 4 (the default behavior, true) if
     * violations are found or just with 0 (to not break the build, e.g.).
     *
     * <p>Note: If additionally recoverable errors occurred, the exit status is 5. See
     * {@link #isFailOnError()}.
     *
     * @return failOnViolation
     *
     * @see #isFailOnError()
     * @since 6.0.0
     */
    public boolean isFailOnViolation() {
        return failOnViolation;
    }

    /**
     * Sets whether PMD should exit with status 4 (the default behavior, true)
     * if violations are found or just with 0 (to not break the build, e.g.).
     *
     * <p>Note: If additionally recoverable errors occurred, the exit status is 5. See
     * {@link #isFailOnError()}.
     *
     * @param failOnViolation whether to exit with 4 and fail the build if violations are found.
     *
     * @see #isFailOnError()
     * @since 6.0.0
     */
    public void setFailOnViolation(boolean failOnViolation) {
        this.failOnViolation = failOnViolation;
    }

    /**
     * Whether PMD should exit with status 5 (the default behavior, true) if
     * recoverable errors occurred or just with 0 (to not break the build, e.g.).
     *
     * <p>Note: If only violations are found, the exit status is 4. See
     * {@link #isFailOnViolation()}.
     *
     * @return failOnError
     *
     * @see #isFailOnViolation()
     * @since 7.3.0
     */
    public boolean isFailOnError() {
        return failOnError;
    }

    /**
     * Sets whether PMD should exit with status 5 (the default behavior, true)
     * if recoverable errors occurred or just with 0 (to not break the build, e.g.).
     *
     * <p>Note: If only violations are found, the exit status is 4. See
     * {@link #isFailOnViolation()}.
     *
     * @param failOnError whether to exit with 5 and fail the build if recoverable errors occurred.
     *
     * @see #isFailOnViolation()
     * @since 7.3.0
     */
    public void setFailOnError(boolean failOnError) {
        this.failOnError = failOnError;
    }
}
