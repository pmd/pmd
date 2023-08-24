/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.cs;

import net.sourceforge.pmd.cpd.CpdLanguageProperties;
import net.sourceforge.pmd.cpd.Tokenizer;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.cs.cpd.CsTokenizer;
import net.sourceforge.pmd.lang.impl.CpdOnlyLanguageModuleBase;

/**
 * Defines the Language module for C#.
 */
public class CsLanguageModule extends CpdOnlyLanguageModuleBase {

    public CsLanguageModule() {
        super(LanguageMetadata.withId("cs")
                              .name("C#")
                              .addDefaultVersion("any")
                              .extensions("cs"));
    }

    public static CsLanguageModule getInstance() {
        return (CsLanguageModule) LanguageRegistry.CPD.getLanguageById("cs");
    }

    @Override
    public LanguagePropertyBundle newPropertyBundle() {
        LanguagePropertyBundle bundle = super.newPropertyBundle();
        bundle.definePropertyDescriptor(CpdLanguageProperties.CPD_IGNORE_LITERAL_SEQUENCES);
        bundle.definePropertyDescriptor(CpdLanguageProperties.CPD_IGNORE_IMPORTS);
        bundle.definePropertyDescriptor(CpdLanguageProperties.CPD_IGNORE_METADATA);
        return bundle;
    }

    @Override
    public Tokenizer createCpdTokenizer(LanguagePropertyBundle bundle) {
        return new CsTokenizer(bundle);
    }
}
