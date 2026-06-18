/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin;

import static net.sourceforge.pmd.properties.NumericConstraints.positive;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.JvmLanguagePropertyBundle;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;

/**
 * Language properties for Kotlin.
 * @since 7.25.0
 */
public class KotlinLanguageProperties extends JvmLanguagePropertyBundle {

    /**
     * @since 7.25.0
     * @experimental might be moved to pmd-core in the future
     */
    @Experimental
    public static final PropertyDescriptor<Integer> PARSE_TIMEOUT_SECONDS =
        PropertyFactory.intProperty("xParseTimeoutSeconds")
                       .desc("Per-file parse timeout in seconds. Files exceeding this limit are skipped with a processing error.")
                       .defaultValue(30)
                       .require(positive())
                       .build();

    public KotlinLanguageProperties(Language language) {
        super(language);
        definePropertyDescriptor(PARSE_TIMEOUT_SECONDS);
    }

    /**
     * @since 7.25.0
     * @experimental See {@link #PARSE_TIMEOUT_SECONDS}
     */
    @Experimental
    public int getParseTimeoutSeconds() {
        return getProperty(PARSE_TIMEOUT_SECONDS);
    }
}
