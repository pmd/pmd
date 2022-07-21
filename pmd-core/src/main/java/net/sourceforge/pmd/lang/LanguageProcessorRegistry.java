/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import static net.sourceforge.pmd.util.CollectionUtil.mapOf;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
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
public final class LanguageProcessorRegistry implements Iterable<Language>, AutoCloseable {

    private final Map<Language, LanguageProcessor> processors;


    private LanguageProcessorRegistry(Map<Language, LanguageProcessor> processors) {
        this.processors = Collections.unmodifiableMap(processors);
    }

    public @NonNull LanguageProcessor getProcessor(Language l) {
        net.sourceforge.pmd.lang.LanguageProcessor obj = processors.get(l);
        if (obj == null)
            throw new IllegalStateException("Language " + l.getId() + " is not initialized in " + this);
        return obj;
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


    public static LanguageProcessorRegistry singleton(LanguageProcessor lp) {
        return new LanguageProcessorRegistry(mapOf(lp.getLanguage(), lp));
    }

    public static LanguageProcessorRegistry create(LanguageRegistry registry,
                                                   Map<Language, LanguagePropertyBundle> languageProperties,
                                                   MessageReporter messageReporter) {
        Map<Language, LanguageProcessor> processors = new HashMap<>();
        for (Language language : registry) {
            LanguagePropertyBundle properties = languageProperties.getOrDefault(language, language.newPropertyBundle());
            try {
                assert properties.getLanguage().equals(language) : "Mismatched language";

                readLanguagePropertiesFromEnv(properties, messageReporter);

                @SuppressWarnings("PMD.CloseResource")
                LanguageProcessor processor = language.createProcessor(properties);
                processors.put(language, processor);
            } catch (IllegalArgumentException e) {
                messageReporter.error(e); // todo
            }
        }

        return new LanguageProcessorRegistry(processors);
    }

    // TODO this should be reused when implementing the CLI
    public static Map<Language, LanguagePropertyBundle> derivePropertiesFromStrings(
        Map<Language, Properties> stringProperties,
        MessageReporter reporter
    ) {
        Map<Language, LanguagePropertyBundle> typedProperties = new HashMap<>();
        stringProperties.forEach((l, props) -> {
            LanguagePropertyBundle properties = l.newPropertyBundle();
            setLanguageProperties(stringProperties, reporter, l, properties);
        });
        return typedProperties;
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

    // transitional until the CLI supports setting language properties
    @Deprecated
    public static void readLanguagePropertiesFromEnv(LanguagePropertyBundle props, MessageReporter reporter) {
        for (PropertyDescriptor<?> propertyDescriptor : props.getPropertyDescriptors()) {
            String propertyValue = getEnvValue(props.getLanguage().getTerseName(), propertyDescriptor);

            if (propertyValue != null) {
                trySetPropertyCapture(props, propertyDescriptor, propertyValue, reporter);
            }
        }
    }

    /**
     * Returns the environment variable name that a user can set in order to override the default value.
     */
    private static String getEnvironmentVariableName(String langTerseName, PropertyDescriptor<?> propertyDescriptor) {
        if (langTerseName == null) {
            throw new IllegalStateException("Language is null");
        }
        return "PMD_" + langTerseName.toUpperCase(Locale.ROOT) + "_"
            + propertyDescriptor.name().toUpperCase(Locale.ROOT);
    }

    /**
     * @return environment variable that overrides the PropertyDesciptors default value. Returns null if no environment
     *     variable has been set.
     */
    private static String getEnvValue(String langTerseName, PropertyDescriptor<?> propertyDescriptor) {
        // note: since we use environent variables and not system properties,
        // tests override this method.
        return System.getenv(getEnvironmentVariableName(langTerseName, propertyDescriptor));
    }


    @Override
    public String toString() {
        return "LanguageProcessorRegistry(" + new LanguageRegistry(processors.keySet()).commaSeparatedList(Language::getId) +")";
    }

    public static class LanguageTerminationException extends RuntimeException {

        public LanguageTerminationException(Throwable cause) {
            super(cause);
        }
    }
}
