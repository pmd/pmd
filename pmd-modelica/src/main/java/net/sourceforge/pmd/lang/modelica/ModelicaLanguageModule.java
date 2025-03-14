/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica;

import net.sourceforge.pmd.cpd.CpdLexer;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.impl.SimpleLanguageModuleBase;
import net.sourceforge.pmd.lang.modelica.cpd.ModelicaCpdLexer;

public class ModelicaLanguageModule extends SimpleLanguageModuleBase {
    private static final String ID = "modelica";

    public ModelicaLanguageModule() {
        super(LanguageMetadata.withId(ID).name("Modelica")
                              .extensions("mo")
                              .addVersion("3.4")
                              .addDefaultVersion("3.5"),
              new ModelicaHandler());
    }

    public static ModelicaLanguageModule getInstance() {
        return (ModelicaLanguageModule) LanguageRegistry.PMD.getLanguageById(ID);
    }

    @Override
    public CpdLexer createCpdLexer(LanguagePropertyBundle bundle) {
        return new ModelicaCpdLexer();
    }
}
