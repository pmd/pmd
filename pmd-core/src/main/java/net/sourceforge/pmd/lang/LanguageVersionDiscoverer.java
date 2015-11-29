/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class can discover the LanguageVersion of a source file.  Further, every
 * Language has a default LanguageVersion, which can be temporarily overridden
 * here.
 */
public class LanguageVersionDiscoverer {
    private Map<Language, LanguageVersion> languageToLanguageVersion = new HashMap<>();

    /**
     * Set the given LanguageVersion as the current default for it's Language.
     * @param languageVersion The new default for the Language.
     * @return The previous default version for the language.
     */
    public LanguageVersion setDefaultLanguageVersion(LanguageVersion languageVersion) {
	LanguageVersion currentLanguageVersion = languageToLanguageVersion.put(languageVersion.getLanguage(),
		languageVersion);
	if (currentLanguageVersion == null) {
	    currentLanguageVersion = languageVersion.getLanguage().getDefaultVersion();
	}
	return currentLanguageVersion;
    }

    /**
     * Get the current default LanguageVersion for the given Language. 
     * @param language The Language.
     * @return The current default version for the language.
     */
    public LanguageVersion getDefaultLanguageVersion(Language language) {
	LanguageVersion languageVersion = languageToLanguageVersion.get(language);
	if (languageVersion == null) {
	    languageVersion = language.getDefaultVersion();
	}
	return languageVersion;
    }

    /**
     * Get the default LanguageVersion for the first Language of a given source file.
     *
     * @param sourceFile The file.
     * @return The currently configured LanguageVersion for the source file,
     * or <code>null</code> if there are no supported Languages for the file.
     */
    public LanguageVersion getDefaultLanguageVersionForFile(File sourceFile) {
	return getDefaultLanguageVersionForFile(sourceFile.getName());
    }

    /**
     * Get the LanguageVersion for the first Language of a source file
     * with the given name.
     *
     * @param fileName The file name.
     * @return The currently configured LanguageVersion for the source file
     * or <code>null</code> if there are no supported Languages for the file.
     */
    public LanguageVersion getDefaultLanguageVersionForFile(String fileName) {
	List<Language> languages = getLanguagesForFile(fileName);
	LanguageVersion languageVersion = null;
	if (!languages.isEmpty()) {
	    languageVersion = getDefaultLanguageVersion(languages.get(0));
	}
	return languageVersion;
    }

    /**
     * Get the Languages of a given source file.
     *
     * @param sourceFile The file.
     * @return The Languages for the source file, may be empty.
     */
    public List<Language> getLanguagesForFile(File sourceFile) {
	return getLanguagesForFile(sourceFile.getName());
    }

    /**
     * Get the Languages of a given source file.
     *
     * @param fileName The file name.
     * @return The Languages for the source file, may be empty.
     */
    public List<Language> getLanguagesForFile(String fileName) {
	String extension = getExtension(fileName);
	return LanguageRegistry.findByExtension(extension);
    }

    // Get the extensions from a file
    private String getExtension(String fileName) {
	String extension = null;
	int extensionIndex = 1 + fileName.lastIndexOf('.');
	if (extensionIndex > 0) {
	    extension = fileName.substring(extensionIndex);
	}
	return extension;
    }
}
