/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.fortran;

import net.sourceforge.pmd.cpd.AnyCpdLexer;
import net.sourceforge.pmd.cpd.CpdLexer;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.impl.CpdOnlyLanguageModuleBase;

/**
 * Language implementation for Fortran
 *
 * @author Romain PELISSE belaran@gmail.com
 */
public class FortranLanguageModule extends CpdOnlyLanguageModuleBase {
    private static final String ID = "fortran";

    /**
     * Create a Fortran Language instance.
     */
    public FortranLanguageModule() {
        super(LanguageMetadata.withId(ID).name("Fortran").extensions("for", "f", "f66", "f77", "f90"));
    }

    public static FortranLanguageModule getInstance() {
        return (FortranLanguageModule) LanguageRegistry.CPD.getLanguageById(ID);
    }

    @Override
    public CpdLexer createCpdLexer(LanguagePropertyBundle bundle) {
        return new AnyCpdLexer("!");
    }
}
