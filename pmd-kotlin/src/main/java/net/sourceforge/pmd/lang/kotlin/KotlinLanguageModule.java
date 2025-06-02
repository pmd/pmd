/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin;

import net.sourceforge.pmd.cpd.CpdLexer;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.impl.SimpleLanguageModuleBase;
import net.sourceforge.pmd.lang.kotlin.cpd.KotlinCpdLexer;

/**
 * Language Module for Kotlin
 */
public class KotlinLanguageModule extends SimpleLanguageModuleBase {
    private static final String ID = "kotlin";

    public KotlinLanguageModule() {
        super(LanguageMetadata.withId(ID).name("Kotlin")
                              .extensions("kt", "ktm")
                              .addVersion("1.6")
                              .addVersion("1.7")
                              .addDefaultVersion("1.8"),
              new KotlinHandler());

    }

    public static KotlinLanguageModule getInstance() {
        return (KotlinLanguageModule) LanguageRegistry.PMD.getLanguageById(ID);
    }

    @Override
    public CpdLexer createCpdLexer(LanguagePropertyBundle bundle) {
        return new KotlinCpdLexer();
    }
}
