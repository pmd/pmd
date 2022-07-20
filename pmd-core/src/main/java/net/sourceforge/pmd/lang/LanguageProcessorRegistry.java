/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertySource;
import net.sourceforge.pmd.util.IOUtil;
import net.sourceforge.pmd.util.log.MessageReporter;

/**
 * Stores all currently initialized {@link LanguageProcessor}s during analysis.
 *
 * @author Clément Fournier
 */
public class LanguageProcessorRegistry implements Iterable<Language>, AutoCloseable {

    private final Map<Language, LanguageProcessor> processors;


    private LanguageProcessorRegistry(Map<Language, LanguageProcessor> processors) {
        this.processors = Collections.unmodifiableMap(processors);
    }

    public @NonNull LanguageProcessor getProcessor(Language l) {
        return Objects.requireNonNull(processors.get(l));
    }

    @Override
    public void close() throws LanguageTerminationException {
        Exception e = IOUtil.closeAll(processors.values());
        if (e != null) {
            throw new LanguageTerminationException(e);
        }
    }

    @Override
    public Iterator<Language> iterator() {
        return processors.keySet().iterator();
    }

    public static LanguageProcessorRegistry create(LanguageRegistry registry,
                                                   Map<Language, Properties> languageProperties,
                                                   MessageReporter messageReporter) {
        Map<Language, LanguageProcessor> processors = new HashMap<>();
        for (Language language : registry) {
            LanguagePropertyBundle properties = language.newPropertyBundle();
            setLanguageProperties(languageProperties, messageReporter, language, properties);
            try {
                LanguageProcessor processor = language.createProcessor(properties);
                processors.put(language, processor);
            } catch (IllegalArgumentException e) {
                messageReporter.error(e); // todo
            }
        }

        return new LanguageProcessorRegistry(processors);
    }

    private static void setLanguageProperties(Map<Language, Properties> languageProperties, MessageReporter messageReporter, Language language, LanguagePropertyBundle properties) {
        Properties props = languageProperties.get(language);
        if (props != null) {
            props.forEach((k, v) -> {
                PropertyDescriptor<?> descriptor = properties.getPropertyDescriptor(k.toString());
                if (descriptor == null) {
                    messageReporter.error("No property {0} for language {1}", k, language.getId());
                    return;
                }

                trySetPropertyCapture(properties, descriptor, v.toString(), messageReporter);
            });
        }
    }


    private static <T> void trySetPropertyCapture(PropertySource source,
                                                  PropertyDescriptor<T> propertyDescriptor,
                                                  String propertyValue,
                                                  MessageReporter reporter) {
        try {
            T value = propertyDescriptor.valueFrom(propertyValue);
            source.setProperty(propertyDescriptor, value);
        } catch (IllegalArgumentException e) {
            reporter.error("Cannot set property {0} to {1}: {2}",
                           propertyDescriptor.name(),
                           propertyValue,
                           e.getMessage());
        }
    }


    public static class LanguageTerminationException extends RuntimeException {
        public LanguageTerminationException(Throwable cause) {
            super(cause);
        }
    }
}
