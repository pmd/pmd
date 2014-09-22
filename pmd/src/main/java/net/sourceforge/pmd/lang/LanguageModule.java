package net.sourceforge.pmd.lang;

import java.util.List;

/**
 * Created by christoferdutz on 20.09.14.
 */
public interface LanguageModule {

    static final String LANGUAGE_MODULES_CLASS_NAMES_PROPERTY = "languageModulesClassNames";

    String getName();
    String getShortName();
    String getTerseName();
    List<String> getExtensions();
    boolean hasExtension(String extension);
    Class<?> getRuleChainVisitorClass();
    List<LanguageVersionModule> getVersions();
    boolean hasVersion(String version);
    LanguageVersionModule getVersion(String version);
    LanguageVersionModule getDefaultVersion();

}
