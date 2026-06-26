/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin;

import net.sourceforge.pmd.cpd.CpdCapableLanguage;
import net.sourceforge.pmd.cpd.CpdLexer;
import net.sourceforge.pmd.lang.LanguageModuleBase;
import net.sourceforge.pmd.lang.LanguageProcessor;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.PmdCapableLanguage;
import net.sourceforge.pmd.lang.impl.SimpleLanguageModuleBase;
import net.sourceforge.pmd.lang.kotlin.cpd.KotlinCpdLexer;

/**
 * Language Module for Kotlin
 */
public class KotlinLanguageModule extends SimpleLanguageModuleBase implements PmdCapableLanguage, CpdCapableLanguage {
    private static final String ID = "kotlin";

    public KotlinLanguageModule() {
        super(LanguageModuleBase.LanguageMetadata.withId(ID).name("Kotlin")
                              .extensions("kt", "ktm")
                              .addVersion("1.6")
                              .addVersion("1.7")
                              .addDefaultVersion("1.8")
                              // Newer versions are informational only.
                              // Unlike Java, we don't restrict parsing based on selected language version.
                              .addVersion("2.2.0"),
                (LanguageVersionHandler) null); // for backwards compatibility we need to extend SimpleLanguageModuleBase
                // the KotlinHandler is created later via #createProcessor by KotlinLanguageProcessor
                // TODO: PMD 8: extend LanguageModuleBase instead of SimpleLanguageModuleBase
    }

    @Override
    public LanguagePropertyBundle newPropertyBundle() {
        return new KotlinLanguageProperties(this);
    }

    public static KotlinLanguageModule getInstance() {
        return (KotlinLanguageModule) LanguageRegistry.PMD.getLanguageById(ID);
    }

    @Override
    public LanguageProcessor createProcessor(LanguagePropertyBundle bundle) {
        return new KotlinLanguageProcessor((KotlinLanguageProperties) bundle);
    }

    @Override
    public CpdLexer createCpdLexer(LanguagePropertyBundle bundle) {
        return new KotlinCpdLexer();
    }
}
