/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * This is an implementation of the {@link FilenameFilter} interface which
 * compares a file against a collection of Languages to see if the any are
 * applicable.
 *
 * @author Pieter_Van_Raemdonck - Application Engineers NV/SA - www.ae.be
 */
public class LanguageFilenameFilter implements FilenameFilter {

	private final Set<Language> languages;

	/**
	 * Create a LanguageFilenameFilter for a single Language.
	 * @param language The Language.
	 */
	public LanguageFilenameFilter(Language language) {
		this(Collections.singleton(language));
	}

	/**
	 * Create a LanguageFilenameFilter for a List of Languages.
	 * @param languages The List of Languages.
	 */
	public LanguageFilenameFilter(Set<Language> languages) {
		this.languages = languages;
	}

	/**
	 * Check if a file should be checked by PMD.
	 * {@inheritDoc}
	 */
	public boolean accept(File dir, String name) {
		// Any source file should have a '.' in its name...
		int lastDotIndex = name.lastIndexOf('.');
		if (lastDotIndex < 0) {
			return false;
		}

		String extension = name.substring(1 + lastDotIndex).toUpperCase();
		for (Language language : languages) {
			for (String ext : language.getExtensions()) {
				if (extension.equalsIgnoreCase(ext)) {
					return true;
				}
			}
		}
		return false;
	}

	public String toString() {
		StringBuilder buffer = new StringBuilder("(Extension is one of: ");
		for (Language language : languages) {
			List<String> extensions = language.getExtensions();
			for (int i = 0; i < extensions.size(); i++) {
				if (i > 0) {
					buffer.append(", ");
				}
				buffer.append(extensions.get(i));
			}
		}
		buffer.append(')');
		return buffer.toString();
	}
}
