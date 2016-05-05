/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * Created by christoferdutz on 20.09.14.
 */
public final class LanguageRegistry {

    private static LanguageRegistry instance = new LanguageRegistry();

    private Map<String, Language> languages;

    private LanguageRegistry() {
        languages = new LinkedHashMap<>();
        ServiceLoader<Language> languageLoader = ServiceLoader.load(Language.class);
        Iterator<Language> iterator = languageLoader.iterator();
        while (iterator.hasNext()) {
            try {
                Language language = iterator.next();
                languages.put(language.getName(), language);
            } catch (UnsupportedClassVersionError e) {
                // Some languages require java8 and are therefore only available
                // if java8 or later is used as runtime.
                System.err.println("Ignoring language for PMD: " + e.toString());
            }
        }
    }

    public static LanguageRegistry getInstance() {
        return instance;
    }

    public static Collection<Language> getLanguages() {
        return getInstance().languages.values();
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
            version = terseNameAndVersion.substring(terseNameAndVersion.lastIndexOf(' ') + 1);
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

    public static List<LanguageVersion> findAllVersions() {
        List<LanguageVersion> versions = new ArrayList<>();
        for (Language language : getLanguages()) {
            for (LanguageVersion languageVersion : language.getVersions()) {
                versions.add(languageVersion);
            }
        }
        return versions;
    }

    /**
     * A utility method to find the Languages which have Rule support.
     * 
     * @return A List of Languages with Rule support.
     */
    public static List<Language> findWithRuleSupport() {
        List<Language> languages = new ArrayList<>();
        for (Language language : getInstance().languages.values()) {
            if (language.getRuleChainVisitorClass() != null) {
                languages.add(language);
            }
        }
        return languages;
    }

    public static String commaSeparatedTerseNamesForLanguage(List<Language> languages) {
        StringBuilder builder = new StringBuilder();
        for (Language language : languages) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(language.getTerseName());
        }
        return builder.toString();
    }

    public static String commaSeparatedTerseNamesForLanguageVersion(List<LanguageVersion> languageVersions) {
        if (languageVersions == null || languageVersions.isEmpty()) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        builder.append(languageVersions.get(0).getTerseName());
        for (int i = 1; i < languageVersions.size(); i++) {
            builder.append(", ").append(languageVersions.get(i).getTerseName());
        }
        return builder.toString();
    }

}
