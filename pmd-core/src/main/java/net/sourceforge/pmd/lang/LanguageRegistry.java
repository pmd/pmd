/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.internal.LanguageServiceBase;

/**
 * Provides access to the registered PMD languages. These are found
 * from the classpath of the {@link ClassLoader} of this class.
 */
public final class LanguageRegistry extends LanguageServiceBase<Language> {

    // sort languages by name. Avoiding differences in the order of languages
    // across JVM versions / OS.
    private static final Comparator<Language> LANGUAGE_COMPARATOR = new Comparator<Language>() {
        @Override
        public int compare(Language o1, Language o2) {
            return o1.getTerseName().compareToIgnoreCase(o2.getTerseName());
        }
    };

    private static final NameExtractor<Language> NAME_EXTRACTOR = new NameExtractor<Language>() {
        @Override
        public String getName(Language language) {
            return language.getName();
        }
    };

    private static final NameExtractor<Language> TERSE_NAME_EXTRACTOR = new NameExtractor<Language>() {
        @Override
        public String getName(Language language) {
            return language.getTerseName();
        }
    };

    // Important: the INSTANCE needs to be defined *after* LANGUAGE_COMPARATOR and *NAME_EXTRACTOR
    // as these are needed in the constructor.
    private static final LanguageRegistry INSTANCE = new LanguageRegistry();

    private LanguageRegistry() {
        super(Language.class, LANGUAGE_COMPARATOR, NAME_EXTRACTOR, TERSE_NAME_EXTRACTOR);
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
        return INSTANCE.languages;
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
        return INSTANCE.languagesByName.get(languageName);
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
        return INSTANCE.languagesByTerseName.get(terseName);
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
