/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;

import java.util.List;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.annotation.InternalApi;
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

    @InternalApi
    public static final List<String> EXTENSIONS = listOf("kt", "ktm");

    /**
     * Create a new instance of Kotlin Language Module.
     */
    public KotlinLanguageModule() {
        super(LanguageMetadata.withId(TERSE_NAME).name(NAME)
                              .extensions(EXTENSIONS)
                              .addVersion("1.6")
                              .addVersion("1.7")
                              .addDefaultVersion("1.8"),
              new KotlinHandler());
    }
}
