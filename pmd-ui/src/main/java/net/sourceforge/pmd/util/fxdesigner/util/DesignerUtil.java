/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.Parser;


/**
 * @author Clément Fournier
 * @since 6.0.0
 */
public class DesignerUtil {


    private static final Path PMD_SETTINGS_DIR = Paths.get(System.getProperty("user.home"), ".pmd");
    private static final File DESIGNER_SETTINGS_FILE = PMD_SETTINGS_DIR.resolve("pmd_new_designer.xml").toFile();


    private static LanguageVersion[] supportedLanguageVersions;
    private static Map<String, LanguageVersion> extensionsToLanguage;


    private DesignerUtil() {

    }


    private static Map<String, LanguageVersion> getExtensionsToLanguageMap() {
        Map<String, LanguageVersion> result = new HashMap<>();
        Arrays.stream(getSupportedLanguageVersions())
              .map(LanguageVersion::getLanguage)
              .distinct()
              .collect(Collectors.toMap(Language::getExtensions, Language::getDefaultVersion))
              .forEach((key, value) -> key.forEach(ext -> result.put(ext, value)));
        return result;
    }


    public static LanguageVersion getLanguageVersionFromExtension(String filename) {
        if (extensionsToLanguage == null) {
            extensionsToLanguage = getExtensionsToLanguageMap();
        }

        if (filename.indexOf('.') > 0) {
            String[] tokens = filename.split("\\.");
            return extensionsToLanguage.get(tokens[tokens.length - 1]);
        }

        return null;
    }


    /**
     * Name of the designer's settings file.
     *
     * @return The name
     */
    public static File getSettingsFile() {
        return DESIGNER_SETTINGS_FILE;
    }


    public static LanguageVersion[] getSupportedLanguageVersions() {
        if (supportedLanguageVersions == null) {
            List<LanguageVersion> languageVersions = new ArrayList<>();
            for (LanguageVersion languageVersion : LanguageRegistry.findAllVersions()) {
                LanguageVersionHandler languageVersionHandler = languageVersion.getLanguageVersionHandler();
                if (languageVersionHandler != null) {
                    Parser parser = languageVersionHandler.getParser(languageVersionHandler.getDefaultParserOptions());
                    if (parser != null && parser.canParse()) {
                        languageVersions.add(languageVersion);
                    }
                }
            }
            supportedLanguageVersions = languageVersions.toArray(new LanguageVersion[languageVersions.size()]);
        }
        return supportedLanguageVersions;
    }
}
