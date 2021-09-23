/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.Comparator;
import java.util.Locale;
import java.util.Properties;

import net.sourceforge.pmd.internal.LanguageServiceBase;

public final class LanguageFactory extends LanguageServiceBase<Language> {

    public static final String EXTENSION = "extension";
    public static final String BY_EXTENSION = "by_extension";

    private static final Comparator<Language> LANGUAGE_COMPARATOR = new Comparator<Language>() {
        @Override
        public int compare(Language o1, Language o2) {
            return o1.getTerseName().compareToIgnoreCase(o2.getTerseName());
        }
    };

    private static final NameExtractor<Language> NAME_EXTRACTOR = new NameExtractor<Language>() {
        @Override
        public String getName(Language language) {
            return language.getName().toLowerCase(Locale.ROOT);
        }
    };

    private static final NameExtractor<Language> TERSE_NAME_EXTRACTOR = new NameExtractor<Language>() {
        @Override
        public String getName(Language language) {
            return language.getTerseName().toLowerCase(Locale.ROOT);
        }
    };

    // Important: the "instance" needs to be defined *after* LANGUAGE_COMPARATOR and *NAME_EXTRACTOR
    // as these are needed in the constructor.
    private static final LanguageFactory INSTANCE = new LanguageFactory();

    public static String[] supportedLanguages;

    static {
        supportedLanguages = INSTANCE.languagesByTerseName.keySet().toArray(new String[INSTANCE.languages.size()]);
    }

    private LanguageFactory() {
        super(Language.class, LANGUAGE_COMPARATOR, NAME_EXTRACTOR, TERSE_NAME_EXTRACTOR);
    }

    public static Language createLanguage(String language) {
        return createLanguage(language, new Properties());
    }

    public static Language createLanguage(String language, Properties properties) {
        Language implementation;
        if (BY_EXTENSION.equals(language)) {
            implementation = INSTANCE.getLanguageByExtension(properties.getProperty(EXTENSION));
        } else {
            implementation = INSTANCE.languagesByTerseName.get(INSTANCE.languageAliases(language).toLowerCase(Locale.ROOT));
        }
        if (implementation == null) {
            // No proper implementation
            // FIXME: We should log a warning, shouldn't we ?
            implementation = new AnyLanguage(language);
        }
        implementation.setProperties(properties);
        return implementation;
    }

    private String languageAliases(String language) {
        // CPP and C language share the same parser
        if ("c".equals(language)) {
            return "cpp";
        }
        return language;
    }

    private Language getLanguageByExtension(String extension) {
        Language result = null;

        for (Language language : languages) {
            if (language.getExtensions().contains(extension)) {
                result = language;
                break;
            }
        }
        return result;
    }
}
