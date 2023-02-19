/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import java.nio.charset.Charset;
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
import net.sourceforge.pmd.util.log.MessageReporter;

/**
 * Base configuration class for both PMD and CPD.
 *
 * @author Brian Remedios
 */
public abstract class AbstractConfiguration {

    private Charset sourceEncoding = Charset.forName(System.getProperty("file.encoding"));
    private boolean debug;
    private final Map<Language, LanguagePropertyBundle> langProperties = new HashMap<>();
    private final LanguageRegistry langRegistry;
    private MessageReporter reporter;
    private final LanguageVersionDiscoverer languageVersionDiscoverer;
    private LanguageVersion forceLanguageVersion;


    protected AbstractConfiguration(LanguageRegistry languageRegistry, MessageReporter messageReporter) {
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
     * Return the debug indicator. If this value is <code>true</code> then PMD
     * will log debug information.
     *
     * @return <code>true</code> if debug logging is enabled, <code>false</code>
     *         otherwise.
     */
    public boolean isDebug() {
        return debug;
    }

    /**
     * Set the debug indicator.
     *
     * @param debug
     *            The debug indicator to set.
     * @see #isDebug()
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
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

    public LanguageRegistry getLanguageRegistry() {
        return langRegistry;
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


}
