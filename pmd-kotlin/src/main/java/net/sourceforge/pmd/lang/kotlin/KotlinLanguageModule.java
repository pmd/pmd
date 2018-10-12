/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin;

import net.sourceforge.pmd.lang.BaseLanguageModule;

/**
 * Language Module for Kotlin
 */
public class KotlinLanguageModule extends BaseLanguageModule {

    /** The name. */
    public static final String NAME = "Kotlin";
    /** The terse name. */
    public static final String TERSE_NAME = "kotlin";

    /**
     * Create a new instance of Kotlin Language Module.
     */
    public KotlinLanguageModule() {
        super(NAME, null, TERSE_NAME, null, "kotlin");
        addVersion("", null, true);
    }
}
