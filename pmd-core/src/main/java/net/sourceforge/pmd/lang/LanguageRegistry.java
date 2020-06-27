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
 * A registry of languages, which are dynamically loaded through a
 * {@link ServiceLoader}.
 */
public final class LanguageRegistry implements AutoCloseable {

    /**
     * The "static" language registry instance, which uses the classloader
     * of this class to load modules. It uses default language property values.
     * This is only provided for compatibility, as language registries should be
     * closed after usage.
     */
    @Deprecated
    // @DeprecatedUntil700
    public static final LanguageRegistry STATIC = new LanguageRegistry(LanguageRegistry.class.getClassLoader());

    private final Map<String, Language> languagesByName;
    private final Map<String, Language> languagesByTerseName;
    private final Set<Language> languages;


    private LanguageRegistry(ClassLoader languageClassLoader) {
        // sort languages by terse name. Avoiding differences in the order of languages
        // across JVM versions / OS.
        Set<Language> sortedLangs = new TreeSet<>((o1, o2) -> o1.getTerseName().compareToIgnoreCase(o2.getTerseName()));
        // Use current class' classloader instead of the threads context classloader, see https://github.com/pmd/pmd/issues/1377
        ServiceLoader<Language> languageLoader = ServiceLoader.load(Language.class, languageClassLoader);
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

    @Override
    public void close() throws Exception {
        // Do nothing for now
        // Later we can make Language Closeable and close them here to drop resources if needed
        // This allows eg to shutdown a language server used by the language module
    }


    /**
     * Create a new language registry, using the given ClassLoader to
     * load PMD languages (through a {@link ServiceLoader}).
     *
     * @param classLoader Classloader to look for PMD modules to load
     *
     * @return A new language registry
     */
    public static LanguageRegistry fromClassLoader(ClassLoader classLoader) {
        return new LanguageRegistry(classLoader);
    }


    public static LanguageRegistry fromDefaultClassLoader() {
        return fromClassLoader(LanguageRegistry.class.getClassLoader());
    }

    /**
     * Returns the registered languages, as un unmodifiable set.
     */
    public Set<Language> getLanguages() {
        return languages;
    }

    /** Gets a language from its full name ({@link Language#getName()}). */
    public Language getLanguage(String languageName) {
        return languagesByName.get(languageName);
    }

    public Language getDefaultLanguage() {
        Language defaultLanguage = getLanguage("Java");
        if (defaultLanguage == null) {
            Collection<Language> allLanguages = languagesByName.values();
            if (!allLanguages.isEmpty()) {
                defaultLanguage = allLanguages.iterator().next();
            }
        }
        return defaultLanguage;
    }

    public Language findLanguageByTerseName(String terseName) {
        return languagesByTerseName.get(terseName);
    }

    public List<Language> findByExtension(String extension) {
        List<Language> languages = new ArrayList<>();
        for (Language language : getLanguages()) {
            if (language.hasExtension(extension)) {
                languages.add(language);
            }
        }
        return languages;
    }

}
