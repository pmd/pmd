/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.julia;

import net.sourceforge.pmd.cpd.Tokenizer;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.impl.CpdOnlyLanguageModuleBase;
import net.sourceforge.pmd.lang.julia.cpd.JuliaTokenizer;

/**
 * Language implementation for Julia.
 */
public class JuliaLanguageModule extends CpdOnlyLanguageModuleBase {

    /**
     * Creates a new Julia Language instance.
     */
    public JuliaLanguageModule() {
        super(LanguageMetadata.withId("julia").name("Julia").extensions("jl"));
    }

    @Override
    public Tokenizer createCpdTokenizer(LanguagePropertyBundle bundle) {
        return new JuliaTokenizer();
    }

    public static JuliaLanguageModule getInstance() {
        return (JuliaLanguageModule) LanguageRegistry.CPD.getLanguageById("julia");
    }
}
