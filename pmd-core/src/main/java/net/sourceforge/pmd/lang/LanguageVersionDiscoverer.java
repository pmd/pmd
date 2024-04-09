/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
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

        // Add pattern to recognize POM. This can be overridden by patterns added later. POM defaults to XML if not loaded.
        addLanguageFilePattern(LanguageFilePattern.ofRegex(Pattern.compile("(.*/)?pom\\.xml$"), "pom", "xml"));
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
     * @param pattern            A pattern
     */
    public void addLanguageFilePattern(LanguageFilePattern pattern) {
        AssertionUtil.requireParamNotNull("pattern", pattern);
        languageFilePatterns.add(pattern);
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

        LanguageFilePattern pat = matchLanguageFilePatterns(fileName);
        if (pat != null) {
            // matched one of the patterns
            for (String langId : pat.languageIds) {
                Language lang = languageRegistry.getLanguageById(langId);
                if (lang != null) {
                    return Collections.singletonList(lang);
                }
            }
            // language was not loaded, file is ignored.
            return Collections.emptyList();
        }

        return languageRegistry.getLanguages().stream()
                               .filter(it -> it.hasExtension(extension))
                               .collect(Collectors.toList());

    }

    private @Nullable LanguageFilePattern matchLanguageFilePatterns(String fileName) {
        // match patterns from most recent to most ancient
        for (LanguageFilePattern pat : IteratorUtil.asReversed(languageFilePatterns)) {
            if (pat.matches(fileName)) {
                return pat;
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

    /**
     * A pattern that matches file names to assign them a language.
     *
     * @see net.sourceforge.pmd.AbstractConfiguration#addLanguageFilePattern(LanguageFilePattern)
     * @see #addLanguageFilePattern(LanguageFilePattern)
     */
    public static final class LanguageFilePattern {
        private final Predicate<? super String> matcher;
        private final List<String> languageIds;

        private LanguageFilePattern(Predicate<? super String> matcher, List<String> languageIds) {
            this.matcher = Objects.requireNonNull(matcher);
            this.languageIds = Objects.requireNonNull(languageIds);
        }

        boolean matches(String path) {
            return matcher.test(path);
        }

        /** Language ID of the first language to be loaded if this pattern matches. */
        public String getLanguageid() {
            return languageIds.get(0);
        }

        /**
         * Make a pattern from language IDs that will be assigned to the file if its name matches the regex.
         *
         * @param pattern      Regex
         * @param firstLang    First language
         * @param defaultLangs Other language that will be tried if the first language cannot be loaded
         *
         * @return A new pattern
         */
        public static LanguageFilePattern ofRegex(Pattern pattern, String firstLang, String... defaultLangs) {
            Predicate<String> pred = path -> {
                // make platform independent
                path = path.replace('\\', '/');
                return pattern.matcher(path).matches();
            };
            return new LanguageFilePattern(pred, listOf(firstLang, defaultLangs));
        }
    }
}
