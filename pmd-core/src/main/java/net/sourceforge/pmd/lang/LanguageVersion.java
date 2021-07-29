/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import java.util.List;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.annotation.InternalApi;

/**
 * Represents a version of a {@link Language}. Language instances provide
 * a list of supported versions ({@link Language#getVersions()}). Individual
 * versions can be retrieved from their version number ({@link Language#getVersion(String)}).
 *
 * <p>Versions are used to limit some rules to operate on only a version range.
 * For instance, a rule that suggests eliding local variable types in Java
 * (replacing them with {@code var}) makes no sense if the codebase is not
 * using Java 10 or later. This is determined by {@link Rule#getMinimumLanguageVersion()}
 * and {@link Rule#getMaximumLanguageVersion()}. These should be set in the
 * ruleset XML (they're attributes of the {@code <rule>} element), and not
 * overridden.
 *
 * <p>Example usage:
 * <pre>
 * Language javaLanguage = LanguageRegistry.{@link LanguageRegistry#getLanguage(String) getLanguage}("Java");
 * LanguageVersion java11 = javaLanguage.{@link Language#getVersion(String) getVersion}("11");
 * LanguageVersionHandler handler = java11.getLanguageVersionHandler();
 * Parser parser = handler.getParser(handler.getDefaultParserOptions());
 * // use parser
 * </pre>
 */
public class LanguageVersion implements Comparable<LanguageVersion> {

    private final Language language;
    private final String version;
    private final LanguageVersionHandler languageVersionHandler;

    /**
     * @deprecated Use {@link Language#getVersion(String)}. This is only
     *     supposed to be used when initializing a {@link Language} instance.
     */
    @Deprecated
    @InternalApi
    public LanguageVersion(Language language, String version, LanguageVersionHandler languageVersionHandler) {
        this.language = language;
        this.version = version;
        this.languageVersionHandler = languageVersionHandler;
    }

    /**
     * Returns the language that owns this version.
     */
    public Language getLanguage() {
        return language;
    }

    /**
     * Returns the version string. This is usually a version number, e.g.
     * {@code "1.7"} or {@code "11"}. This is used by {@link Language#getVersion(String)}.
     */
    public String getVersion() {
        return version;
    }

    /**
     * Returns the {@link LanguageVersionHandler}, which provides access
     * to version-specific services, like the parser.
     */
    public LanguageVersionHandler getLanguageVersionHandler() {
        return languageVersionHandler;
    }

    /**
     * Returns the name of this language version. This is the version string
     * prefixed with the {@linkplain Language#getName() language name}.
     *
     * @return The name of this LanguageVersion.
     */
    public String getName() {
        return version.length() > 0 ? language.getName() + ' ' + version : language.getName();
    }

    /**
     * Get the short name of this LanguageVersion. This is Language short name
     * appended with the LanguageVersion version if not an empty String.
     *
     * @return The short name of this LanguageVersion.
     */
    public String getShortName() {
        return version.length() > 0 ? language.getShortName() + ' ' + version : language.getShortName();
    }

    /**
     * Get the terse name of this LanguageVersion. This is Language terse name
     * appended with the LanguageVersion version if not an empty String.
     *
     * @return The terse name of this LanguageVersion.
     */
    public String getTerseName() {
        return version.length() > 0 ? language.getTerseName() + ' ' + version : language.getTerseName();
    }

    /**
     * Compare this version to another version of the same language identified
     * by the given version string.
     *
     * @param versionString The version with which to compare
     *
     * @throws IllegalArgumentException If the argument is not a valid version
     *                                  string for the parent language
     */
    public int compareToVersion(String versionString) {
        LanguageVersion otherVersion = language.getVersion(versionString);
        if (otherVersion == null) {
            throw new IllegalArgumentException(
                "No such version '" + versionString + "' for language " + language.getName());
        }
        return this.compareTo(otherVersion);
    }

    @Override
    public int compareTo(LanguageVersion o) {
        List<LanguageVersion> versions = language.getVersions();
        int thisPosition = versions.indexOf(this);
        int otherPosition = versions.indexOf(o);
        return Integer.compare(thisPosition, otherPosition);
    }

    /**
     * Compare this version to another version of the same language identified
     * by the given version string.
     *
     * @param versionString The version with which to compare
     *
     * @throws IllegalArgumentException If the argument is not a valid version
     *                                  string for the parent language
     */
    public int compareToVersion(String versionString) {
        LanguageVersion otherVersion = language.getVersion(versionString);
        if (otherVersion == null) {
            throw new IllegalArgumentException(
                "No such version '" + versionString + "' for language " + language.getName());
        }
        return this.compareTo(otherVersion);
    }

    @Override
    public String toString() {
        return language.toString() + "+version:" + version;
    }
}
