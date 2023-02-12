/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.gherkin;

import net.sourceforge.pmd.cpd.Tokenizer;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.gherkin.cpd.GherkinTokenizer;
import net.sourceforge.pmd.lang.impl.CpdOnlyLanguageModuleBase;

/**
 * Language implementation for Gherkin.
 */
public class GherkinLanguageModule extends CpdOnlyLanguageModuleBase {

    /**
     * Creates a new Gherkin Language instance.
     */
    public GherkinLanguageModule() {
        super(LanguageMetadata.withId("gherkin").name("Gherkin").extensions("feature"));
    }

    @Override
    public Tokenizer createCpdTokenizer(LanguagePropertyBundle bundle) {
        return new GherkinTokenizer();
    }
}
