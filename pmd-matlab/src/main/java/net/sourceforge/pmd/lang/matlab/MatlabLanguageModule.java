/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.matlab;

import net.sourceforge.pmd.cpd.Tokenizer;
import net.sourceforge.pmd.lang.CpdOnlyLanguageModuleBase;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.matlab.cpd.MatlabTokenizer;

/**
 * Defines the Language module for Matlab
 */
public class MatlabLanguageModule extends CpdOnlyLanguageModuleBase {

    /**
     * Creates a new instance of {@link MatlabLanguageModule} with the default
     * extensions for matlab files.
     */
    public MatlabLanguageModule() {
        super(LanguageMetadata.withId("matlab").name("Matlab").extensions("m"));
    }

    @Override
    public Tokenizer createCpdTokenizer(LanguagePropertyBundle bundle) {
        return new MatlabTokenizer();
    }
}
