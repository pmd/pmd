/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import java.util.List;
import java.util.ServiceLoader;

/**
 * Represents a language module, and provides access to language-specific
 * functionality. You can get a language instance from {@link LanguageRegistry}.
 * Using a language involves first selecting the relevant {@link LanguageVersion}
 * for the sources, and accessing implemented services through {@link LanguageVersion#getLanguageVersionHandler()}.
 *
 * <p>Language instances must be registered with a {@linkplain ServiceLoader service file}
 * to be picked up on by the {@link LanguageRegistry}.
 *
 * <p>The following are key components of a language in PMD:
 * <ul>
 * <li>Name - Full name of the language</li>
 * <li>Short name - The common short form of the language</li>
 * <li>Terse name - The shortest and simplest possible form of the language
 * name, generally used for rule configuration</li>
 * <li>Extensions - File extensions associated with the language</li>
 * <li>Versions - The language versions associated with the language</li>
 * </ul>
 *
 * @see LanguageVersion
 * @see LanguageVersionDiscoverer
 */
public interface Language extends Comparable<Language> {

    String LANGUAGE_MODULES_CLASS_NAMES_PROPERTY = "languageModulesClassNames";

    /**
     * Returns the full name of this Language. This is generally the name of this
     * language without the use of acronyms, but possibly some capital letters,
     * eg {@code "Java"}. It's suitable for displaying in a GUI.
     *
     * @return The full name of this language.
     */
    String getName();

    /**
     * Returns the short name of this language. This is the commonly
     * used short form of this language's name, perhaps an acronym,
     * but possibly with special characters.
     *
     * @return The short name of this language.
     */
    String getShortName();

    /**
     * Returns the terse name of this language. This is a short, alphanumeric,
     * lowercase name, eg {@code "java"}. It's used to identify the language
     * in the ruleset XML, and is also in the package name of the language
     * module.
     *
     * @return The terse name of this language.
     */
    String getTerseName();


    /**
     * Returns the ID of this language. This is a short, alphanumeric,
     * lowercase name, eg {@code "java"}. It's used to identify the language
     * in the ruleset XML, and is also in the package name of the language
     * module.
     *
     * @return The ID of this language.
     */
    default String getId() {
        return getTerseName();
    }

    /**
     * Returns the list of file extensions associated with this language.
     * This list is unmodifiable. Extensions do not have a '.' prefix.
     *
     * @return A list of file extensions.
     */
    List<String> getExtensions();

    /**
     * Returns whether this language handles the given file extension.
     * The comparison is done ignoring case.
     *
     * @param extensionWithoutDot A file extension (without '.' prefix)
     *
     * @return <code>true</code> if this language handles the extension,
     *     <code>false</code> otherwise.
     */
    boolean hasExtension(String extensionWithoutDot);

    /**
     * Returns an ordered list of supported versions for this language.
     *
     * @return All supported language versions.
     */
    List<LanguageVersion> getVersions();

    /**
     * Returns a complete list of supported version names for this language including all aliases.
     *
     * @return All supported language version names and aliases.
     */
    List<String> getVersionNamesAndAliases();

    /**
     * Returns true if a language version with the given {@linkplain LanguageVersion#getVersion() version string}
     * is registered. Then, {@link #getVersion(String) getVersion} will return a non-null value.
     *
     * @param version A version string
     *
     * @return True if the version string is known
     */
    boolean hasVersion(String version);

    /**
     * Returns the language version with the given {@linkplain LanguageVersion#getVersion() version string}.
     * Returns null if no such version exists.
     *
     * @param version A language version string.
     *
     * @return The corresponding LanguageVersion, {@code null} if the
     *     version string is not recognized.
     */
    LanguageVersion getVersion(String version);

    /**
     * Returns the default language version for this language.
     * This is an arbitrary choice made by the PMD product, and can change
     * between PMD releases. Every language has a default version.
     *
     * @return The current default language version for this language.
     */
    LanguageVersion getDefaultVersion();

}
