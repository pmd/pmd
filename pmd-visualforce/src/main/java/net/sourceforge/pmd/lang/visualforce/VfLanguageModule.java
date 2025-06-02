/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.visualforce;

import net.sourceforge.pmd.cpd.CpdCapableLanguage;
import net.sourceforge.pmd.cpd.CpdLexer;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.apex.ApexLanguageModule;
import net.sourceforge.pmd.lang.impl.SimpleLanguageModuleBase;
import net.sourceforge.pmd.lang.visualforce.cpd.VfCpdLexer;

/**
 * @author sergey.gorbaty
 */
public class VfLanguageModule extends SimpleLanguageModuleBase implements CpdCapableLanguage {
    static final String ID = "visualforce";
    static final String NAME = "Salesforce Visualforce";

    public VfLanguageModule() {
        super(LanguageMetadata.withId(ID).name(NAME)
                              .extensions("page", "component")
                              .dependsOnLanguage(ApexLanguageModule.getInstance().getId())
                              .addAllVersionsOf(ApexLanguageModule.getInstance()),
              p -> new VfHandler((VfLanguageProperties) p));
    }

    public static VfLanguageModule getInstance() {
        return (VfLanguageModule) LanguageRegistry.PMD.getLanguageById(ID);
    }

    @Override
    public CpdLexer createCpdLexer(LanguagePropertyBundle bundle) {
        return new VfCpdLexer();
    }

    @Override
    public LanguagePropertyBundle newPropertyBundle() {
        return new VfLanguageProperties();
    }
}
