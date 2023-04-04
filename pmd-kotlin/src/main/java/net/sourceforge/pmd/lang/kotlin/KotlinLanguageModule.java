/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.cpd.Tokenizer;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.impl.SimpleLanguageModuleBase;
import net.sourceforge.pmd.lang.kotlin.cpd.KotlinTokenizer;

/**
 * Language Module for Kotlin
 *
 * <p>Note: Kotlin support is considered an experimental feature. The AST structure might change.</p>
 */
@Experimental
public class KotlinLanguageModule extends SimpleLanguageModuleBase {

    private static final KotlinLanguageModule INSTANCE = new KotlinLanguageModule();

    public KotlinLanguageModule() {
        super(LanguageMetadata.withId("kotlin").name("Kotlin")
                              .extensions("kt", "ktm")
                              .addVersion("1.6")
                              .addVersion("1.7")
                              .addDefaultVersion("1.8"),
              new KotlinHandler());

    }

    @Override
    public Tokenizer createCpdTokenizer(LanguagePropertyBundle bundle) {
        return new KotlinTokenizer();
    }

    public static KotlinLanguageModule getInstance() {
        return INSTANCE;
    }
}
