/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Objects;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.LanguageRegistry;

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

    /**
     * Create a new abstract configuration.
     */
    protected AbstractConfiguration() {
        super();
    }

    protected AbstractConfiguration(LanguageRegistry languageRegistry) {
        this.langRegistry = Objects.requireNonNull(languageRegistry);
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
    public void setSourceEncoding(String sourceEncoding) {
        this.sourceEncoding = Charset.forName(sourceEncoding);
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
}
