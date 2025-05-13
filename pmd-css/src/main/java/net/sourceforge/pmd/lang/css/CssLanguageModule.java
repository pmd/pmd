/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.css;

import net.sourceforge.pmd.cpd.CpdLexer;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.css.cpd.CssCpdLexer;
import net.sourceforge.pmd.lang.impl.CpdOnlyLanguageModuleBase;

public class CssLanguageModule extends CpdOnlyLanguageModuleBase {
    private static final String ID = "css";

    public CssLanguageModule() {
        super(LanguageMetadata.withId(ID).name("Css").extensions("css"));
    }

    public static CssLanguageModule getInstance() {
        return (CssLanguageModule) LanguageRegistry.CPD.getLanguageById(ID);
    }

    @Override
    public CpdLexer createCpdLexer(LanguagePropertyBundle bundle) {
        return new CssCpdLexer();
    }
}
