/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.Properties;

import net.sourceforge.pmd.lang.java.JavaLanguageModule;
import net.sourceforge.pmd.lang.java.internal.JavaLanguageProperties;

public class JavaTokenizer extends net.sourceforge.pmd.lang.java.cpd.JavaCpdLexer implements Tokenizer {
    public JavaTokenizer(Properties properties) {
        super(convertLanguageProperties(properties));
    }

    public static final String IGNORE_LITERALS = "ignore_literals";
    public static final String IGNORE_IDENTIFIERS = "ignore_identifiers";
    public static final String IGNORE_ANNOTATIONS = "ignore_annotations";

    private static JavaLanguageProperties convertLanguageProperties(Properties properties) {
        boolean ignoreAnnotations = Boolean.parseBoolean(properties.getProperty(IGNORE_ANNOTATIONS, "false"));
        boolean ignoreLiterals = Boolean.parseBoolean(properties.getProperty(IGNORE_LITERALS, "false"));
        boolean ignoreIdentifiers = Boolean.parseBoolean(properties.getProperty(IGNORE_IDENTIFIERS, "false"));

        JavaLanguageProperties javaLanguageProperties = (JavaLanguageProperties) JavaLanguageModule.getInstance().newPropertyBundle();
        javaLanguageProperties.setProperty(CpdLanguageProperties.CPD_IGNORE_METADATA, ignoreAnnotations);
        javaLanguageProperties.setProperty(CpdLanguageProperties.CPD_ANONYMIZE_LITERALS, ignoreLiterals);
        javaLanguageProperties.setProperty(CpdLanguageProperties.CPD_ANONYMIZE_IDENTIFIERS, ignoreIdentifiers);

        return javaLanguageProperties;
    }
}
