/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin;

import static net.sourceforge.pmd.properties.NumericConstraints.positive;

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;

/**
 * Language properties for Kotlin.
 */
public class KotlinLanguageProperties extends LanguagePropertyBundle {

    public static final PropertyDescriptor<Integer> PARSE_TIMEOUT_SECONDS =
        PropertyFactory.intProperty("parseTimeoutSeconds")
                       .desc("Per-file parse timeout in seconds. Files exceeding this limit are skipped with a warning.")
                       .defaultValue(30)
                       .require(positive())
                       .build();

    public KotlinLanguageProperties(Language language) {
        super(language);
        definePropertyDescriptor(PARSE_TIMEOUT_SECONDS);
    }

    public int getParseTimeoutSeconds() {
        return getProperty(PARSE_TIMEOUT_SECONDS);
    }
}
