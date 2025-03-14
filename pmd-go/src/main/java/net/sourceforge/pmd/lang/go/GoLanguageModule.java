/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.go;

import net.sourceforge.pmd.cpd.CpdLexer;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.go.cpd.GoCpdLexer;
import net.sourceforge.pmd.lang.impl.CpdOnlyLanguageModuleBase;

public class GoLanguageModule extends CpdOnlyLanguageModuleBase {
    private static final String ID = "go";

    public GoLanguageModule() {
        super(LanguageMetadata.withId(ID).name("Go").extensions("go"));
    }

    public static GoLanguageModule getInstance() {
        return (GoLanguageModule) LanguageRegistry.CPD.getLanguageById(ID);
    }

    @Override
    public CpdLexer createCpdLexer(LanguagePropertyBundle bundle) {
        return new GoCpdLexer();
    }
}
