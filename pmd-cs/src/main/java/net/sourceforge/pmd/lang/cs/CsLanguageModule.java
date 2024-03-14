/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.cs;

import net.sourceforge.pmd.cpd.CpdLanguageProperties;
import net.sourceforge.pmd.cpd.CpdLexer;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.cs.cpd.CsCpdLexer;
import net.sourceforge.pmd.lang.impl.CpdOnlyLanguageModuleBase;

/**
 * Defines the Language module for C#.
 */
public class CsLanguageModule extends CpdOnlyLanguageModuleBase {
    private static final String ID = "cs";

    public CsLanguageModule() {
        super(LanguageMetadata.withId(ID)
                              .name("C#")
                              .addDefaultVersion("any")
                              .extensions("cs"));
    }

    public static CsLanguageModule getInstance() {
        return (CsLanguageModule) LanguageRegistry.CPD.getLanguageById(ID);
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
    public CpdLexer createCpdLexer(LanguagePropertyBundle bundle) {
        return new CsCpdLexer(bundle);
    }
}
