/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

/**
 * A registry of languages, which are dynamically loaded through a
 * {@link ServiceLoader}. Language registries have a lifecycle:
 * they're initially created using one of the factory methods, and
 * need to be closed when the analysis is done. Languages of different
 * language registries are different instances, and can be parameterized
 * differently (todo). This allows Language instances to be configured
 * independently, or to use heavyweight resources like external language
 * servers and be sure those will be reclaimed.
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
    public static final LanguageRegistry STATIC = LanguageLoader.DEFAULT.load();

    private final Map<String, Language> languagesByName;
    private final Map<String, Language> languagesByTerseName;
    private final Set<Language> languages;


    LanguageRegistry(Set<Language> sortedLanguages) {
        this.languages = sortedLanguages;

        // using a linked hash map to maintain insertion order
        Map<String, Language> byName = new LinkedHashMap<>();
        Map<String, Language> byTerseName = new LinkedHashMap<>();
        for (Language language : sortedLanguages) {
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
