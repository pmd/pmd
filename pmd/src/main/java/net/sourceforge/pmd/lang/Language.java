package net.sourceforge.pmd.lang;

import java.util.List;

/**
 * Created by christoferdutz on 20.09.14.
 */
public interface Language {

    static final String LANGUAGE_MODULES_CLASS_NAMES_PROPERTY = "languageModulesClassNames";

    String getName();
    String getShortName();
    String getTerseName();
    List<String> getExtensions();
    boolean hasExtension(String extension);
    Class<?> getRuleChainVisitorClass();
    List<LanguageVersion> getVersions();
    boolean hasVersion(String version);
    LanguageVersion getVersion(String version);
    LanguageVersion getDefaultVersion();

}
