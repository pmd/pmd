/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import net.sourceforge.pmd.lang.kotlin.KotlinLanguageModule;

/**
 * Language implementation for Kotlin
 */
public class KotlinLanguage extends AbstractLanguage {

    /**
     * Creates a new Kotlin Language instance.
     */
    public KotlinLanguage() {
        super(KotlinLanguageModule.NAME, KotlinLanguageModule.TERSE_NAME, new KotlinTokenizer(), KotlinLanguageModule.EXTENSIONS);
    }
}
