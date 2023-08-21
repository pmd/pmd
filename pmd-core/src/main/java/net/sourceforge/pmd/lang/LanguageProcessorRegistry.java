/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import static net.sourceforge.pmd.util.StringUtil.CaseConvention.SCREAMING_SNAKE_CASE;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.internal.util.IOUtil;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertySource;
import net.sourceforge.pmd.util.CollectionUtil;
import net.sourceforge.pmd.util.StringUtil.CaseConvention;
import net.sourceforge.pmd.util.log.MessageReporter;

/**
 * Stores all currently initialized {@link LanguageProcessor}s during analysis.
 *
 * @author Clément Fournier
 */
public final class LanguageProcessorRegistry implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(LanguageProcessorRegistry.class);


    private final Map<Language, LanguageProcessor> processors;
    private final LanguageRegistry languages;


    private LanguageProcessorRegistry(Set<LanguageProcessor> processors) {
        this.processors = Collections.unmodifiableMap(
            CollectionUtil.associateBy(processors, LanguageProcessor::getLanguage)
        );
        this.languages = new LanguageRegistry(this.processors.keySet());

        for (Language language : languages.getLanguages()) {
            for (String id : language.getDependencies()) {
                if (languages.getLanguageById(id) == null) {
                    throw new IllegalStateException(
                        "Language " + language.getId() + " has unsatisfied dependencies: " + id + " is not loaded"
                    );
                }
            }
        }
    }

    /**
     * Return the languages that are registered in this instance.
     */
    public LanguageRegistry getLanguages() {
        return languages;
    }

    /**
     * Return the processor for a given language.
     *
     * @param l a language
     *
     * @throws IllegalArgumentException if the language is not part of this registry
     */
    public @NonNull LanguageProcessor getProcessor(Language l) {
        LanguageProcessor obj = processors.get(l);
        if (obj == null) {
            throw new IllegalArgumentException("Language " + l.getId() + " is not initialized in " + this);
        }
        return obj;
    }

    /**
     * Close all processors in this registry.
     *
     * @throws LanguageTerminationException If closing any of the processors threw something
     */
    @Override
    public void close() throws LanguageTerminationException {
        Exception e = IOUtil.closeAll(processors.values());
        if (e != null) {
            throw new LanguageTerminationException(e);
        }
    }

    /**
     * Create a registry with a single language processor.
     *
     * @throws IllegalStateException If the language depends on other languages,
     *                               as they are then not included in this registry (see
     *                               {@link Language#getDependencies()}).
     */
    public static LanguageProcessorRegistry singleton(@NonNull LanguageProcessor lp) {
        return new LanguageProcessorRegistry(Collections.singleton(lp));
    }

    /**
     * Create a new instance by creating a processor for each language in
     * the given language registry. Each processor is created using the property
     * bundle that is in the map, if present. Language properties are defaulted
     * to environment variables if they are not already overridden.
     *
     * @throws IllegalStateException    If any language in the registry depends on
     *                                  languages that are not found in it, or that
     *                                  could not be instantiated (see {@link Language#getDependencies()}).
     * @throws IllegalArgumentException If some entry in the map maps a language
     *                                  to an incompatible property bundle
     */
    public static LanguageProcessorRegistry create(LanguageRegistry registry,
                                                   Map<Language, LanguagePropertyBundle> languageProperties,
                                                   MessageReporter messageReporter) {
        Set<LanguageProcessor> processors = new HashSet<>();
        for (Language language : registry) {
            LanguagePropertyBundle properties = languageProperties.getOrDefault(language, language.newPropertyBundle());
            if (!properties.getLanguage().equals(language)) {
                throw new IllegalArgumentException("Mismatched language");
            }

            try {
                //
                readLanguagePropertiesFromEnv(properties, messageReporter);

                processors.add(language.createProcessor(properties));
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

    private static void readLanguagePropertiesFromEnv(LanguagePropertyBundle props, MessageReporter reporter) {
        for (PropertyDescriptor<?> propertyDescriptor : props.getPropertyDescriptors()) {

            String envVarName = getEnvironmentVariableName(props.getLanguage(), propertyDescriptor);
            String propertyValue = System.getenv(envVarName);

            if (propertyValue != null) {
                if (props.isPropertyOverridden(propertyDescriptor)) {
                    // Env vars are a default, they don't override other ways to set properties.
                    // If the property has already been set, don't set it.
                    LOG.debug(
                        "Property {} for lang {} is already set, ignoring environment variable {}={}",
                        propertyDescriptor.name(),
                        props.getLanguage().getId(),
                        envVarName,
                        propertyValue
                    );
                } else {
                    LOG.debug(
                        "Property {} for lang {} is not yet set, using environment variable {}={}",
                        propertyDescriptor.name(),
                        props.getLanguage().getId(),
                        envVarName,
                        propertyValue
                    );
                    trySetPropertyCapture(props, propertyDescriptor, propertyValue, reporter);
                }
            }
        }
    }

    /**
     * Returns the environment variable name that a user can set in order to override the default value.
     */
    private static String getEnvironmentVariableName(Language lang, PropertyDescriptor<?> propertyDescriptor) {
        return "PMD_" + lang.getId().toUpperCase(Locale.ROOT) + "_"
            + CaseConvention.CAMEL_CASE.convertTo(SCREAMING_SNAKE_CASE, propertyDescriptor.name());
    }


    @Override
    public String toString() {
        return "LanguageProcessorRegistry("
            + new LanguageRegistry(processors.keySet()).commaSeparatedList(Language::getId)
            + ")";
    }

    /**
     * An exception that occurs during the closing of a {@link LanguageProcessor},
     */
    public static class LanguageTerminationException extends RuntimeException {

        public LanguageTerminationException(Throwable cause) {
            super(cause);
        }
    }
}
