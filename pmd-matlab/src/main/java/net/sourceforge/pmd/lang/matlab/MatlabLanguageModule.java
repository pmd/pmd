/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.matlab;

import net.sourceforge.pmd.cpd.CpdLexer;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.impl.CpdOnlyLanguageModuleBase;
import net.sourceforge.pmd.lang.matlab.cpd.MatlabCpdLexer;

/**
 * Defines the Language module for Matlab
 */
public class MatlabLanguageModule extends CpdOnlyLanguageModuleBase {
    private static final String ID = "matlab";

    /**
     * Creates a new instance of {@link MatlabLanguageModule} with the default
     * extensions for matlab files.
     */
    public MatlabLanguageModule() {
        super(LanguageMetadata.withId(ID).name("Matlab").extensions("m"));
    }

    public static MatlabLanguageModule getInstance() {
        return (MatlabLanguageModule) LanguageRegistry.CPD.getLanguageById(ID);
    }

    @Override
    public CpdLexer createCpdLexer(LanguagePropertyBundle bundle) {
        return new MatlabCpdLexer();
    }
}
