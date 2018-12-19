/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

/**
 * Language implementation for Kotlin
 */
public class KotlinLanguage extends AbstractLanguage {

    /**
     * Creates a new Kotlin Language instance.
     */
    public KotlinLanguage() {
        super("Kotlin", "kotlin", new KotlinTokenizer(), ".kt");
    }
}
