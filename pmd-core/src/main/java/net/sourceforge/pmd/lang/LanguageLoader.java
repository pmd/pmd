/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.TreeSet;

/**
 * A language loader creates {@link LanguageRegistry} instances by
 * asking a {@link ServiceLoader} for {@link Language} implementations.
 */
public final class LanguageLoader {

    public static final LanguageLoader DEFAULT = new LanguageLoader(LanguageLoader.class.getClassLoader());

    private final Set<Language> languages;
    private final List<String> languageIds;

    private LanguageLoader(ClassLoader classLoader) {
        // sort languages by terse name. Avoiding differences in the order of languages
        // across JVM versions / OS.
        Set<Language> sortedLangs = new TreeSet<>(Comparator.comparing(Language::getTerseName, String::compareToIgnoreCase));
        List<String> languageIds = new ArrayList<>();
        ServiceLoader<Language> languageLoader = ServiceLoader.load(Language.class, classLoader);
        Iterator<Language> iterator = languageLoader.iterator();
        while (true) {
            // this loop is weird, but both hasNext and next may throw ServiceConfigurationError,
            // it's more robust that way
            try {
                if (iterator.hasNext()) {
                    Language language = iterator.next();
                    sortedLangs.add(language);
                    languageIds.add(language.getTerseName());
                } else {
                    break;
                }
            } catch (UnsupportedClassVersionError | ServiceConfigurationError e) {
                // Some languages require java8 and are therefore only available
                // if java8 or later is used as runtime.
                System.err.println("Ignoring language for PMD: " + e.toString());
            }
        }
        languageIds.sort(String::compareToIgnoreCase);
        this.languageIds = Collections.unmodifiableList(languageIds);
        this.languages = Collections.unmodifiableSet(new LinkedHashSet<>(sortedLangs));
    }

    public LanguageRegistry load() { // TODO add properties parameter to implement language properties
        return new LanguageRegistry(this.languages);
    }

    public List<String> availableLanguageIds() {
        return languageIds;
    }


    /**
     * Create a new language loader, using the given ClassLoader to
     * load PMD languages (through a {@link ServiceLoader}). If you
     * want to use pmd's own classloader, you may use {@link #DEFAULT}.
     *
     * @param classLoader Classloader to look for PMD modules to load
     *
     * @return A new language registry
     */
    public static LanguageLoader fromClassLoader(ClassLoader classLoader) {
        return new LanguageLoader(classLoader);
    }
}
