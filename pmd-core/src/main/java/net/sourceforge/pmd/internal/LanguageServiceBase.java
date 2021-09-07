/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import net.sourceforge.pmd.annotation.InternalApi;

@InternalApi
public abstract class LanguageServiceBase<T> {

    protected interface NameExtractor<T> {
        String getName(T language);
    }

    protected final Map<String, T> languages;

    protected LanguageServiceBase(final Class<T> serviceType, final Comparator<T> comparator,
            final NameExtractor<T> nameExtractor) {
        List<T> languagesList = new ArrayList<>();
        // Use current class' classloader instead of the threads context classloader, see https://github.com/pmd/pmd/issues/1788
        ServiceLoader<T> languageLoader = ServiceLoader.load(serviceType, getClass().getClassLoader());
        Iterator<T> iterator = languageLoader.iterator();

        while (true) {
            // this loop is weird, but both hasNext and next may throw ServiceConfigurationError,
            // it's more robust that way
            try {
                if (iterator.hasNext()) {
                    T language = iterator.next();
                    languagesList.add(language);
                } else {
                    break;
                }
            } catch (UnsupportedClassVersionError | ServiceConfigurationError e) {
                // Some languages require java8 and are therefore only available
                // if java8 or later is used as runtime.
                System.err.println("Ignoring language for PMD: " + e.toString());
            }
        }

        // sort languages by terse name. Avoiding differences in the order of languages
        // across JVM versions / OS.
        Collections.sort(languagesList, comparator);

        // using a linked hash map to maintain insertion order
        languages = new LinkedHashMap<>();
        for (T language : languagesList) {
            languages.put(nameExtractor.getName(language), language);
        }
    }
}
