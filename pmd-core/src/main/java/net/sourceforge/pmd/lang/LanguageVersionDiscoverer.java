/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.util.AssertionUtil;
import net.sourceforge.pmd.util.IteratorUtil;

/**
 * This class can discover the LanguageVersion of a source file. Further, every
 * Language has a default LanguageVersion, which can be temporarily overridden
 * here.
 */
public class LanguageVersionDiscoverer {

    private LanguageRegistry languageRegistry;
    private final Map<Language, LanguageVersion> languageToLanguageVersion = new HashMap<>();
    private final List<LanguageFilePattern> languageFilePatterns = new ArrayList<>();
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
     * Add a pattern that will be matched to a language. If the language is unknown,
     * return false. File patterns are matched in the reverse order they were added.
     * This behavior allows later patterns to take precedence over already added patterns.
     * The first match stops the search. If the language is unknown (not loaded), the
     * search is stopped anyway.
     *
     * @param pattern    A pattern
     * @param languageId A language ID
     *
     * @return True if the language is known, false otherwise.
     */
    public boolean addLanguageFilePattern(Pattern pattern, String languageId) {
        languageFilePatterns.add(new LanguageFilePattern(pattern, languageId));
        return languageRegistry.getLanguageById(languageId) != null;
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
     * @param fileName
     *            The file name.
     * @return The Languages for the source file, may be empty.
     */
    public List<Language> getLanguagesForFile(String fileName) {
        String extension = getExtension(fileName);

        String langId = matchLanguageFilePatterns(fileName);
        if (langId != null) {
            // matched one of the patterns
            Language lang = languageRegistry.getLanguageById(langId);
            if (lang != null) {
                return Collections.singletonList(lang);
            } else {
                // language was not loaded, file is ignored.
                return Collections.emptyList();
            }
        }

        return languageRegistry.getLanguages().stream()
                               .filter(it -> it.hasExtension(extension))
                               .collect(Collectors.toList());
    }

    private @Nullable String matchLanguageFilePatterns(String fileName) {
        // match patterns from most recent to most ancient
        for (LanguageFilePattern pat : IteratorUtil.asReversed(languageFilePatterns)) {
            if (pat.matches(fileName)) {
                return pat.languageId;
            }
        }
        return null;
    }

    // Get the extensions from a file
    private String getExtension(String fileName) {
        return StringUtils.substringAfterLast(fileName, ".");
    }

    /**
     * Make it so that the only extensions that are considered are those
     * of the given language. This is different from {@link #setForcedVersion(LanguageVersion)}.
     * because that one will assign the given language version to all files
     * irrespective of extension. This method, on the other hand, will
     * ignore files that do not match the given language.
     *
     * @param lang A language
     */
    public void onlyRecognizeLanguages(LanguageRegistry lang) {
        this.languageRegistry = Objects.requireNonNull(lang);
    }

    @Override
    public String toString() {
        return "LanguageVersionDiscoverer(" + languageRegistry
                + (forcedVersion != null ? ",forcedVersion=" + forcedVersion : "")
                + ")";
    }

    static final class LanguageFilePattern {
        private final Pattern pat;
        private final String languageId;

        LanguageFilePattern(Pattern pat, String languageId) {
            this.pat = pat;
            this.languageId = languageId;
        }

        public boolean matches(String filename) {
            return pat.matcher(filename).matches();
        }
    }
}
