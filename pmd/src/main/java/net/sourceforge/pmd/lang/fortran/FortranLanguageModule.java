package net.sourceforge.pmd.lang.fortran;

import net.sourceforge.pmd.lang.BaseLanguageModule;
import net.sourceforge.pmd.lang.ecmascript.rule.EcmascriptRuleChainVisitor;

/**
 * Created by christoferdutz on 20.09.14.
 */
public class FortranLanguageModule extends BaseLanguageModule {

    public static final String NAME = "Fortran";

    public FortranLanguageModule() {
        super(NAME, null, "fortran", EcmascriptRuleChainVisitor.class, "for", "f", "f66", "f77", "f90");
        addVersion("", null, true);
    }

}
