/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;
import net.sourceforge.pmd.util.log.PmdReporter;

class LanguageProcessorRegistryTest {
    @Test
    void loadEnvironmentVariables() throws Exception {
        Map<String, String> env = new HashMap<>();
        env.put("PMD_DUMMY_ROOT_DIRECTORY", "theValue");

        Language dummyLanguage = DummyLanguageModule.getInstance().getDefaultVersion().getLanguage();
        LanguageRegistry languageRegistry = LanguageRegistry.singleton(dummyLanguage);

        Map<Language, LanguagePropertyBundle> languageProperties = new HashMap<>();
        DummyLanguagePropertyBundle bundle = new DummyLanguagePropertyBundle(dummyLanguage);
        languageProperties.put(dummyLanguage, bundle);

        try (LanguageProcessorRegistry ignored = LanguageProcessorRegistry.create(languageRegistry, languageProperties, PmdReporter.quiet(), env)) {
            assertEquals("theValue", bundle.getRootDirectory());
        }
    }

    private static class DummyLanguagePropertyBundle extends LanguagePropertyBundle {
        private static final PropertyDescriptor<String> ROOT_DIRECTORY = PropertyFactory.stringProperty("rootDirectory")
                .desc("Test")
                .defaultValue("")
                .build();

        DummyLanguagePropertyBundle(Language language) {
            super(language);
            definePropertyDescriptor(ROOT_DIRECTORY);
        }

        public String getRootDirectory() {
            return getProperty(ROOT_DIRECTORY);
        }
    }
}
