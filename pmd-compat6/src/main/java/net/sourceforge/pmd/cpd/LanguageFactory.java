/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

// This class is here just to make maven-pmd-plugin compile with
// pmd 7.0.0 including this compat6 module.
// It would only be used, if a custom language (other than java, jsp or javascript)
// would be requested.

package net.sourceforge.pmd.cpd;

import java.util.Properties;
import java.util.stream.Collectors;

import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.properties.PropertyDescriptor;

public final class LanguageFactory {
    private LanguageFactory() {
        // utility class
    }

    public static Language createLanguage(String name, Properties properties) {
        CpdCapableLanguage cpdLanguage = (CpdCapableLanguage) LanguageRegistry.CPD.getLanguageById(name);
        if (cpdLanguage != null) {
            return new CpdLanguageAdapter(cpdLanguage, properties);
        }
        throw new UnsupportedOperationException("Language " + name + " is not supported");
    }

    public static class CpdLanguageAdapter extends AbstractLanguage {
        private CpdCapableLanguage language;

        public CpdLanguageAdapter(CpdCapableLanguage cpdCapableLanguage, Properties properties) {
            super(cpdCapableLanguage.getName(), cpdCapableLanguage.getId(), createLexer(cpdCapableLanguage, properties), convertExtensions(cpdCapableLanguage));
            this.language = cpdCapableLanguage;
        }

        private static Tokenizer createLexer(CpdCapableLanguage cpdCapableLanguage, Properties properties) {
            LanguagePropertyBundle propertyBundle = cpdCapableLanguage.newPropertyBundle();
            for (String propName : properties.stringPropertyNames()) {
                PropertyDescriptor<?> propertyDescriptor = propertyBundle.getPropertyDescriptor(propName);
                if (propertyDescriptor != null) {
                    setProperty(propertyBundle, propertyDescriptor, properties.getProperty(propName));
                }
            }
            CpdLexer cpdLexer = cpdCapableLanguage.createCpdLexer(propertyBundle);
            return cpdLexer::tokenize;
        }

        private static <T> void setProperty(LanguagePropertyBundle propertyBundle, PropertyDescriptor<T> propertyDescriptor, String stringValue) {
            T value = propertyDescriptor.serializer().fromString(stringValue);
            propertyBundle.setProperty(propertyDescriptor, value);
        }

        private static String[] convertExtensions(CpdCapableLanguage cpdCapableLanguage) {
            return cpdCapableLanguage.getExtensions().stream().map(s -> "." + s).collect(Collectors.toList()).toArray(new String[0]);
        }

        public CpdCapableLanguage getLanguage() {
            return language;
        }
    }
}
