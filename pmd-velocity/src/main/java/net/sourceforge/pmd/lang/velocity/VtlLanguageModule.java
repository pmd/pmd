/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.velocity;

import net.sourceforge.pmd.cpd.CpdLexer;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.impl.SimpleLanguageModuleBase;
import net.sourceforge.pmd.lang.velocity.cpd.VtlCpdLexer;

/**
 * Created by christoferdutz on 20.09.14.
 */
public class VtlLanguageModule extends SimpleLanguageModuleBase {
    static final String ID = "velocity";
    static final String NAME = "Velocity Template Language (VTL)";

    public VtlLanguageModule() {
        super(LanguageMetadata.withId(ID).name(NAME)
                              .extensions("vm")
                              .addVersion("2.0")
                              .addVersion("2.1")
                              .addVersion("2.2")
                              .addDefaultVersion("2.3"),
                new VtlHandler());
    }

    public static VtlLanguageModule getInstance() {
        return (VtlLanguageModule) LanguageRegistry.PMD.getLanguageById(ID);
    }

    @Override
    public CpdLexer createCpdLexer(LanguagePropertyBundle bundle) {
        return new VtlCpdLexer();
    }
}
