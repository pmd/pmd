package net.sourceforge.pmd.lang;

import net.sourceforge.pmd.lang.java.JavaLanguageModule;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * Created by christoferdutz on 20.09.14.
 */
public class LanguageRegistry {

    private static LanguageRegistry instance;

    private Map<String, LanguageModule> languages;

    private LanguageRegistry() {
        languages = new HashMap<String, LanguageModule>();

        try {
            Enumeration<URL> languageModulesProperties = LanguageRegistry.class.getClassLoader().getResources("META-INF/pmd-language.properties");
            while (languageModulesProperties.hasMoreElements()) {
                URL curLanguageModulePropertiesUrl = languageModulesProperties.nextElement();
                Properties curLanguageModuleProperties = new Properties();
                curLanguageModuleProperties.load(curLanguageModulePropertiesUrl.openStream());
                String languageModulesImplClassNames = curLanguageModuleProperties.getProperty(LanguageModule.LANGUAGE_MODULES_CLASS_NAMES_PROPERTY);
                if(languageModulesImplClassNames != null) {
                    String[] languageModuleImplClassNames = languageModulesImplClassNames.split(";");
                    for(String languageModuleImplClassName : languageModuleImplClassNames) {
                        Class<?> languageModuleImplClass = LanguageRegistry.class.getClassLoader().loadClass(languageModuleImplClassName);
                        LanguageModule languageModule = (LanguageModule) languageModuleImplClass.newInstance();
                        languages.put(languageModule.getName(), languageModule);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    protected static LanguageRegistry getInstance() {
        if(instance == null) {
            instance = new LanguageRegistry();
        }
        return instance;
    }

    public static Collection<LanguageModule> getLanguages() {
        return getInstance().languages.values();
    }

    public static LanguageModule getLanguage(String languageName) {
        return getInstance().languages.get(languageName);
    }

    public static LanguageModule getDefaultLanguage() {
        return getLanguage(JavaLanguageModule.NAME);
    }

    public static LanguageModule findLanguageByTerseName(String terseName) {
        for (LanguageModule language : getInstance().languages.values()) {
            if (language.getTerseName().equals(terseName)) {
                return language;
            }
        }
        return null;
    }

    public static LanguageVersionModule findLanguageVersionByTerseName(String terseName) {
        String version = null;
        if(terseName.contains(" ")) {
            version = terseName.substring(terseName.lastIndexOf(" ") + 1);
            terseName = terseName.substring(0, terseName.lastIndexOf(" "));
        }
        LanguageModule language = findLanguageByTerseName(terseName);
        if(language != null) {
            if(version == null) {
                return language.getDefaultVersion();
            } else {
                return language.getVersion(version);
            }
        }
        return null;
    }

    public static List<LanguageModule> findByExtension(String extension) {
        List<LanguageModule> languages = new ArrayList<LanguageModule>();
        for (LanguageModule language : getInstance().languages.values()) {
            if (language.hasExtension(extension)) {
                languages.add(language);
            }
        }
        return languages;
    }

    public static List<LanguageVersionModule> findAllVersions() {
        List<LanguageVersionModule> versions = new ArrayList<LanguageVersionModule>();
        for(LanguageModule language : getLanguages()) {
            for(LanguageVersionModule languageVersion : language.getVersions()) {
                versions.add(languageVersion);
            }
        }
        return versions;
    }

    /**
     * A utility method to find the Languages which have Rule support.
     * @return A List of Languages with Rule support.
     */
    public static List<LanguageModule> findWithRuleSupport() {
        List<LanguageModule> languages = new ArrayList<LanguageModule>();
        for (LanguageModule language : getInstance().languages.values()) {
            if (language.getRuleChainVisitorClass() != null) {
                languages.add(language);
            }
        }
        return languages;
    }

    public static String commaSeparatedTerseNamesForLanguage(List<LanguageModule> languages) {
        StringBuilder builder = new StringBuilder();
        for (LanguageModule language : languages) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(language.getTerseName());
        }
        return builder.toString();
    }

    public static String commaSeparatedTerseNamesForLanguageVersion(List<LanguageVersionModule> languageVersions) {
        if (languageVersions == null || languageVersions.isEmpty()) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        builder.append(languageVersions.get(0).getTerseName());
        for (int i=1; i<languageVersions.size(); i++) {
            builder.append(", ").append(languageVersions.get(i).getTerseName());
        }
        return builder.toString();
    }

}
