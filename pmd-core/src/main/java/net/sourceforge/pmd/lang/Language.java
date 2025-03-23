/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.cpd.CpdCapableLanguage;

/**
 * Represents a language module, and provides access to language-specific
 * functionality. You can get a language instance from a {@link LanguageRegistry},
 * see {@link LanguageRegistry#PMD} for instance.
 *
 * <p>Language instances are extensions to the core of PMD. They can be
 * registered with a {@linkplain ServiceLoader service file} so that
 * PMD automatically finds them on the classpath.
 *
 * <p>Instances of this interface are stateless and immutable after construction.
 * They mostly provide metadata about the language, like ID, name and different
 * versions that are supported.
 *
 * <p>Languages should implement the interfaces {@link PmdCapableLanguage}
 * or {@link CpdCapableLanguage} to be usable by PMD or CPD, respectively.
 *
 * @see LanguageVersion
 */
public interface Language extends Comparable<Language> {


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
     * Returns the ID of this language. This is a short, alphanumeric,
     * lowercase name, eg {@code "java"}. It's used to identify the language
     * in the ruleset XML, and is also in the package name of the language
     * module.
     *
     * @return The ID of this language.
     */
    String getId();

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
    default boolean hasExtension(String extensionWithoutDot) {
        return getExtensions().contains(extensionWithoutDot);
    }

    /**
     * Returns an ordered list of supported versions for this language.
     *
     * @return All supported language versions.
     */
    List<LanguageVersion> getVersions();

    /**
     * Returns the latest language version. May not be the
     * {@linkplain #getDefaultVersion() default}.
     *
     * @return The latest language version
     */
    default LanguageVersion getLatestVersion() {
        List<LanguageVersion> versions = getVersions();
        return versions.get(versions.size() - 1);
    }

    /**
     * Returns a complete set of supported version names for this language
     * including all aliases.
     *
     * @return All supported language version names and aliases.
     */
    Set<String> getVersionNamesAndAliases();

    /**
     * Returns true if a language version with the given {@linkplain LanguageVersion#getVersion() version string}
     * is registered. Then, {@link #getVersion(String) getVersion} will return a non-null value.
     *
     * @param version A version string
     *
     * @return True if the version string is known
     */
    default boolean hasVersion(String version) {
        return getVersion(version) != null;
    }

    /**
     * Returns the language version with the given {@linkplain LanguageVersion#getVersion() version string}.
     * Returns null if no such version exists.
     *
     * @param version A language version string.
     *
     * @return The corresponding LanguageVersion, {@code null} if the
     *     version string is not recognized.
     */
    default @Nullable LanguageVersion getVersion(String version) {
        for (LanguageVersion v : getVersions()) {
            if (v.getVersion().equals(version)) {
                return v;
            }
        }
        return null;
    }

    /**
     * Returns the default language version for this language.
     * This is an arbitrary choice made by the PMD product, and can change
     * between PMD releases. Every language has a default version.
     *
     * @return The current default language version for this language.
     */
    @NonNull LanguageVersion getDefaultVersion();


    /**
     * Creates a new bundle of properties that will serve to configure
     * the {@link LanguageProcessor} for this language. The returned
     * bundle must have all supported properties already declared. See
     * {@link PmdCapableLanguage} and {@link CpdCapableLanguage} for sites
     * where properties are passed back to the language with user-provided
     * values.
     *
     * @return A new set of properties
     */
    default LanguagePropertyBundle newPropertyBundle() {
        return new LanguagePropertyBundle(this);
    }


    /**
     * Returns a set of the IDs of languages that this language instance
     * depends on. Whenever this language is loaded into a {@link LanguageProcessorRegistry},
     * those dependencies need to be loaded as well.
     */
    Set<String> getDependencies();

}
