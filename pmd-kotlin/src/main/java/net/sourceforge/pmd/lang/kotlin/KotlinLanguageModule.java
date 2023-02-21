/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.impl.SimpleLanguageModuleBase;

/**
 * Language Module for Kotlin
 *
 * <p>Note: Kotlin support is considered an experimental feature. The AST structure might change.</p>
 */
@Experimental
public class KotlinLanguageModule extends SimpleLanguageModuleBase {

    /** The name. */
    public static final String NAME = "Kotlin";
    /** The terse name. */
    public static final String TERSE_NAME = "kotlin";

    /**
     * Create a new instance of Kotlin Language Module.
     */
    public KotlinLanguageModule() {
        super(LanguageMetadata.withId(TERSE_NAME).name(NAME).extensions("kt", "ktm")
                              .addDefaultVersion("1.6-rfc+0.1", "1.6"),
              new KotlinHandler());

    }
}
