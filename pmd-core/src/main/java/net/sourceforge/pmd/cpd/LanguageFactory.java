/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ServiceLoader;

public final class LanguageFactory {

    public static final String EXTENSION = "extension";
    public static final String BY_EXTENSION = "by_extension";

    private static LanguageFactory instance = new LanguageFactory();

    public static String[] supportedLanguages;

    static {
        supportedLanguages = instance.languages.keySet().toArray(new String[instance.languages.size()]);
    }

    private Map<String, Language> languages = new HashMap<>();

    private LanguageFactory() {
        List<Language> languagesList = new ArrayList<>();
        // Use current class' classloader instead of the threads context classloader, see https://github.com/pmd/pmd/issues/1788
        ServiceLoader<Language> languageLoader = ServiceLoader.load(Language.class, getClass().getClassLoader());
        Iterator<Language> iterator = languageLoader.iterator();
        while (iterator.hasNext()) {
            try {
                Language language = iterator.next();
                languagesList.add(language);
            } catch (UnsupportedClassVersionError e) {
                // Some languages require java8 and are therefore only available
                // if java8 or later is used as runtime.
                System.err.println("Ignoring language for CPD: " + e.toString());
            }
        }

        // sort languages by terse name. Avoiding differences in the order of languages
        // across JVM versions / OS.
        Collections.sort(languagesList, new Comparator<Language>() {
            @Override
            public int compare(Language o1, Language o2) {
                return o1.getTerseName().compareToIgnoreCase(o2.getTerseName());
            }
        });

        // using a linked hash map to maintain insertion order
        languages = new LinkedHashMap<>();
        for (Language language : languagesList) {
            languages.put(language.getTerseName().toLowerCase(Locale.ROOT), language);
        }

    }

    public static Language createLanguage(String language) {
        return createLanguage(language, new Properties());
    }

    public static Language createLanguage(String language, Properties properties) {
        Language implementation;
        if (BY_EXTENSION.equals(language)) {
            implementation = instance.getLanguageByExtension(properties.getProperty(EXTENSION));
        } else {
            implementation = instance.languages.get(instance.languageAliases(language).toLowerCase(Locale.ROOT));
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

        for (Language language : languages.values()) {
            if (language.getExtensions().contains(extension)) {
                result = language;
                break;
            }
        }
        return result;
    }
}
