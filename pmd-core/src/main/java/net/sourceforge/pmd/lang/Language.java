/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang;

import java.util.List;

/**
 * Interface each Language implementation has to implement. It is used by the
 * LanguageRregistry to access constants and implementation classes in order to
 * provide support for the language.
 * <p>
 * The following are key components of a Language in PMD:
 * <ul>
 * 	<li>Name - Full name of the Language</li>
 * 	<li>Short name - The common short form of the Language</li>
 * 	<li>Terse name - The shortest and simplest possible form of the Language
 * 		name, generally used for Rule configuration</li>
 * 	<li>Extensions - File extensions associated with the Language</li>
 * 	<li>Rule Chain Visitor - The RuleChainVisitor implementation used for this
 * 		Language</li>
 * 	<li>Versions - The LanguageVersions associated with the Language</li>
 * </ul>
 *
 * @see LanguageVersion
 * @see LanguageVersionDiscoverer
 */
public interface Language extends Comparable<Language> {

    String LANGUAGE_MODULES_CLASS_NAMES_PROPERTY = "languageModulesClassNames";

    /**
     * Get the full name of this Language.  This is generally the name of this
     * Language without the use of acronyms.
     * @return The full name of this Language.
     */
    String getName();

    /**
     * Get the short name of this Language.  This is the commonly used short
     * form of this Language's name, perhaps an acronym.
     * @return The short name of this Language.
     */
    String getShortName();

    /**
     * Get the terse name of this Language.  This is used for Rule configuration.
     * @return The terse name of this Language.
     */
    String getTerseName();

    /**
     * Get the list of file extensions associated with this Language.
     * @return List of file extensions.
     */
    List<String> getExtensions();

    /**
     * Returns whether the given Language handles the given file extension.
     * The comparison is done ignoring case.
     * @param extension A file extension.
     * @return <code>true</code> if this Language handles this extension, <code>false</code> otherwise.
     */
    boolean hasExtension(String extension);

    /**
     * Get the RuleChainVisitor implementation class used when visiting the AST
     * structure for this Rules for this Language.
     * @return The RuleChainVisitor class.
     * @see net.sourceforge.pmd.lang.rule.RuleChainVisitor
     */
    Class<?> getRuleChainVisitorClass();

    /**
     * Gets the list of supported LanguageVersion for this Language.
     * @return The LanguageVersion for this Language.
     */
    List<LanguageVersion> getVersions();

    boolean hasVersion(String version);

    /**
     * Get the LanguageVersion for the version string from this Language.
     * @param version The language version string.
     * @return The corresponding LanguageVersion, <code>null</code> if the
     * version string is not recognized.
     */
    LanguageVersion getVersion(String version);

    /**
     * Get the current PMD defined default LanguageVersion for this Language.
     * This is an arbitrary choice made by the PMD product, and can change
     * between PMD releases.  Every Language has a default version.
     * @return The current default LanguageVersion for this Language.
     */
    LanguageVersion getDefaultVersion();

}
