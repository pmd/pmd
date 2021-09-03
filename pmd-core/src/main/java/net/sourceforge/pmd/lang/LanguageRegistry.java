/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.TreeSet;

/**
 * Provides access to the registered PMD languages. These are found
 * from the classpath of the {@link ClassLoader} of this class.
 */
public final class LanguageRegistry {

    private static final LanguageRegistry INSTANCE = new LanguageRegistry();

    private final Map<String, Language> languagesByName;
    private final Map<String, Language> languagesByTerseName;
    private final Set<Language> languages;

    private LanguageRegistry() {
        // sort languages by terse name. Avoiding differences in the order of languages
        // across JVM versions / OS.
        Set<Language> sortedLangs = new TreeSet<>((o1, o2) -> o1.getTerseName().compareToIgnoreCase(o2.getTerseName()));
        // Use current class' classloader instead of the threads context classloader, see https://github.com/pmd/pmd/issues/1377
        ServiceLoader<Language> languageLoader = ServiceLoader.load(Language.class, getClass().getClassLoader());
        Iterator<Language> iterator = languageLoader.iterator();

        while (true) {
            // this loop is weird, but both hasNext and next may throw ServiceConfigurationError,
            // it's more robust that way
            try {
                if (iterator.hasNext()) {
                    Language language = iterator.next();
                    sortedLangs.add(language);
                } else {
                    break;
                }
            } catch (UnsupportedClassVersionError | ServiceConfigurationError e) {
                // Some languages require java8 and are therefore only available
                // if java8 or later is used as runtime.
                System.err.println("Ignoring language for PMD: " + e.toString());
            }
        }

        languages = Collections.unmodifiableSet(new LinkedHashSet<>(sortedLangs));

        // using a linked hash map to maintain insertion order
        // TODO there may be languages with duplicate names
        Map<String, Language> byName = new LinkedHashMap<>();
        Map<String, Language> byTerseName = new LinkedHashMap<>();
        for (Language language : sortedLangs) {
            byName.put(language.getName(), language);
            byTerseName.put(language.getTerseName(), language);
        }
        languagesByName = Collections.unmodifiableMap(byName);
        languagesByTerseName = Collections.unmodifiableMap(byTerseName);
    }

    /**
     * @deprecated Use the static methods instead, will be made private
     */
    @Deprecated
    public static LanguageRegistry getInstance() {
        return INSTANCE;
    }

    /**
     * Returns a set of all the known languages. The ordering of the languages
     * is by terse name.
     */
    public static Set<Language> getLanguages() {
        return getInstance().languages;
    }

    /**
     * Returns a language from its {@linkplain Language#getName() full name}
     * (eg {@code "Java"}). This is case sensitive.
     *
     * @param languageName Language name
     *
     * @return A language, or null if the name is unknown
     */
    public static Language getLanguage(String languageName) {
        return getInstance().languagesByName.get(languageName);
    }

    /**
     * Returns a "default language" known to the service loader. This
     * is the Java language if available, otherwise an arbitrary one.
     * If no languages are loaded, returns null.
     *
     * @return A language, or null if the name is unknown
     */
    public static Language getDefaultLanguage() {
        Language defaultLanguage = getLanguage("Java");
        if (defaultLanguage == null) {
            Collection<Language> allLanguages = getInstance().languagesByName.values();
            if (!allLanguages.isEmpty()) {
                defaultLanguage = allLanguages.iterator().next();
            }
        }
        return defaultLanguage;
    }

    /**
     * Returns a language from its {@linkplain Language#getTerseName() terse name}
     * (eg {@code "java"}). This is case sensitive.
     *
     * @param terseName Language terse name
     *
     * @return A language, or null if the name is unknown
     */
    public static Language findLanguageByTerseName(String terseName) {
        return getInstance().languagesByTerseName.get(terseName);
    }

    /**
     * Returns all languages that support the given extension.
     *
     * @param extensionWithoutDot A file extension (without '.' prefix)
     */
    public static List<Language> findByExtension(String extensionWithoutDot) {
        List<Language> languages = new ArrayList<>();
        for (Language language : getLanguages()) {
            if (language.hasExtension(extensionWithoutDot)) {
                languages.add(language);
            }
        }
        return languages;
    }

}
