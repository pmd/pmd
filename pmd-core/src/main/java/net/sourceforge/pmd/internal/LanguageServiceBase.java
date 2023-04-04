/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.internal;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.TreeSet;

import net.sourceforge.pmd.annotation.InternalApi;

@InternalApi
public abstract class LanguageServiceBase<T> {

    protected interface NameExtractor<T> {
        String getName(T language);
    }

    protected final Set<T> languages;
    protected final Map<String, T> languagesByName;
    protected final Map<String, T> languagesByTerseName;

    protected LanguageServiceBase(final Class<T> serviceType, final Comparator<T> comparator,
            final NameExtractor<T> nameExtractor, final NameExtractor<T> terseNameExtractor) {
        Set<T> sortedLangs = new TreeSet<>(comparator);
        // Use current class' classloader instead of the threads context classloader, see https://github.com/pmd/pmd/issues/1788
        ServiceLoader<T> languageLoader = ServiceLoader.load(serviceType, getClass().getClassLoader());
        Iterator<T> iterator = languageLoader.iterator();

        while (true) {
            // this loop is weird, but both hasNext and next may throw ServiceConfigurationError,
            // it's more robust that way
            try {
                if (iterator.hasNext()) {
                    T language = iterator.next();
                    sortedLangs.add(language);
                } else {
                    break;
                }
            } catch (UnsupportedClassVersionError | ServiceConfigurationError e) {
                // Some languages require java8 and are therefore only available
                // if java8 or later is used as runtime.
                System.err.println("Ignoring language for PMD: " + e.toString());
            }
        }

        // using a linked hash map to maintain insertion order
        languages = Collections.unmodifiableSet(new LinkedHashSet<>(sortedLangs));

        // TODO there may be languages with duplicate names
        Map<String, T> byName = new LinkedHashMap<>();
        Map<String, T> byTerseName = new LinkedHashMap<>();
        for (T language : sortedLangs) {
            byName.put(nameExtractor.getName(language), language);
            byTerseName.put(terseNameExtractor.getName(language), language);
        }
        languagesByName = Collections.unmodifiableMap(byName);
        languagesByTerseName = Collections.unmodifiableMap(byTerseName);
    }
}
