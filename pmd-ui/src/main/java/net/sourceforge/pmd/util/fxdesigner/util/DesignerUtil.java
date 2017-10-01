/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.Paragraph;

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


    /** Length in characters before the specified position. */
    public static int lengthUntil(int line, int column, CodeArea codeArea) {
        List<Paragraph<Collection<String>>> paragraphs = codeArea.getParagraphs();
        int length = 0;
        for (int i = 0; i < line - 1; i++) {
            length += paragraphs.get(i).length() + 1;
        }
        return length + column - 1;
    }
}
