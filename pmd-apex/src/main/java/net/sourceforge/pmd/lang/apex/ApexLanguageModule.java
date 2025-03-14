/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex;

import net.sourceforge.pmd.cpd.CpdCapableLanguage;
import net.sourceforge.pmd.cpd.CpdLexer;
import net.sourceforge.pmd.lang.LanguageModuleBase;
import net.sourceforge.pmd.lang.LanguageProcessor;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.PmdCapableLanguage;
import net.sourceforge.pmd.lang.apex.cpd.ApexCpdLexer;

public class ApexLanguageModule extends LanguageModuleBase implements PmdCapableLanguage, CpdCapableLanguage {
    private static final String ID = "apex";

    private static final ApexLanguageModule INSTANCE = new ApexLanguageModule();

    public ApexLanguageModule() {
        super(LanguageMetadata.withId(ID).name("Apex")
                              .extensions("cls", "trigger")
                              .addVersion("52")
                              .addVersion("53")
                              .addVersion("54")
                              .addVersion("55")
                              .addVersion("56")
                              .addVersion("57")
                              .addVersion("58")
                              .addVersion("59")
                              .addDefaultVersion("60"));
    }

    public static ApexLanguageModule getInstance() {
        // note: can't load this language from registry, since VfLanguageModule requires
        // an instance of ApexLanguageModule during construction (VfLanguageModule depends on ApexLanguageModule).
        return INSTANCE;
    }

    @Override
    public ApexLanguageProperties newPropertyBundle() {
        return new ApexLanguageProperties();
    }

    @Override
    public LanguageProcessor createProcessor(LanguagePropertyBundle bundle) {
        return new ApexLanguageProcessor((ApexLanguageProperties) bundle);
    }

    @Override
    public CpdLexer createCpdLexer(LanguagePropertyBundle bundle) {
        return new ApexCpdLexer();
    }
}
