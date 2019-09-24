/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by christoferdutz on 20.09.14.
 */
public final class LanguageRegistry {

    private static LanguageRegistry instance = new LanguageRegistry();

    private Map<String, Language> languages;

    private LanguageRegistry() {
        List<Language> languagesList = new ArrayList<>();
        // Use current class' classloader instead of the threads context classloader, see https://github.com/pmd/pmd/issues/1377
        ServiceLoader<Language> languageLoader = ServiceLoader.load(Language.class, getClass().getClassLoader());
        for (Language language : languageLoader) {
            try {
                languagesList.add(language);
            } catch (UnsupportedClassVersionError e) {
                // Some languages require java8 and are therefore only available
                // if java8 or later is used as runtime.
                System.err.println("Ignoring language for PMD: " + e.toString());
            }
        }

        // sort languages by terse name. Avoiding differences in the order of languages
        // across JVM versions / OS.
        languagesList.sort((o1, o2) -> o1.getTerseName().compareToIgnoreCase(o2.getTerseName()));

        // using a linked hash map to maintain insertion order
        languages = new LinkedHashMap<>();
        for (Language language : languagesList) {
            languages.put(language.getName(), language);
        }
    }

    public static LanguageRegistry getInstance() {
        return instance;
    }

    public static Set<Language> getLanguages() {
        return new LinkedHashSet<>(getInstance().languages.values());
    }

    public static Language getLanguage(String languageName) {
        return getInstance().languages.get(languageName);
    }

    public static Language getDefaultLanguage() {
        Language defaultLanguage = getLanguage("Java");
        if (defaultLanguage == null) {
            Collection<Language> allLanguages = getInstance().languages.values();
            if (!allLanguages.isEmpty()) {
                defaultLanguage = allLanguages.iterator().next();
            }
        }
        return defaultLanguage;
    }

    public static Language findLanguageByTerseName(String terseName) {
        for (Language language : getInstance().languages.values()) {
            if (language.getTerseName().equals(terseName)) {
                return language;
            }
        }
        return null;
    }

    public static LanguageVersion findLanguageVersionByTerseName(String terseNameAndVersion) {
        String version;
        String terseName;
        if (terseNameAndVersion.contains(" ")) {
            version = StringUtils.trimToNull(terseNameAndVersion.substring(terseNameAndVersion.lastIndexOf(' ') + 1));
            terseName = terseNameAndVersion.substring(0, terseNameAndVersion.lastIndexOf(' '));
        } else {
            version = null;
            terseName = terseNameAndVersion;
        }
        Language language = findLanguageByTerseName(terseName);
        if (language != null) {
            if (version == null) {
                return language.getDefaultVersion();
            } else {
                return language.getVersion(version);
            }
        }
        return null;
    }

    public static List<Language> findByExtension(String extension) {
        List<Language> languages = new ArrayList<>();
        for (Language language : getInstance().languages.values()) {
            if (language.hasExtension(extension)) {
                languages.add(language);
            }
        }
        return languages;
    }

}
