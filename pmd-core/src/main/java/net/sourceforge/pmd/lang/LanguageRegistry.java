/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.annotation.DeprecatedUntil700;
import net.sourceforge.pmd.util.CollectionUtil;

/**
 * A set of languages with convenient methods. In the PMD CLI, languages
 * are loaded from the classloader of this class. These are in the registry
 * {@link #PMD}. You can otherwise create different registries with different
 * languages, eg filter some out.
 */
public final class LanguageRegistry implements Iterable<Language> {

    private static final Logger LOG = LoggerFactory.getLogger(LanguageRegistry.class);

    /**
     * Contains the languages that support PMD and are found on the classpath
     * of the classloader of this class. This can be used as a "default" registry.
     */
    public static final LanguageRegistry PMD = loadLanguages(LanguageRegistry.class.getClassLoader());

    private final Set<Language> languages;

    private final Map<String, Language> languagesById;
    private final Map<String, Language> languagesByFullName;

    /**
     * Create a new registry that contains the given set of languages.
     * @throws NullPointerException If the parameter is null
     */
    public LanguageRegistry(Set<Language> languages) {
        this.languages = languages.stream()
                                  .sorted(Comparator.comparing(Language::getTerseName, String::compareToIgnoreCase))
                                  .collect(CollectionUtil.toUnmodifiableSet());
        this.languagesById = CollectionUtil.associateBy(languages, Language::getTerseName);
        this.languagesByFullName = CollectionUtil.associateBy(languages, Language::getName);
    }

    /**
     * Creates a language registry containing a single language. Note
     * that this may be inconvertible to a {@link LanguageProcessorRegistry}
     * if the language depends on other languages.
     */
    public static LanguageRegistry singleton(Language l) {
        return new LanguageRegistry(Collections.singleton(l));
    }

    /**
     * Creates a language registry containing the given language and
     * its dependencies, fetched from this language registry or the
     * parameter.
     *
     * @throws IllegalStateException If dependencies cannot be fulfilled.
     */
    public LanguageRegistry getDependenciesOf(Language lang) {
        Set<Language> result = new HashSet<>();
        result.add(lang);
        addDepsOrThrow(lang, result);
        return new LanguageRegistry(result);
    }

    private void addDepsOrThrow(Language l, Set<Language> languages) {
        for (String depId : l.getDependencies()) {
            Language dep = getLanguageById(depId);
            if (dep == null) {
                throw new IllegalStateException(
                    "Cannot find language " + depId + " in " + this);
            }
            if (languages.add(dep)) {
                addDepsOrThrow(dep, languages);
            }
        }
    }

    @Override
    public @NonNull Iterator<Language> iterator() {
        return languages.iterator();
    }

    /**
     * Create a new registry by loading the languages registered via {@link ServiceLoader}
     * on the classpath of the given classloader.
     *
     * @param classLoader A classloader
     */
    public static @NonNull LanguageRegistry loadLanguages(ClassLoader classLoader) {
        // sort languages by terse name. Avoiding differences in the order of languages
        // across JVM versions / OS.
        Set<Language> languages = new TreeSet<>(Comparator.comparing(Language::getTerseName, String::compareToIgnoreCase));
        ServiceLoader<Language> languageLoader = ServiceLoader.load(Language.class, classLoader);
        Iterator<Language> iterator = languageLoader.iterator();
        while (true) {
            // this loop is weird, but both hasNext and next may throw ServiceConfigurationError,
            // it's more robust that way
            try {
                if (iterator.hasNext()) {
                    Language language = iterator.next();
                    languages.add(language);
                } else {
                    break;
                }
            } catch (UnsupportedClassVersionError | ServiceConfigurationError e) {
                // Some languages require java8 and are therefore only available
                // if java8 or later is used as runtime.
                LOG.warn("Cannot load PMD language, ignored", e);
            }
        }
        return new LanguageRegistry(languages);
    }

    /**
     * Returns a set of all the known languages. The ordering of the languages
     * is by terse name.
     */
    public Set<Language> getLanguages() {
        return languages;
    }

    /**
     * Returns a language from its {@linkplain Language#getName() full name}
     * (eg {@code "Java"}). This is case sensitive.
     *
     * @param languageName Language name
     *
     * @return A language, or null if the name is unknown
     *
     * @deprecated Use {@link #getLanguageByFullName(String) PMD.getLanguageByFullName}
     */
    @Deprecated
    @DeprecatedUntil700
    public static Language getLanguage(String languageName) {
        return PMD.getLanguageByFullName(languageName);
    }

    /**
     * Returns a language from its {@linkplain Language#getId() ID}
     * (eg {@code "java"}). This is case-sensitive.
     *
     * @param langId Language ID
     *
     * @return A language, or null if the name is unknown, or the parameter is null
     */
    public @Nullable Language getLanguageById(@Nullable String langId) {
        return languagesById.get(langId);
    }

    /**
     * Returns a language version from its {@linkplain Language#getId() language ID}
     * (eg {@code "java"}). This is case-sensitive.
     *
     * @param langId  Language ID
     * @param version Version ID
     *
     * @return A language, or null if the name is unknown
     */
    public @Nullable LanguageVersion getLanguageVersionById(@Nullable String langId, @Nullable String version) {
        Language lang = languagesById.get(langId);
        if (lang == null) {
            return null;
        }
        return version == null ? lang.getDefaultVersion()
                               : lang.getVersion(version);
    }

    /**
     * Returns a language from its {@linkplain Language#getName() full name}
     * (eg {@code "Java"}). This is case sensitive.
     *
     * @param languageName Language name
     *
     * @return A language, or null if the name is unknown
     */
    public @Nullable Language getLanguageByFullName(String languageName) {
        return languagesByFullName.get(languageName);
    }

    /**
     * Returns a language from its {@linkplain Language#getTerseName() terse name}
     * (eg {@code "java"}). This is case sensitive.
     *
     * @param terseName Language terse name
     *
     * @return A language, or null if the name is unknown
     *
     * @deprecated Use {@link #getLanguageById(String) PMD.getLanguageById}.
     */
    @Deprecated
    @DeprecatedUntil700
    public static @Nullable Language findLanguageByTerseName(@Nullable String terseName) {
        return PMD.getLanguageById(terseName);
    }

    /**
     * Returns all languages that support the given extension.
     *
     * @param extensionWithoutDot A file extension (without '.' prefix)
     *
     * @deprecated Not replaced, extension will be extended to match full name in PMD 7.
     */
    @Deprecated
    @DeprecatedUntil700
    public static List<Language> findByExtension(String extensionWithoutDot) {
        List<Language> languages = new ArrayList<>();
        for (Language language : PMD.getLanguages()) {
            if (language.hasExtension(extensionWithoutDot)) {
                languages.add(language);
            }
        }
        return languages;
    }

    /**
     * Formats the set of languages with the given formatter, sort and
     * join everything with commas. Convenience method.
     */
    public @NonNull String commaSeparatedList(Function<? super Language, String> languageToString) {
        return getLanguages().stream().map(languageToString).sorted().collect(Collectors.joining(", "));
    }

    @Override
    public String toString() {
        return "LanguageRegistry(" + commaSeparatedList(Language::getId) + ")";
    }
}
