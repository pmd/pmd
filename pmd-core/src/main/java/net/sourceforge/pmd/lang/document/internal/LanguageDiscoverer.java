/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.document.internal;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageRegistry;

/**
 * Discovers the languages applicable to a file.
 */
public class LanguageDiscoverer {


    private final Language forcedLanguage;

    /**
     * Build a new instance.
     *
     * @param forcedLanguage If non-null, all files will be assigned this language.
     */
    public LanguageDiscoverer(Language forcedLanguage) {
        this.forcedLanguage = forcedLanguage;
    }

    /**
     * Get the Languages of a given source file.
     *
     * @param sourceFile The file.
     *
     * @return The Languages for the source file, may be empty.
     */
    public List<Language> getLanguagesForFile(Path sourceFile) {
        return getLanguagesForFile(sourceFile.getFileName().toString());
    }

    /**
     * Get the Languages of a given source file.
     *
     * @param fileName The file name.
     *
     * @return The Languages for the source file, may be empty.
     */
    public List<Language> getLanguagesForFile(String fileName) {
        if (forcedLanguage != null) {
            return Collections.singletonList(forcedLanguage);
        }
        String extension = getExtension(fileName);
        return LanguageRegistry.findByExtension(extension);
    }

    // Get the extensions from a file
    private String getExtension(String fileName) {
        return StringUtils.substringAfterLast(fileName, ".");
    }
}
