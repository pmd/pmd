/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import java.util.ArrayList;
import java.util.Comparator;
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

import net.sourceforge.pmd.util.CollectionUtil;

/**
 * A set of languages with convenient methods. In the PMD CLI, languages
 * are loaded from the classloader of this class. These are in the registry
 * {@link #PMD}. You can otherwise create different registries with different
 * languages, eg filter some out.
 */
public final class LanguageRegistry implements Iterable<Language> {

    /**
     * Contains the languages that support PMD and are found on the classpath
     * of the classloader of this class. This can be used as a "default" registry.
     */
    public static final LanguageRegistry PMD = loadLanguages(LanguageRegistry.class.getClassLoader());

    private final Set<Language> languages;

    private final Map<String, Language> languagesById;
    private final Map<String, Language> languagesByFullName;

    public LanguageRegistry(Set<Language> languages) {
        this.languages = languages.stream()
                                  .sorted(Comparator.comparing(Language::getTerseName, String::compareToIgnoreCase))
                                  .collect(CollectionUtil.toUnmodifiableSet());
        this.languagesById = CollectionUtil.associateBy(languages, Language::getTerseName);
        this.languagesByFullName = CollectionUtil.associateBy(languages, Language::getName);
    }

    @Override
    public @NonNull Iterator<Language> iterator() {
        return languages.iterator();
    }


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
                System.err.println("Ignoring language for PMD: " + e);
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
     * @deprecated Use {@link #getLanguageByFullName(String)}
     */
    @Deprecated
    public Language getLanguage(String languageName) {
        return languagesByFullName.get(languageName);
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
     * Returns a "default language" known to the service loader. This
     * is the Java language if available, otherwise an arbitrary one.
     * If no languages are loaded, returns null.
     *
     * @return A language, or null if the name is unknown
     */
    public static @Nullable Language getDefaultLanguage() {
        return null;
    }

    /**
     * Returns a language from its {@linkplain Language#getTerseName() terse name}
     * (eg {@code "java"}). This is case sensitive.
     *
     * @param terseName Language terse name
     *
     * @return A language, or null if the name is unknown
     *
     * @deprecated Use {@link #getLanguageById(String)}.
     */
    @Deprecated
    public @Nullable Language findLanguageByTerseName(@Nullable String terseName) {
        return languagesById.get(terseName);
    }

    /**
     * Returns all languages that support the given extension.
     *
     * @param extensionWithoutDot A file extension (without '.' prefix)
     */
    public List<Language> findByExtension(String extensionWithoutDot) {
        List<Language> languages = new ArrayList<>();
        for (Language language : getLanguages()) {
            if (language.hasExtension(extensionWithoutDot)) {
                languages.add(language);
            }
        }
        return languages;
    }

    public @NonNull String commaSeparatedList(Function<Language, String> getter) {
        return getLanguages().stream().map(getter).collect(Collectors.joining(", "));
    }


}
