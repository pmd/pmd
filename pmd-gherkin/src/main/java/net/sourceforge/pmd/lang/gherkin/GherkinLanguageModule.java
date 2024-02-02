/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.gherkin;

import net.sourceforge.pmd.cpd.CpdLexer;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.gherkin.cpd.GherkinCpdLexer;
import net.sourceforge.pmd.lang.impl.CpdOnlyLanguageModuleBase;

/**
 * Language implementation for Gherkin.
 */
public class GherkinLanguageModule extends CpdOnlyLanguageModuleBase {
    private static final String ID = "gherkin";

    /**
     * Creates a new Gherkin Language instance.
     */
    public GherkinLanguageModule() {
        super(LanguageMetadata.withId(ID).name("Gherkin").extensions("feature"));
    }

    public static GherkinLanguageModule getInstance() {
        return (GherkinLanguageModule) LanguageRegistry.CPD.getLanguageById(ID);
    }

    @Override
    public CpdLexer createCpdLexer(LanguagePropertyBundle bundle) {
        return new GherkinCpdLexer();
    }
}
