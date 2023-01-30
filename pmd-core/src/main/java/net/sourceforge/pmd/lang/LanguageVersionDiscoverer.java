/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.annotation.DeprecatedUntil700;
import net.sourceforge.pmd.util.AssertionUtil;

/**
 * This class can discover the LanguageVersion of a source file. Further, every
 * Language has a default LanguageVersion, which can be temporarily overridden
 * here.
 */
public class LanguageVersionDiscoverer {

    private final LanguageRegistry languageRegistry;
    private final Map<Language, LanguageVersion> languageToLanguageVersion = new HashMap<>();
    private LanguageVersion forcedVersion;


    /**
     * Build a new instance.
     *
     * @param forcedVersion If non-null, all files should be assigned this version.
     *                      The methods of this class still work as usual and do not
     *                      care about the forced language version.
     */
    public LanguageVersionDiscoverer(LanguageRegistry registry, LanguageVersion forcedVersion) {
        this.languageRegistry = registry;
        this.forcedVersion = forcedVersion;
    }

    /**
     * Build a new instance with no forced version.
     */
    public LanguageVersionDiscoverer(LanguageRegistry registry) {
        this(registry, null);
    }

    /**
     * Set the given LanguageVersion as the current default for it's Language.
     *
     * @param languageVersion
     *            The new default for the Language.
     * @return The previous default version for the language.
     */
    public LanguageVersion setDefaultLanguageVersion(LanguageVersion languageVersion) {
        AssertionUtil.requireParamNotNull("languageVersion", languageVersion);
        LanguageVersion currentLanguageVersion = languageToLanguageVersion.put(languageVersion.getLanguage(),
                languageVersion);
        if (currentLanguageVersion == null) {
            currentLanguageVersion = languageVersion.getLanguage().getDefaultVersion();
        }
        return currentLanguageVersion;
    }

    /**
     * Get the current default LanguageVersion for the given Language.
     *
     * @param language
     *            The Language.
     * @return The current default version for the language.
     */
    public LanguageVersion getDefaultLanguageVersion(Language language) {
        Objects.requireNonNull(language);
        LanguageVersion languageVersion = languageToLanguageVersion.get(language);
        if (languageVersion == null) {
            languageVersion = language.getDefaultVersion();
        }
        return languageVersion;
    }

    /**
     * Get the default LanguageVersion for the first Language of a given source
     * file.
     *
     * @param sourceFile
     *            The file.
     * @return The currently configured LanguageVersion for the source file, or
     *         <code>null</code> if there are no supported Languages for the
     *         file.
     */
    public LanguageVersion getDefaultLanguageVersionForFile(File sourceFile) {
        return getDefaultLanguageVersionForFile(sourceFile.getName());
    }

    /**
     * Get the LanguageVersion for the first Language of a source file with the
     * given name.
     *
     * @param fileName
     *            The file name.
     * @return The currently configured LanguageVersion for the source file or
     *         <code>null</code> if there are no supported Languages for the
     *         file.
     */
    public @Nullable LanguageVersion getDefaultLanguageVersionForFile(String fileName) {
        List<Language> languages = getLanguagesForFile(fileName);
        LanguageVersion languageVersion = null;
        if (!languages.isEmpty()) {
            languageVersion = getDefaultLanguageVersion(languages.get(0));
        }
        return languageVersion;
    }

    public LanguageVersion getForcedVersion() {
        return forcedVersion;
    }

    public void setForcedVersion(LanguageVersion forceLanguageVersion) {
        this.forcedVersion = forceLanguageVersion;
    }

    /**
     * Get the Languages of a given source file.
     *
     * @param sourceFile
     *            The file.
     * @return The Languages for the source file, may be empty.
     *
     * @deprecated PMD 7 avoids using {@link File}.
     */
    @Deprecated
    @DeprecatedUntil700
    public List<Language> getLanguagesForFile(File sourceFile) {
        return getLanguagesForFile(sourceFile.getName());
    }

    /**
     * Get the Languages of a given source file.
     *
     * @param fileName
     *            The file name.
     * @return The Languages for the source file, may be empty.
     */
    public List<Language> getLanguagesForFile(String fileName) {
        String extension = getExtension(fileName);
        return languageRegistry.getLanguages().stream()
                               .filter(it -> it.hasExtension(extension))
                               .collect(Collectors.toList());
    }

    // Get the extensions from a file
    private String getExtension(String fileName) {
        return StringUtils.substringAfterLast(fileName, ".");
    }


}
