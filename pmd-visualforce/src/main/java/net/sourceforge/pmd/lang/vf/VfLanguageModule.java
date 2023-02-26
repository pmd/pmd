/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf;

import net.sourceforge.pmd.cpd.CpdCapableLanguage;
import net.sourceforge.pmd.cpd.Tokenizer;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.apex.ApexLanguageModule;
import net.sourceforge.pmd.lang.impl.SimpleLanguageModuleBase;
import net.sourceforge.pmd.lang.vf.cpd.VfTokenizer;

/**
 * @author sergey.gorbaty
 */
public class VfLanguageModule extends SimpleLanguageModuleBase implements CpdCapableLanguage {

    static final String NAME = "Salesforce VisualForce";
    static final String TERSE_NAME = "vf";
    private static final VfLanguageModule INSTANCE = new VfLanguageModule();

    public VfLanguageModule() {
        super(LanguageMetadata.withId(TERSE_NAME).name(NAME)
                              .extensions("page", "component")
                              .dependsOnLanguage(ApexLanguageModule.getInstance().getId())
                              .addAllVersionsOf(ApexLanguageModule.getInstance()),
              p -> new VfHandler((VfLanguageProperties) p));
    }

    @Override
    public Tokenizer createCpdTokenizer(LanguagePropertyBundle bundle) {
        return new VfTokenizer();
    }

    @Override
    public LanguagePropertyBundle newPropertyBundle() {
        return new VfLanguageProperties();
    }

    public static Language getInstance() {
        return INSTANCE;
    }
}
